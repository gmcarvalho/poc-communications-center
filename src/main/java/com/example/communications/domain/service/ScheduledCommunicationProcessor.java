package com.example.communications.domain.service;

import com.example.communications.domain.model.CommunicationMessage;
import com.example.communications.domain.model.CommunicationStatus;
import com.example.communications.domain.model.ScheduledCommunication;
import com.example.communications.domain.repository.ScheduledCommunicationRepository;
import com.example.communications.infrastructure.messaging.CommunicationDispatcher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledCommunicationProcessor {

    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<>() {};

    private final ScheduledCommunicationRepository repository;
    private final CommunicationDispatcher dispatcher;
    private final RuleEvaluationService ruleEvaluationService;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Scheduled(cron = "${scheduler.communication.cron:0 0 6 * * *}")
    @Transactional
    public void process() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<ScheduledCommunication> communications = repository.findByStatusAndScheduledAtLessThanEqual(
                CommunicationStatus.PENDING, now);

        if (communications.isEmpty()) {
            log.debug("No communications to process at {}", now);
            return;
        }

        log.info("Processing {} scheduled communications", communications.size());
        communications.forEach(this::processCommunication);
    }

    private void processCommunication(ScheduledCommunication communication) {
        UUID id = communication.getId();
        try {
            if (ruleEvaluationService.shouldSend(communication)) {
                CommunicationMessage message = CommunicationMessage.builder()
                        .customerId(communication.getCustomerId())
                        .channel(communication.getChannel())
                        .subject(communication.getSubject())
                        .body(communication.getBody())
                        .attributes(deserializeAttributes(communication.getAttributesJson()))
                        .build();
                dispatcher.dispatch(message);
                communication.setStatus(CommunicationStatus.SENT);
                log.info("Communication {} sent to customer {} via {}", id, communication.getCustomerId(),
                        communication.getChannel());
            } else {
                communication.setStatus(CommunicationStatus.SKIPPED);
                log.info(
                        "Communication {} skipped by rule {} for customer {}", id, communication.getRule(), communication.getCustomerId());
            }
        } catch (Exception ex) {
            log.error("Failed to process communication {}", id, ex);
            // Keep as pending for retry; transaction rollback will maintain state
            throw ex;
        }
    }

    private Map<String, Object> deserializeAttributes(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (IOException e) {
            log.warn("Could not deserialize attributes. Returning null.", e);
            return null;
        }
    }
}
