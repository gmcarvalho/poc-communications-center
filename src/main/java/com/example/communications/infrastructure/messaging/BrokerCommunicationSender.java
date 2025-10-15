package com.example.communications.infrastructure.messaging;

import com.example.communications.domain.model.CommunicationChannel;
import com.example.communications.domain.model.CommunicationMessage;
import java.util.EnumSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerCommunicationSender implements CommunicationSender {

    private static final Set<CommunicationChannel> SUPPORTED_CHANNELS =
            EnumSet.of(CommunicationChannel.EMAIL, CommunicationChannel.SMS, CommunicationChannel.WHATSAPP);

    private final FakeBrokerService brokerService;

    @Override
    public boolean supports(CommunicationChannel channel) {
        return SUPPORTED_CHANNELS.contains(channel);
    }

    @Override
    public void send(CommunicationMessage message) {
        switch (message.getChannel()) {
            case EMAIL -> brokerService.sendEmail(message.getCustomerId(), message.getSubject(), message.getBody());
            case SMS -> brokerService.sendSms(message.getCustomerId(), message.getBody());
            case WHATSAPP -> brokerService.sendWhatsapp(message.getCustomerId(), message.getBody());
            default -> throw new IllegalArgumentException("Unsupported channel for broker: " + message.getChannel());
        }
    }
}
