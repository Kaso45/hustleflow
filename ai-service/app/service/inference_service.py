from app.schemas.prediction import PredictionRequest, PredictionResponse

def predict(req: PredictionRequest) -> PredictionResponse:
    return PredictionResponse(
        prediction="MOCK",
        model_status="NOT_LOADED",
        received_features=len(req.features),
    )
