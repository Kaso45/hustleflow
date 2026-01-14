import os
import logging
from pathlib import Path
from typing import Any, Dict, Optional, Union

import joblib
import pandas as pd

class ModelInference:
    def __init__(self, model_path: Union[str, os.PathLike] = "ai-service/app/model/model.pkl"):
        self.model_path = str(model_path)
        self.model: Optional[Any] = None
        self.load_model()

    def load_model(self):
        """Loads the serialized model from disk."""
        model_path = Path(self.model_path)
        if model_path.exists():
            try:
                self.model = joblib.load(model_path)
                logging.getLogger(__name__).info("Model loaded successfully from %s", model_path)
            except Exception as e:
                logging.getLogger(__name__).exception("Error loading model from %s", model_path)
                self.model = None
        else:
            logging.getLogger(__name__).warning("Model file not found at %s", model_path)

    def predict(self, input_data: Union[pd.DataFrame, Dict[str, Any]]):
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
        elif not isinstance(input_data, pd.DataFrame):
            raise TypeError("input_data must be a pandas.DataFrame or dict")
            
        try:
            predictions = self.model.predict(input_data)
            if hasattr(predictions, "tolist"):
                return predictions.tolist()
            return list(predictions)
        except Exception as e:
            raise RuntimeError(f"Prediction failed: {e}")

    def predict_proba(self, input_data: Union[pd.DataFrame, Dict[str, Any]]):
        """
        Make probability predictions.
        """
        if self.model is None:
            raise RuntimeError("Model is not loaded.")
            
        if isinstance(input_data, dict):
            input_data = pd.DataFrame([input_data])
        elif not isinstance(input_data, pd.DataFrame):
            raise TypeError("input_data must be a pandas.DataFrame or dict")

        try:
            if hasattr(self.model, "predict_proba"):
                probs = self.model.predict_proba(input_data)
                if hasattr(probs, "tolist"):
                    return probs.tolist()
                return list(probs)
            else:
                raise NotImplementedError("Model does not support probability predictions.")
        except Exception as e:
            raise RuntimeError(f"Prediction failed: {e}")


# --- Convenience singleton and infer wrapper for application code ---
_DEFAULT_MODEL_PATH = (Path(__file__).resolve().parents[1] / "model" / "model.pkl")
_MODEL_PATH = os.environ.get("MODEL_PATH", str(_DEFAULT_MODEL_PATH))
_INFERENCE = ModelInference(model_path=_MODEL_PATH)

def infer(input_features: Optional[Dict[str, Any]]):
    """Run prediction and optional probability for a single input feature dict.

    Returns a dict: {"prediction": <value>, "comments": <str>, "error": <str or None>}.
    """
    features = input_features or {}
    try:
        preds = _INFERENCE.predict(features)
        pred = preds[0] if isinstance(preds, (list, tuple)) and len(preds) > 0 else preds
    except Exception as e:
        return {"prediction": None, "comments": f"prediction_error: {e}", "error": str(e)}

    comments = ""
    try:
        probs = _INFERENCE.predict_proba(features)
        if isinstance(probs, (list, tuple)) and len(probs) > 0:
            first = probs[0]
            if isinstance(first, (list, tuple)):
                comments = f"confidence={max(first):.4f}"
            else:
                comments = f"confidence={float(first):.4f}"
    except Exception:
        if not comments:
            comments = "no_confidence"

    return {"prediction": pred, "comments": comments, "error": None}
