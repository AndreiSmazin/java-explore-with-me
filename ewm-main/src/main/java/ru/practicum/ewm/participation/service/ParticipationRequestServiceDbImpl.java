package ru.practicum.ewm.participation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ViolationOperationRulesException;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.entity.ParticipationRequest;
import ru.practicum.ewm.participation.entity.ParticipationRequestStatus;
import ru.practicum.ewm.participation.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participation.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.entity.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceDbImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final EventService eventService;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto createNewParticipationRequest(long userId, long eventId) {
        log.debug("+ createNewParticipationRequest: {}, {}", userId, eventId);

        ParticipationRequest participationRequest = new ParticipationRequest();

        Event event = eventService.checkEvent(eventId);
        User user = userService.checkUser(userId);

        validateEvent(userId, event);
        participationRequest.setEvent(event);
        participationRequest.setRequester(user);
        participationRequest.setCreated(LocalDateTime.now());

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
            increaseConfirmedRequests(event, 1);
        } else {
            participationRequest.setStatus(ParticipationRequestStatus.PENDING);
        }

        return participationRequestMapper.participationRequestToParticipationRequestDto(
                participationRequestRepository.save(participationRequest));
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsOfUser(long userId) {
        return participationRequestRepository.findByRequester_Id(userId).stream()
                .map(participationRequestMapper::participationRequestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequestByUser(long userId, long id) {
        log.debug("+ cancelParticipationRequestByUser: {}, {}", userId, id);

        ParticipationRequest participationRequest = checkParticipationRequest(id);
        validateParticipationRequest(participationRequest);

        validateRequester(participationRequest, userId);
        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);

        return participationRequestMapper.participationRequestToParticipationRequestDto(
                participationRequestRepository.save(participationRequest)
        );
    }

    @Override
    public ParticipationRequest checkParticipationRequest(long id) {
        return participationRequestRepository.findById(id).orElseThrow(() -> {
            String message = "No ParticipationRequest entity with id " + id + " exists!";
            throw new ObjectNotFoundException("ParticipationRequest", id, message);
        });
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsOfEvent(long userId, long eventId) {
        return participationRequestRepository.findByEvent_Id(eventId).stream()
                .map(participationRequestMapper::participationRequestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateParticipationRequestsOfEvent(
            long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        Event event = eventService.checkEvent(eventId);

        long participants = event.getConfirmedRequests();
        if (event.getParticipantLimit() == participants && event.getParticipantLimit() != 0) {
            throw new ViolationOperationRulesException("Field: eventId. Error: participation limit of event is" +
                    " reached. Value: " + event.getId());
        }
        long currentParticipationLimit = event.getParticipantLimit() - participants;

        List<Long> ids = Arrays.asList(eventRequestStatusUpdateRequest.getRequestIds());
        List<ParticipationRequest> participationRequests = participationRequestRepository.findAllById(ids);
        List<ParticipationRequest> confirmedParticipationRequests;
        List<ParticipationRequest> rejectedParticipationRequests;

        if (eventRequestStatusUpdateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            confirmedParticipationRequests = confirmParticipationRequests(participationRequests,
                    currentParticipationLimit);

            increaseConfirmedRequests(event, confirmedParticipationRequests.size());

            if (confirmedParticipationRequests.size() == currentParticipationLimit) {
                rejectedParticipationRequests = rejectParticipationRequests(
                        participationRequestRepository.findByEvent_IdAndStatus(eventId,
                                ParticipationRequestStatus.PENDING));
            } else {
                rejectedParticipationRequests = new ArrayList<>();
            }
        } else {
            rejectedParticipationRequests = rejectParticipationRequests(participationRequests);
            confirmedParticipationRequests = new ArrayList<>();
        }

        return makeEventRequestStatusUpdateResult(confirmedParticipationRequests, rejectedParticipationRequests);
    }



    private void validateEvent(long userId, Event event) {
        if (userId == event.getInitiator().getId()) {
            throw new ViolationOperationRulesException("Field: eventId. Error: event must not be created by " +
                    "requester. Value: " + event.getId());
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ViolationOperationRulesException("Field: eventId. Error: event must not be unpublished. " +
                    "Value: " + event.getId());
        }
        if (event.getParticipantLimit() == event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new ViolationOperationRulesException("Field: eventId. Error: participation limit of event is" +
                    " reached. Value: " + event.getId());
        }
    }

    private void validateRequester(ParticipationRequest participationRequest, long userId) {
        if (participationRequest.getRequester().getId() != userId) {
            throw new ViolationOperationRulesException("Field: userId. Error: user must be requester of participation" +
                    " request. Value: " + userId);
        }
    }

    private List<ParticipationRequest> confirmParticipationRequests(List<ParticipationRequest> participationRequests,
                                              long currentParticipationLimit) {
        List<ParticipationRequest> targetParticipationRequests = participationRequests.stream()
                .peek((this::validateParticipationRequest))
                .sorted(Comparator.comparing(ParticipationRequest::getCreated))
                .limit(currentParticipationLimit)
                .peek(participationRequest -> participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED))
                .collect(Collectors.toList());

        return participationRequestRepository.saveAll(targetParticipationRequests);
    }

    private List<ParticipationRequest> rejectParticipationRequests(List<ParticipationRequest> participationRequests) {
        List<ParticipationRequest> targetParticipationRequests = participationRequests.stream()
                .peek(this::validateParticipationRequest)
                .peek(participationRequest -> participationRequest.setStatus(ParticipationRequestStatus.REJECTED))
                .collect(Collectors.toList());

        return participationRequestRepository.saveAll(targetParticipationRequests);
    }

    private void validateParticipationRequest(ParticipationRequest participationRequest) {
        if (!participationRequest.getStatus().equals(ParticipationRequestStatus.PENDING)) {
            throw new ViolationOperationRulesException("Field: ids. Error: request status must be PENDING" +
                    " Value: " + participationRequest.getId());
        }
    }

    private EventRequestStatusUpdateResult makeEventRequestStatusUpdateResult(
            List<ParticipationRequest> confirmedParticipationRequests,
            List<ParticipationRequest> rejectedParticipationRequests) {
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();

        eventRequestStatusUpdateResult.setConfirmedRequests(confirmedParticipationRequests.stream()
                .map(participationRequestMapper::participationRequestToParticipationRequestDto)
                .collect(Collectors.toList()));
        eventRequestStatusUpdateResult.setRejectedRequests(rejectedParticipationRequests.stream()
                .map(participationRequestMapper::participationRequestToParticipationRequestDto)
                .collect(Collectors.toList()));

        return eventRequestStatusUpdateResult;
    }

    private void increaseConfirmedRequests(Event event, int number) {
        long participants = event.getConfirmedRequests() + number;
        eventRepository.updateConfirmedRequests(event.getId(), participants);
    }
}
