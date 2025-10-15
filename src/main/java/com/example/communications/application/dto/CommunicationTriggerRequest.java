package com.example.communications.application.dto;

import com.example.communications.domain.model.CommunicationChannel;
import com.example.communications.domain.model.CommunicationRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CommunicationTriggerRequest {

    @NotBlank
    String customerId;

    @NotBlank
    String subject;

    @NotBlank
    String body;

    @NotNull
    CommunicationRule rule;

    @NotNull
    Boolean sendImmediately;

    @NotNull
    @Size(min = 1)
    @Singular
    List<CommunicationChannel> channels;

    @Singular
    List<Integer> scheduleDays;

    Map<String, Object> attributes;
}
