package com.example.communications.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "customer_card_status")
public class CustomerCardStatus {

    @Id
    private String customerId;

    @Column(nullable = false)
    private boolean cardIssued;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
