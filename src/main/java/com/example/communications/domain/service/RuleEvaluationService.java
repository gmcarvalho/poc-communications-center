package com.example.communications.domain.service;

import com.example.communications.domain.model.CommunicationRule;
import com.example.communications.domain.model.ScheduledCommunication;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RuleEvaluationService {

    private final List<RuleEvaluator> evaluators;
    private Map<CommunicationRule, RuleEvaluator> evaluatorMap;

    public boolean shouldSend(ScheduledCommunication communication) {
        ensureInitialized();
        RuleEvaluator evaluator = evaluatorMap.getOrDefault(
                communication.getRule(), new AlwaysSendRuleEvaluator());
        return evaluator.shouldSend(communication);
    }

    private void ensureInitialized() {
        if (evaluatorMap == null) {
            evaluatorMap = new EnumMap<>(CommunicationRule.class);
            for (RuleEvaluator evaluator : evaluators) {
                evaluatorMap.put(evaluator.supportedRule(), evaluator);
            }
            evaluatorMap.putIfAbsent(CommunicationRule.NONE, new AlwaysSendRuleEvaluator());
        }
    }

    private static class AlwaysSendRuleEvaluator implements RuleEvaluator {

        @Override
        public CommunicationRule supportedRule() {
            return CommunicationRule.NONE;
        }

        @Override
        public boolean shouldSend(ScheduledCommunication communication) {
            return true;
        }
    }
}
