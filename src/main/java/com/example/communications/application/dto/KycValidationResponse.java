package com.example.communications.application.dto;

import com.example.communications.domain.model.CommunicationChannel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class KycValidationResponse {

    String customerId;
    String kycStatus;
    LocalDateTime approvedAt;

    CommunicationChannel initialChannel;
    String initialSubject;
    String initialBody;

    @Singular
    List<Integer> reminderDays;

    CommunicationChannel reminderChannel;
    String reminderSubject;
    String reminderBody;

    @Singular
    List<UUID> scheduledCommunicationIds;
}
