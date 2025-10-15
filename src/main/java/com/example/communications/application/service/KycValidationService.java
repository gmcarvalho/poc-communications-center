package com.example.communications.application.service;

import com.example.communications.application.dto.CommunicationTriggerRequest;
import com.example.communications.application.dto.CommunicationTriggerResponse;
import com.example.communications.application.dto.KycValidationRequest;
import com.example.communications.application.dto.KycValidationResponse;
import com.example.communications.domain.model.CommunicationChannel;
import com.example.communications.domain.model.CommunicationRule;
import com.example.communications.domain.model.CustomerCardStatus;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KycValidationService {

    private static final String PUSH_SUBJECT = "KYC concluído – seu cartão está disponível";
    private static final String REMINDER_SUBJECT = "Complete a emissão do seu cartão";

    private final CustomerCardStatusService customerCardStatusService;
    private final CommunicationCommandService communicationCommandService;

    public KycValidationResponse simulateSuccessfulValidation(KycValidationRequest request) {
        CustomerCardStatus status = customerCardStatusService.updateStatus(request.getCustomerId(), false);

        Map<String, Object> attributes = buildAttributes(request);
        String pushBody = buildPushBody(request.getCustomerName());
        String reminderBody = buildReminderBody(request.getCustomerName());

        triggerImmediatePush(request, attributes, pushBody);
        CommunicationTriggerResponse remindersResponse = scheduleReminders(request, attributes, reminderBody);

        return buildResponse(request, status.getUpdatedAt(), pushBody, reminderBody, remindersResponse);
    }

    private void triggerImmediatePush(
            KycValidationRequest request, Map<String, Object> attributes, String pushBody) {
        CommunicationTriggerRequest pushTrigger = CommunicationTriggerRequest.builder()
                .customerId(request.getCustomerId())
                .subject(PUSH_SUBJECT)
                .body(pushBody)
                .rule(CommunicationRule.CARD_ISSUANCE_REMINDER)
                .sendImmediately(true)
                .channel(CommunicationChannel.PUSH)
                .attributes(attributes)
                .build();

        communicationCommandService.handleTrigger(pushTrigger);
    }

    private CommunicationTriggerResponse scheduleReminders(
            KycValidationRequest request, Map<String, Object> attributes, String reminderBody) {
        CommunicationTriggerRequest reminderTrigger = CommunicationTriggerRequest.builder()
                .customerId(request.getCustomerId())
                .subject(REMINDER_SUBJECT)
                .body(reminderBody)
                .rule(CommunicationRule.CARD_ISSUANCE_REMINDER)
                .sendImmediately(false)
                .channel(CommunicationChannel.EMAIL)
                .scheduleDay(15)
                .scheduleDay(30)
                .attributes(attributes)
                .build();

        return communicationCommandService.handleTrigger(reminderTrigger);
    }

    private KycValidationResponse buildResponse(
            KycValidationRequest request,
            LocalDateTime approvedAt,
            String pushBody,
            String reminderBody,
            CommunicationTriggerResponse remindersResponse) {
        List<UUID> scheduledIds = remindersResponse.getScheduledCommunicationIds();
        return KycValidationResponse.builder()
                .customerId(request.getCustomerId())
                .kycStatus("APPROVED")
                .approvedAt(approvedAt)
                .initialChannel(CommunicationChannel.PUSH)
                .initialSubject(PUSH_SUBJECT)
                .initialBody(pushBody)
                .reminderDay(15)
                .reminderDay(30)
                .reminderChannel(CommunicationChannel.EMAIL)
                .reminderSubject(REMINDER_SUBJECT)
                .reminderBody(reminderBody)
                .scheduledCommunicationIds(scheduledIds)
                .build();
    }

    private Map<String, Object> buildAttributes(KycValidationRequest request) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("customerName", request.getCustomerName());
        if (request.getEmail() != null) {
            attributes.put("email", request.getEmail());
        }
        return attributes;
    }

    private String buildPushBody(String customerName) {
        return "Olá "
                + customerName
                + ", seus dados foram validados com sucesso. Emita seu cartão agora mesmo no app.";
    }

    private String buildReminderBody(String customerName) {
        return "Olá "
                + customerName
                + ", percebemos que seu cartão ainda não foi emitido. Conclua a emissão para aproveitar todos os benefícios.";
    }
}
