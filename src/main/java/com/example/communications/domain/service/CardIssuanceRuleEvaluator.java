package com.example.communications.domain.service;

import com.example.communications.domain.model.CommunicationRule;
import com.example.communications.domain.model.CustomerCardStatus;
import com.example.communications.domain.model.ScheduledCommunication;
import com.example.communications.domain.repository.CustomerCardStatusRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardIssuanceRuleEvaluator implements RuleEvaluator {

    private static final Logger log = LoggerFactory.getLogger(CardIssuanceRuleEvaluator.class);
    private final CustomerCardStatusRepository customerCardStatusRepository;

    @Override
    public CommunicationRule supportedRule() {
        return CommunicationRule.CARD_ISSUANCE_REMINDER;
    }

    @Override
    public boolean shouldSend(ScheduledCommunication communication) {
        Optional<CustomerCardStatus> statusOptional =
                customerCardStatusRepository.findById(communication.getCustomerId());

        boolean shouldSend = statusOptional.map(status -> !status.isCardIssued()).orElse(true);
        log.debug("Evaluating rule {} for customer {} - cardIssued? {} - shouldSend? {}",
                communication.getRule(),
                communication.getCustomerId(),
                statusOptional.map(CustomerCardStatus::isCardIssued).orElse(false),
                shouldSend);
        return shouldSend;
    }
}
