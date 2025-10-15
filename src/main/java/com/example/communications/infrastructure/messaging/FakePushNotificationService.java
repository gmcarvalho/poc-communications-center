package com.example.communications.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FakePushNotificationService {

    public void sendPush(String customerId, String subject, String body) {
        log.info("[FAKE-PUSH] Push notification sent to {} | title={} | body={}", customerId, subject, body);
    }
}
