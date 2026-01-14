package com.hustleflow.ml.service;

import com.hustleflow.ml.dto.MlPredictRequest;
import com.hustleflow.ml.dto.MlPredictResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MlClientService {

    private final RestTemplate restTemplate;

    @Value("${ml.service.base-url}")
    private String mlBaseUrl;

    public MlClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MlPredictResponse predict(MlPredictRequest request) {
        MlPredictResponse response = restTemplate.postForObject(mlBaseUrl + "/predict", request, MlPredictResponse.class);
        if (response == null) {
            return null;
        }

        response.setPerformanceScore(mapPerformanceScore(response.getPerformanceScore()));
        return response;
    }

    private String mapPerformanceScore(String rawScore) {
        if (rawScore == null) {
            return null;
        }

        String value = rawScore.trim();
        switch (value) {
            case "0":
                return "low";
            case "1":
                return "medium";
            case "2":
                return "good";
            default:
                return rawScore;
        }
    }
}
