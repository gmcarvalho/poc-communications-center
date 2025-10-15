package com.example.communications.domain.model;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CommunicationMessage {
    String customerId;
    CommunicationChannel channel;
    String subject;
    String body;
    Map<String, Object> attributes;
}
