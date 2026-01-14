from datetime import datetime
from pydantic import BaseModel
from typing import Any, Dict

class PredictionRequest(BaseModel):
    features: Dict[str, Any]

class PredictionResponse(BaseModel):
    employeeId: int
    performanceScore: str
    reviewDate: datetime
    comments: str
