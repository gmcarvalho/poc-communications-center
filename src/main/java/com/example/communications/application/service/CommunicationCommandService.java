package com.example.communications.application.service;

import com.example.communications.application.dto.CommunicationTriggerRequest;
import com.example.communications.application.dto.CommunicationTriggerResponse;
import com.example.communications.domain.model.CommunicationChannel;
import com.example.communications.domain.model.CommunicationMessage;
import com.example.communications.domain.model.CommunicationStatus;
import com.example.communications.domain.model.ScheduledCommunication;
import com.example.communications.domain.repository.ScheduledCommunicationRepository;
import com.example.communications.infrastructure.messaging.CommunicationDispatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunicationCommandService {

    private final ScheduledCommunicationRepository scheduledCommunicationRepository;
    private final CommunicationDispatcher communicationDispatcher;
    private final ObjectMapper objectMapper;

    @Transactional
    public CommunicationTriggerResponse handleTrigger(CommunicationTriggerRequest request) {
        List<UUID> scheduledIds = new ArrayList<>();

        if (Boolean.TRUE.equals(request.getSendImmediately())) {
            request.getChannels().forEach(channel -> sendNow(channel, request));
        }

        if (!CollectionUtils.isEmpty(request.getScheduleDays())) {
            for (CommunicationChannel channel : request.getChannels()) {
                for (Integer dayOffset : request.getScheduleDays()) {
                    ScheduledCommunication communication = buildScheduledCommunication(request, channel, dayOffset);
                    ScheduledCommunication saved = scheduledCommunicationRepository.save(communication);
                    scheduledIds.add(saved.getId());
                    log.info("Scheduled communication {} for customer {} on {} via {}",
                            saved.getId(), saved.getCustomerId(), saved.getScheduledAt(), saved.getChannel());
                }
            }
        }

        return CommunicationTriggerResponse.builder().scheduledCommunicationIds(scheduledIds).build();
    }

    private void sendNow(CommunicationChannel channel, CommunicationTriggerRequest request) {
        CommunicationMessage message = CommunicationMessage.builder()
                .customerId(request.getCustomerId())
                .channel(channel)
                .subject(request.getSubject())
                .body(request.getBody())
                .attributes(request.getAttributes())
                .build();
        communicationDispatcher.dispatch(message);
    }

    private ScheduledCommunication buildScheduledCommunication(
            CommunicationTriggerRequest request, CommunicationChannel channel, Integer dayOffset) {
        int safeOffset = dayOffset == null ? 0 : Math.max(dayOffset, 0);
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(safeOffset);
        ScheduledCommunication communication = new ScheduledCommunication();
        communication.setCustomerId(request.getCustomerId());
        communication.setChannel(channel);
        communication.setRule(request.getRule());
        communication.setStatus(CommunicationStatus.PENDING);
        communication.setScheduledAt(scheduledAt);
        communication.setSubject(request.getSubject());
        communication.setBody(request.getBody());
        communication.setAttributesJson(serializeAttributes(request));
        return communication;
    }

    private String serializeAttributes(CommunicationTriggerRequest request) {
        if (request.getAttributes() == null || request.getAttributes().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(request.getAttributes());
        } catch (JsonProcessingException ex) {
            log.warn("Unable to serialize attributes for customer {}. Ignoring attributes.",
                    request.getCustomerId(),
                    ex);
            return null;
        }
    }
}
