package com.example.communications.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FakeBrokerService {

    public void sendEmail(String customerId, String subject, String body) {
        log.info("[FAKE-BROKER] Email sent to {} | subject={} | body={}", customerId, subject, body);
    }

    public void sendSms(String customerId, String body) {
        log.info("[FAKE-BROKER] SMS sent to {} | body={}", customerId, body);
    }

    public void sendWhatsapp(String customerId, String body) {
        log.info("[FAKE-BROKER] WhatsApp sent to {} | body={}", customerId, body);
    }
}
