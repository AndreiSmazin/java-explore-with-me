package ru.practicum.ewm.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.participation.entity.ParticipationRequest;
import ru.practicum.ewm.participation.entity.ParticipationRequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequester_Id(Long userId);

    List<ParticipationRequest> findByEvent_Id(Long eventId);

    List<ParticipationRequest> findByEvent_IdAndStatus(Long eventId, ParticipationRequestStatus status);
}
