package com.example.communications.interfaces.controller;

import com.example.communications.application.dto.CommunicationTriggerRequest;
import com.example.communications.application.dto.CommunicationTriggerResponse;
import com.example.communications.application.service.CommunicationCommandService;
import com.example.communications.domain.model.ScheduledCommunication;
import com.example.communications.domain.repository.ScheduledCommunicationRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communications")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationCommandService communicationCommandService;
    private final ScheduledCommunicationRepository scheduledCommunicationRepository;

    @PostMapping("/triggers")
    public ResponseEntity<CommunicationTriggerResponse> trigger(@Valid @RequestBody CommunicationTriggerRequest request) {
        CommunicationTriggerResponse response = communicationCommandService.handleTrigger(request);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/scheduled")
    public ResponseEntity<List<ScheduledCommunication>> listScheduled() {
        return ResponseEntity.ok(scheduledCommunicationRepository.findAll());
    }
}
