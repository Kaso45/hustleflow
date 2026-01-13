import joblib
import pandas as pd
import os
import numpy as np

class ModelInference:
    def __init__(self, model_path='model.pkl'):
        self.model_path = model_path
        self.model = None
        self.load_model()

    def load_model(self):
        """Loads the serialized model from disk."""
        if os.path.exists(self.model_path):
            try:
                self.model = joblib.load(self.model_path)
                print(f"Model loaded successfully from {self.model_path}")
            except Exception as e:
                print(f"Error loading model: {e}")
                self.model = None
        else:
            print(f"Warning: Model file not found at {self.model_path}")

    def predict(self, input_data):
        """
        Make predictions using the loaded model.
        
        Args:
            input_data (pd.DataFrame or dict): Input features.
            
        Returns:
            list: Predictions.
        """
        if self.model is None:
            raise RuntimeError("Model is not loaded. Please train the model first.")
        
        # Handle dictionary input
        if isinstance(input_data, dict):
            input_data = pd.DataFrame([input_data])
            
        try:
            predictions = self.model.predict(input_data)
            return predictions.tolist()
        except Exception as e:
            raise RuntimeError(f"Prediction failed: {e}")

    def predict_proba(self, input_data):
        """
        Make probability predictions.
        """
        if self.model is None:
            raise RuntimeError("Model is not loaded.")
            
        if isinstance(input_data, dict):
            input_data = pd.DataFrame([input_data])

        try:
            if hasattr(self.model, "predict_proba"):
                probs = self.model.predict_proba(input_data)
                return probs.tolist()
            else:
                raise NotImplementedError("Model does not support probability predictions.")
        except Exception as e:
            raise RuntimeError(f"Prediction failed: {e}")
