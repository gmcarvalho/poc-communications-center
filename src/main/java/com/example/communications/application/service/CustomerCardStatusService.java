package com.example.communications.application.service;

import com.example.communications.domain.model.CustomerCardStatus;
import com.example.communications.domain.repository.CustomerCardStatusRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerCardStatusService {

    private final CustomerCardStatusRepository repository;

    @Transactional
    public CustomerCardStatus updateStatus(String customerId, boolean cardIssued) {
        CustomerCardStatus status = repository
                .findById(customerId)
                .orElseGet(() -> {
                    CustomerCardStatus newStatus = new CustomerCardStatus();
                    newStatus.setCustomerId(customerId);
                    return newStatus;
                });
        status.setCardIssued(cardIssued);
        status.setUpdatedAt(LocalDateTime.now());
        return repository.save(status);
    }

    public boolean isCardIssued(String customerId) {
        return repository.findById(customerId).map(CustomerCardStatus::isCardIssued).orElse(false);
    }
}
