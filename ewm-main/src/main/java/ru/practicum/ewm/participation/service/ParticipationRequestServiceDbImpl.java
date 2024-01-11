package ru.practicum.ewm.participation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ViolationOperationRulesException;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.entity.ParticipationRequest;
import ru.practicum.ewm.participation.entity.ParticipationRequestStatus;
import ru.practicum.ewm.participation.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participation.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.entity.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceDbImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final EventService eventService;
    private final UserService userService;

    @Override
    public ParticipationRequestDto createNewParticipationRequest(long userId, long eventId) {
        log.debug("+ createNewParticipationRequest: {}, {}", userId, eventId);

        ParticipationRequest participationRequest = new ParticipationRequest();

        Event event = eventService.checkEvent(eventId);
        validateEventState(event);
        validateEventParticipationLimit(event);
        participationRequest.setEvent(event);

        User user = userService.checkUser(userId);
        validateRequester(userId, event);
        participationRequest.setRequester(user);

        participationRequest.setCreated(LocalDateTime.now());

        if (event.isRequestModeration()) {
            participationRequest.setStatus(ParticipationRequestStatus.PENDING);
        } else {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
        }

        ParticipationRequestDto participationRequestDto =
                participationRequestMapper.participationRequestToParticipationRequestDto(
                        participationRequestRepository.save(participationRequest));

        participationRequestDto.setEvent(eventId);
        participationRequestDto.setRequester(userId);

        return participationRequestDto;
    }

    private void validateRequester(long userId, Event event) {
        if (userId == event.getInitiator().getId()) {
            throw new ViolationOperationRulesException("Field: eventId. Error: event must not be created by " +
                    "requester. Value: " + event.getId());
        }
    }

    private void validateEventState(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ViolationOperationRulesException("Field: eventId. Error: event must not be unpublished. " +
                    "Value: " + event.getId());
        }
    }

    private void validateEventParticipationLimit(Event event) {
        long participants = participationRequestRepository.countByEvent_IdAndStatus(event.getId(),
                ParticipationRequestStatus.CONFIRMED);
        if (event.getParticipantLimit() == participants && event.getParticipantLimit() != 0) {
            throw new ViolationOperationRulesException("Field: eventId. Error: participation limit of event is" +
                    " reached. Value: " + event.getId());
        }
    }
}
