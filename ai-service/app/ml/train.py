import os
import argparse
import joblib
import logging
import sys
from pathlib import Path

import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.pipeline import Pipeline
from sklearn.model_selection import RandomizedSearchCV, StratifiedKFold
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, f1_score, precision_score, recall_score, roc_auc_score

from app.ml.preprocessing import load_data, preprocess_features, create_preprocessor

# Configuration
TARGET_COL = 'PerformanceRating'
ID_COL = 'EmpNumber'
MODEL_SAVE_PATH = 'model.pkl'

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')
logger = logging.getLogger(__name__)

def train_model(train_path, test_path, output_path):
    logger.info("Loading data from %s and %s...", train_path, test_path)

    # Validate file paths
    train_path = Path(train_path)
    test_path = Path(test_path)
    if not train_path.exists():
        raise FileNotFoundError(f"Training file not found: {train_path}")
    if not test_path.exists():
        raise FileNotFoundError(f"Test file not found: {test_path}")

    df_train = load_data(str(train_path))
    df_test = load_data(str(test_path))
    
    # 1. Split Data & Preprocessing
    logger.info("Preprocessing data...")
    X_train, y_train = preprocess_features(df_train, target_col=TARGET_COL, id_col=ID_COL)
    X_test, y_test = preprocess_features(df_test, target_col=TARGET_COL, id_col=ID_COL)

    if X_train.shape[0] == 0 or X_test.shape[0] == 0:
        raise ValueError("Training or test set is empty after preprocessing.")

    if TARGET_COL not in df_train.columns:
        raise KeyError(f"Target column '{TARGET_COL}' not found in training data")
    
    # Identify columns
    numeric_cols = X_train.select_dtypes(include=['int64', 'float64']).columns
    categorical_cols = X_train.select_dtypes(include=['object']).columns
    
    logger.info("Numeric columns: %d", len(numeric_cols))
    logger.info("Categorical columns: %d", len(categorical_cols))
    
    # 2. Define Algorithm & Pipeline
    logger.info("Creating pipeline...")
    preprocessor = create_preprocessor(numeric_cols, categorical_cols)
    
    rf = RandomForestClassifier(random_state=42, class_weight='balanced')
    
    pipeline = Pipeline(steps=[
        ('preprocessor', preprocessor),
        ('model', rf)
    ])
    
    # 3. Hyperparameter Tuning
    logger.info("Starting Hyperparameter Tuning...")
    # Param grid from notebook
    param_dist = {
        'model__n_estimators': [100, 200, 300, 500],
        'model__max_depth': [10, 15, 20, 30, None],
        'model__min_samples_split': [2, 5, 10],
        'model__min_samples_leaf': [1, 2, 4],
        'model__max_features': ['sqrt', 'log2']
    }
    
    # Make sure cv splits are valid for the data distribution
    min_class_count = min(np.bincount(y_train.astype(int))) if len(np.unique(y_train)) > 0 else 0
    n_splits = 5
    if min_class_count < n_splits:
        n_splits = max(2, min_class_count)
        logger.warning("Reducing cv splits to %d because of small class counts", n_splits)

    cv = StratifiedKFold(n_splits=n_splits, shuffle=True, random_state=42)
    
    random_search = RandomizedSearchCV(
        pipeline, 
        param_distributions=param_dist, 
        n_iter=10, # Reduced to 10 for speed in demo, user can increase
        cv=cv, 
        scoring='f1_macro', 
        verbose=1, 
        n_jobs=-1, 
        random_state=42
    )
    
    # 4. Training Loop (Fit)
    try:
        random_search.fit(X_train, y_train)
    except Exception as e:
        logger.exception("Hyperparameter search failed: %s", e)
        raise

    best_model = random_search.best_estimator_
    logger.info("Best Parameters: %s", random_search.best_params_)
    
    # 5. Evaluation
    logger.info("Evaluating model...")
    try:
        y_pred = best_model.predict(X_test)
    except Exception as e:
        logger.exception("Prediction failed: %s", e)
        raise

    # predict_proba may not be implemented by all estimators
    y_proba = None
    try:
        if hasattr(best_model, "predict_proba"):
            y_proba = best_model.predict_proba(X_test)
    except Exception as e:
        logger.warning("predict_proba failed: %s", e)
    
    # Metrics
    acc = accuracy_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred, average='macro')
    precision = precision_score(y_test, y_pred, average='macro')
    recall = recall_score(y_test, y_pred, average='macro')
    
    # ROC-AUC (Multi-class handling)
    try:
        # Compute ROC-AUC only when probabilities are available
        if y_proba is not None:
            if len(set(y_test)) > 2:
                roc_auc = roc_auc_score(y_test, y_proba, multi_class='ovr', average='macro')
            else:
                if y_proba.shape[1] == 2:
                    roc_auc = roc_auc_score(y_test, y_proba[:, 1])
                else:
                    roc_auc = None
        else:
            roc_auc = None
    except Exception as e:
        logger.warning("Could not calculate ROC-AUC: %s", e)
        roc_auc = None
    
    logger.info("--- Evaluation Report ---")
    logger.info("Accuracy: %.4f", acc)
    logger.info("F1 Score (Macro): %.4f", f1)
    logger.info("Precision (Macro): %.4f", precision)
    logger.info("Recall (Macro): %.4f", recall)
    if roc_auc is not None:
        logger.info("ROC-AUC (Macro, OVR): %.4f", roc_auc)

    logger.info("\nClassification Report:\n%s", classification_report(y_test, y_pred))
    
    # Business Logic Checks
    logger.info("--- Business Logic Checks ---")
    valid_classes = set(y_train.unique())
    pred_classes = set(y_pred)
    invalid_preds = pred_classes - valid_classes

    if invalid_preds:
        logger.warning("Model predicted invalid classes: %s", invalid_preds)
    else:
        logger.info("PASSED: All predictions are within valid class range.")
        
    # 6. Model Serialization
    output_path = Path(output_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    logger.info("Saving model to %s...", output_path)
    try:
        joblib.dump(best_model, str(output_path))
        logger.info("Model saved successfully.")
    except Exception as e:
        logger.exception("Failed to save model: %s", e)
        raise

def main(argv=None):
    parser = argparse.ArgumentParser(description="Train Random Forest Model")
    parser.add_argument('--train', type=str, required=True, help='Path to training data csv')
    parser.add_argument('--test', type=str, required=True, help='Path to testing data csv')
    parser.add_argument('--output', type=str, default='model.pkl', help='Path to save trained model')

    args = parser.parse_args(argv)

    try:
        train_model(args.train, args.test, args.output)
    except Exception as e:
        logger.exception("Training failed: %s", e)
        sys.exit(1)


if __name__ == "__main__":
    main()
