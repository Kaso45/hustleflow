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
        return restTemplate.postForObject(mlBaseUrl + "/predict", request, MlPredictResponse.class);
    }
}
