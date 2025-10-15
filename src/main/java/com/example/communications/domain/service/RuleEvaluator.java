package com.example.communications.domain.service;

import com.example.communications.domain.model.CommunicationRule;
import com.example.communications.domain.model.ScheduledCommunication;

public interface RuleEvaluator {

    CommunicationRule supportedRule();

    boolean shouldSend(ScheduledCommunication communication);
}
