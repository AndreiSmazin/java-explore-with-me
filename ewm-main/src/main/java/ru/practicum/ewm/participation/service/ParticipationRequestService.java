package ru.practicum.ewm.participation.service;

import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.entity.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto createNewParticipationRequest(long userId, long eventId);

    List<ParticipationRequestDto> getParticipationRequestsOfUser(long userId);

    ParticipationRequestDto cancelParticipationRequestByUser(long userId, long id);

    ParticipationRequest checkParticipationRequest(long id);

    List<ParticipationRequestDto> getParticipationRequestsOfEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateParticipationRequestsOfEvent(
            long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
