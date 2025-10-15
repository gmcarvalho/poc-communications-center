package com.example.communications.infrastructure.messaging;

import com.example.communications.domain.model.CommunicationChannel;
import com.example.communications.domain.model.CommunicationMessage;

public interface CommunicationSender {

    boolean supports(CommunicationChannel channel);

    void send(CommunicationMessage message);
}
