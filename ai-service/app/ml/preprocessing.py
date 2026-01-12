import pandas as pd
import numpy as np
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
from sklearn.preprocessing import OneHotEncoder
from sklearn.compose import ColumnTransformer

def load_data(path):
    """Load dataset from CSV."""
    return pd.read_csv(path)

def preprocess_features(df, target_col=None, id_col=None):
    """
    Separates features and target.
    
    Args:
        df: Input DataFrame.
        target_col: Name of the target column.
        id_col: Name of the ID column to drop.
        
    Returns:
        X: Features DataFrame.
        y: Target Series (if target_col is present).
    """
    df = df.copy()
    
    if id_col and id_col in df.columns:
        df = df.drop(columns=[id_col])
        
    if target_col and target_col in df.columns:
        X = df.drop(columns=[target_col])
        y = df[target_col]
        return X, y
    else:
        return df, None

def create_preprocessor(numeric_cols, categorical_cols):
    """
    Creates a scikit-learn preprocessing pipeline.
    
    Args:
        numeric_cols: List of numeric column names.
        categorical_cols: List of categorical column names.
        
    Returns:
        ColumnTransformer: The preprocessing pipeline.
    """
    numeric_transformer = Pipeline(steps=[
        ('imputer', SimpleImputer(strategy='mean'))
    ])

    categorical_transformer = Pipeline(steps=[
        ('imputer', SimpleImputer(strategy='constant', fill_value='missing')),
        ('onehot', OneHotEncoder(handle_unknown='ignore', sparse_output=False))
    ])

    preprocessor = ColumnTransformer(
        transformers=[
            ('num', numeric_transformer, numeric_cols),
            ('cat', categorical_transformer, categorical_cols)
        ])
        
    return preprocessor
