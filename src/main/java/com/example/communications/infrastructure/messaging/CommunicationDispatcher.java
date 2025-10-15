package com.example.communications.infrastructure.messaging;

import com.example.communications.domain.model.CommunicationMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommunicationDispatcher {

    private final List<CommunicationSender> senders;

    public void dispatch(CommunicationMessage message) {
        senders.stream()
                .filter(sender -> sender.supports(message.getChannel()))
                .findFirst()
                .ifPresentOrElse(sender -> sender.send(message),
                        () -> log.warn("No sender found for channel {}", message.getChannel()));
    }
}
