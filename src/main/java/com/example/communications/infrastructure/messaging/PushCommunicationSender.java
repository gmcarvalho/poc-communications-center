package com.example.communications.infrastructure.messaging;

import com.example.communications.domain.model.CommunicationChannel;
import com.example.communications.domain.model.CommunicationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushCommunicationSender implements CommunicationSender {

    private final FakePushNotificationService pushNotificationService;

    @Override
    public boolean supports(CommunicationChannel channel) {
        return CommunicationChannel.PUSH == channel;
    }

    @Override
    public void send(CommunicationMessage message) {
        pushNotificationService.sendPush(
                message.getCustomerId(), message.getSubject(), message.getBody());
    }
}
