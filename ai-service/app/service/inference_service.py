from datetime import datetime, timezone
from typing import Any, Dict
from app.schemas.prediction import PredictionRequest, PredictionResponse
from app.ml.inference import infer as model_infer


def predict(req: PredictionRequest) -> PredictionResponse:
    """Run the ML model on the provided features and return a PredictionResponse.

    Assumptions made:
    - req.features should contain an 'employeeId' or 'employee_id' key; if missing we return 0.
    - The underlying model.predict returns a list-like of predictions; we use the first item.
    - If the model supports predict_proba we include a simple confidence value in comments.
    """
    features: Dict[str, Any] = req.features or {}

    # extract employee id if present
    emp_id = 0
    try:
        employee_id_value = None
        for k, v in features.items():
            if k.lower() in {"employeeid", "employee_id"}:
                employee_id_value = v
                break
        emp_id = int(employee_id_value or 0)
    except Exception:
        emp_id = 0

    # Run prediction via shared infer(...) helper
    result = model_infer(features)

    if result.get("error"):
        return PredictionResponse(
            employeeId=emp_id,
            performanceScore="ERROR",
            reviewDate=datetime.now(timezone.utc),
            comments=result.get("comments", "prediction_error"),
        )

    pred = result.get("prediction")
    comments = result.get("comments", "")

    return PredictionResponse(
        employeeId=emp_id,
        performanceScore=str(pred),
        reviewDate=datetime.now(timezone.utc),
        comments=comments,
    )
