package ru.practicum.ewm.participation.service;

import ru.practicum.ewm.participation.dto.ParticipationRequestDto;

public interface ParticipationRequestService {
    ParticipationRequestDto createNewParticipationRequest(long userId, long eventId);
}
