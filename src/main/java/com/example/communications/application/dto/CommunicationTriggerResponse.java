package com.example.communications.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CommunicationTriggerResponse {
    List<UUID> scheduledCommunicationIds;
}
