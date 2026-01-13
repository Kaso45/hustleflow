package com.hustleflow.ml.dto;

public class MlPredictResponse {
    private String prediction;
    private String model_status;
    private Integer received_features;

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public String getModel_status() {
        return model_status;
    }

    public void setModel_status(String model_status) {
        this.model_status = model_status;
    }

    public Integer getReceived_features() {
        return received_features;
    }

    public void setReceived_features(Integer received_features) {
        this.received_features = received_features;
    }
}
