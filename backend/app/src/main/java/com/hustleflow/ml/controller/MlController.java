package com.hustleflow.ml.controller;

import com.hustleflow.ml.dto.MlPredictRequest;
import com.hustleflow.ml.dto.MlPredictResponse;
import com.hustleflow.ml.service.MlClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ml")
public class MlController {

    private final MlClientService mlClientService;

    public MlController(MlClientService mlClientService) {
        this.mlClientService = mlClientService;
    }

    @PostMapping("/predict")
    public ResponseEntity<MlPredictResponse> predict(@RequestBody MlPredictRequest request) {
        return ResponseEntity.ok(mlClientService.predict(request));
    }
}
