import os
import argparse
import joblib
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

def train_model(train_path, test_path, output_path):
    print(f"Loading data from {train_path} and {test_path}...")
    df_train = load_data(train_path)
    df_test = load_data(test_path)
    
    # 1. Split Data & Preprocessing
    print("Preprocessing data...")
    X_train, y_train = preprocess_features(df_train, target_col=TARGET_COL, id_col=ID_COL)
    X_test, y_test = preprocess_features(df_test, target_col=TARGET_COL, id_col=ID_COL)
    
    # Identify columns
    numeric_cols = X_train.select_dtypes(include=['int64', 'float64']).columns
    categorical_cols = X_train.select_dtypes(include=['object']).columns
    
    print(f"Numeric columns: {len(numeric_cols)}")
    print(f"Categorical columns: {len(categorical_cols)}")
    
    # 2. Define Algorithm & Pipeline
    print("Creating pipeline...")
    preprocessor = create_preprocessor(numeric_cols, categorical_cols)
    
    rf = RandomForestClassifier(random_state=42, class_weight='balanced')
    
    pipeline = Pipeline(steps=[
        ('preprocessor', preprocessor),
        ('model', rf)
    ])
    
    # 3. Hyperparameter Tuning
    print("Starting Hyperparameter Tuning...")
    # Param grid from notebook
    param_dist = {
        'model__n_estimators': [100, 200, 300, 500],
        'model__max_depth': [10, 15, 20, 30, None],
        'model__min_samples_split': [2, 5, 10],
        'model__min_samples_leaf': [1, 2, 4],
        'model__max_features': ['sqrt', 'log2']
    }
    
    cv = StratifiedKFold(n_splits=5, shuffle=False)
    
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
    random_search.fit(X_train, y_train)
    
    best_model = random_search.best_estimator_
    print(f"Best Parameters: {random_search.best_params_}")
    
    # 5. Evaluation
    print("Evaluating model...")
    y_pred = best_model.predict(X_test)
    y_proba = best_model.predict_proba(X_test)
    
    # Metrics
    acc = accuracy_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred, average='macro')
    precision = precision_score(y_test, y_pred, average='macro')
    recall = recall_score(y_test, y_pred, average='macro')
    
    # ROC-AUC (Multi-class handling)
    try:
        # Check if more than 2 classes exist in test set or model
        if len(set(y_test)) > 2:
            roc_auc = roc_auc_score(y_test, y_proba, multi_class='ovr', average='macro')
        else:
            # Binary case
            # Note: For binary, roc_auc_score expects probability of positive class
            # Assuming labels are 0 and 1, or 1 and 2. 
            # We need to ensure we pick the prob for the "positive" class.
            if y_proba.shape[1] == 2:
                roc_auc = roc_auc_score(y_test, y_proba[:, 1])
            else:
                roc_auc = None 
    except Exception as e:
        print(f"Could not calculate ROC-AUC: {e}")
        roc_auc = None
    
    print("\n--- Evaluation Report ---")
    print(f"Accuracy: {acc:.4f}")
    print(f"F1 Score (Macro): {f1:.4f}")
    print(f"Precision (Macro): {precision:.4f}")
    print(f"Recall (Macro): {recall:.4f}")
    if roc_auc is not None:
        print(f"ROC-AUC (Macro, OVR): {roc_auc:.4f}")
        
    print("\nClassification Report:")
    print(classification_report(y_test, y_pred))
    
    # Business Logic Checks
    print("\n--- Business Logic Checks ---")
    valid_classes = set(y_train.unique())
    pred_classes = set(y_pred)
    invalid_preds = pred_classes - valid_classes
    
    if invalid_preds:
        print(f"WARNING: Model predicted invalid classes: {invalid_preds}")
    else:
        print("PASSED: All predictions are within valid class range.")
        
    # 6. Model Serialization
    print(f"Saving model to {output_path}...")
    joblib.dump(best_model, output_path)
    print("Model saved successfully.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Train Random Forest Model")
    parser.add_argument('--train', type=str, required=True, help='Path to training data csv')
    parser.add_argument('--test', type=str, required=True, help='Path to testing data csv')
    parser.add_argument('--output', type=str, default='model.pkl', help='Path to save trained model')
    
    args = parser.parse_args()
    
    train_model(args.train, args.test, args.output)
