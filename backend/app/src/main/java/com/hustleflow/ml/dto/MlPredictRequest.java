package com.hustleflow.ml.dto;

import java.util.Map;

public class MlPredictRequest {
    private Map<String, Object> features;

    public MlPredictRequest() {
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
}
