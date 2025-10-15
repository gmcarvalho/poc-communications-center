package com.example.communications.domain.repository;

import com.example.communications.domain.model.CommunicationStatus;
import com.example.communications.domain.model.ScheduledCommunication;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledCommunicationRepository
        extends JpaRepository<ScheduledCommunication, UUID> {

    List<ScheduledCommunication> findByStatusAndScheduledAtLessThanEqual(
            CommunicationStatus status, LocalDateTime scheduledAt);
}
