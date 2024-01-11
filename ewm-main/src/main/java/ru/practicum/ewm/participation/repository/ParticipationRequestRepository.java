package ru.practicum.ewm.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.participation.entity.ParticipationRequest;
import ru.practicum.ewm.participation.entity.ParticipationRequestStatus;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    long countByEvent_IdAndStatus(Long eventId, ParticipationRequestStatus status);
}
