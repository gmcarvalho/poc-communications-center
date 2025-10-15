package com.example.communications.domain.repository;

import com.example.communications.domain.model.CustomerCardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCardStatusRepository extends JpaRepository<CustomerCardStatus, String> {
}
