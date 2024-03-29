package be.kdg.backend.controller;

import be.kdg.backend.dto.fraud.NewFraudDetectionDto;
import be.kdg.backend.service.FraudDetectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fraud-detections")
public class FraudDetectionController {
    private final FraudDetectionService fraudDetectionService;

    public FraudDetectionController(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @PostMapping
    public ResponseEntity<String> detectFraud(@Valid NewFraudDetectionDto newFraudDetectionDto) {
        fraudDetectionService.detectFraud(newFraudDetectionDto);
        return ResponseEntity.ok("Fraud detection posted");
    }
}
