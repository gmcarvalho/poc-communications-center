package com.example.communications.interfaces.controller;

import com.example.communications.application.dto.KycValidationRequest;
import com.example.communications.application.dto.KycValidationResponse;
import com.example.communications.application.service.KycValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycValidationService kycValidationService;

    @PostMapping("/validate")
    public ResponseEntity<KycValidationResponse> simulateSuccessfulValidation(
            @Valid @RequestBody KycValidationRequest request) {
        return ResponseEntity.ok(kycValidationService.simulateSuccessfulValidation(request));
    }
}
