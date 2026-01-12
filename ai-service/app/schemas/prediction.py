from pydantic import BaseModel
from typing import Any, Dict

class PredictionRequest(BaseModel):
    features: Dict[str, Any]

class PredictionResponse(BaseModel):
    prediction: str
    model_status: str
    received_features: int
