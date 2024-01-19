package ru.practicum.ewm.participation.validator;

import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.exception.ViolationOperationRulesException;
import ru.practicum.ewm.participation.entity.ParticipationRequest;
import ru.practicum.ewm.participation.entity.ParticipationRequestStatus;

public class ParticipationRequestValidator {
    public static void validateEvent(long userId, Event event) {
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

    public static void validateRequester(ParticipationRequest participationRequest, long userId) {
        if (participationRequest.getRequester().getId() != userId) {
            throw new ViolationOperationRulesException("Field: userId. Error: user must be requester of participation" +
                    " request. Value: " + userId);
        }
    }

    public static void validateParticipationRequest(ParticipationRequest participationRequest) {
        if (!participationRequest.getStatus().equals(ParticipationRequestStatus.PENDING)) {
            throw new ViolationOperationRulesException("Field: ids. Error: request status must be PENDING" +
                    " Value: " + participationRequest.getId());
        }
    }
}
