package com.example.communications.interfaces.controller;

import com.example.communications.application.dto.CardStatusUpdateRequest;
import com.example.communications.application.service.CustomerCardStatusService;
import com.example.communications.domain.model.CustomerCardStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerCardStatusController {

    private final CustomerCardStatusService service;

    @PutMapping("/{customerId}/card-status")
    public ResponseEntity<CustomerCardStatus> updateStatus(
            @PathVariable String customerId, @Valid @RequestBody CardStatusUpdateRequest request) {
        CustomerCardStatus status = service.updateStatus(customerId, request.cardIssued());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{customerId}/card-status")
    public ResponseEntity<Boolean> isCardIssued(@PathVariable String customerId) {
        return ResponseEntity.ok(service.isCardIssued(customerId));
    }
}
