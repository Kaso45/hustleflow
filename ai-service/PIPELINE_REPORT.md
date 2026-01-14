# Machine Learning Pipeline Report & User Guide

## 1. Overview
This project implements a complete Machine Learning training and inference pipeline for the **HustleFlow** employee performance prediction model.
The pipeline uses **Random Forest Classifier** to predict employee performance ratings based on various demographic and work-related features.

## 2. Pipeline Architecture
 The pipeline consists of the following modular steps:

1.  **Data Loading & Splitting**:
    -   Loads raw training and testing CSV files.
    -   Separates features (X) and target (y).
    -   *Location*: `app/ml/preprocessing.py`

2.  **Preprocessing**:
    -   **Numeric Features**: Imputation (Mean).
    -   **Categorical Features**: Imputation (Constant 'missing') and One-Hot Encoding (handle unknown categories).
    -   *Location*: `app/ml/preprocessing.py`

3.  **Hyperparameter Tuning**:
    -   Technique: **RandomizedSearchCV** (Optimizes for F1-Macro score).
    -   Search Space: Number of trees, Max depth, Min samples split, Min samples leaf.
    -   *Location*: `app/ml/train.py`

4.  **Training**:
    -   Algorithm: **Random Forest**.
    -   Class Weighting: Balanced (to handle imbalanced classes).

5.  **Evaluation**:
    -   **Metrics**: Accuracy, F1-Score (Macro), Classification Report (Precision/Recall per class).
    -   **Business Logic Checks**: Verifies that all predictions fall within the valid set of target classes found in training data.
    -   *Location*: `app/ml/train.py`

6.  **Serialization**:
    -   The trained model (including the preprocessing pipeline) is saved as a `.pkl` file using `joblib`.

## 3. Project Structure
```
ai-service/
├── app/
│   ├── ml/
│   │   ├── preprocessing.py   # Data transformation logic
│   │   ├── train.py           # Main training script (Entry point)
│   │   ├── inference.py       # Inference class for API integration
│   ├── model/                 # Directory to save trained models
├── data/                      # Directory for input datasets (train_raw.csv, test_raw.csv)
├── requirements.txt           # Python dependencies
└── PIPELINE_REPORT.md         # This file
```

## 4. How to Run the Pipeline

### Prerequisites
Ensure you have Python installed and the dependencies are set up:
```bash
pip install -r requirements.txt
```

### Step 1: Prepare Data
Place your raw dataset files in the `data/` directory:
-   `data/train_raw.csv`
-   `data/test_raw.csv`

*(Note: Ensure these files match the format expected by the notebook/preprocessing logic)*

### Step 2: Execute Training
Run the training module from the root `ai-service` directory:
```bash
python -m app.ml.train --train data/train_raw.csv --test data/test_raw.csv --output app/model/rf_model.pkl
```

**What happens when you run this?**
-   The script loads the data.
-   It runs RandomizedSearchCV to find the best hyperparameters.
-   It prints the evaluation report (Accuracy, F1, Matrix) to the console.
-   It performs business logic checks.
-   It saves the best model to `app/model/rf_model.pkl`.

### Step 3: Inference (Using the Model)
You can use the trained model in your application logic (e.g., in `app/main.py` or API endpoints) using the helper class:

```python
from app.ml.inference import ModelInference

# Initialize
model_service = ModelInference(model_path='app/model/rf_model.pkl')

# Predict
sample_data = {
    'Age': 30,
    'Department': 'Sales',
    # ... other features
}
prediction = model_service.predict(sample_data)
print(f"Predicted Performance: {prediction}")
```

## 5. Report Generation & Metrics
During training, the system will output a report resembling the following into the terminal:

```text
--- Evaluation Report ---
Accuracy: 0.9292
F1 Score (Macro): 0.8862
Precision (Macro): 0.9320
Recall (Macro): 0.8514
ROC-AUC (Macro, OVR): 0.9812

Classification Report:
              precision    recall  f1-score   support
           0       0.92      0.85      0.88        39
           1       0.93      0.98      0.95       175
           2       0.95      0.73      0.83        26

    accuracy                           0.93       240
   macro avg       0.93      0.85      0.89       240
weighted avg       0.93      0.93      0.93       240

--- Business Logic Checks ---
PASSED: All predictions are within valid class range.
```
