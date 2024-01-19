package ru.practicum.ewm.event.validator;

import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.exception.RequestValidationException;
import ru.practicum.ewm.exception.ViolationOperationRulesException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventValidator {
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new RequestValidationException("Field: EventDate. Error: must be not earlier then 2 hour after" +
                    " event creation or changing. Value: " + eventDate.format(timeFormatter));
        }
    }

    public static void validateTimeRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if ((rangeStart != null && rangeEnd != null) && (rangeStart.isAfter(rangeEnd))) {
            throw new RequestValidationException("Field: rangeEnd. Error: must be not earlier then rangeStart." +
                    " Value: " + rangeEnd.format(timeFormatter));
        }
    }

    public static void validateState(EventState state) {
        if (state.equals(EventState.PUBLISHED)) {
            throw new ViolationOperationRulesException("Only pending or canceled events can be changed");
        }
    }

    public static void validateStateForPublishing(EventState state) {
        if (!state.equals(EventState.PENDING)) {
            throw new ViolationOperationRulesException("Only pending events can be published");
        }
    }
}
