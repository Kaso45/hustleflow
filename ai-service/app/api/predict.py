from fastapi import APIRouter
from app.schemas.prediction import PredictionRequest, PredictionResponse
from app.service.inference_service import predict

router = APIRouter(tags=["inference"])

@router.post("/predict", response_model=PredictionResponse)
def predict_endpoint(req: PredictionRequest):
    return predict(req)
