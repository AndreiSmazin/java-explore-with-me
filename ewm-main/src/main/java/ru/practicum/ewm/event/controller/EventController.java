package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.service.ParticipationRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(name = "userId") long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        log.debug("Received POST-request /users/{}/events with body: {}", userId, eventDto);

        return eventService.createNewEvent(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsOfUser(
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @PathVariable(name = "userId") long userId) {
        return eventService.getEventsOfUser(from, size, userId);
    }

    @GetMapping("/users/{userId}/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable(name = "userId") long userId,
                                     @PathVariable(name = "id") long id) {
        return eventService.getEventById(id);
    }

    @PatchMapping("/users/{userId}/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByUser(@PathVariable(name = "userId") long userId,
                                          @PathVariable(name = "id") long id,
                                          @Valid @RequestBody UpdateEventUserRequest eventDto) {
        log.debug("Received PATCH-request /users/{}/events/{} with body: {}", userId, id, eventDto);

        return eventService.updateEventByUser(userId, id, eventDto);
    }

    @GetMapping("/users/{userId}/events/{id}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getParticipationRequestsOfEvent(@PathVariable(name = "userId") long userId,
                                                                         @PathVariable(name = "id") long id) {
        return participationRequestService.getParticipationRequestsOfEvent(userId, id);
    }

    @PatchMapping("/users/{userId}/events/{id}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateParticipationRequestsOfEvent(
            @PathVariable(name = "userId") long userId,
            @PathVariable(name = "id") long id,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.debug("Received PATCH-request /users/{}/events/{}/requests with body: {}", userId, id,
                eventRequestStatusUpdateRequest);

        return participationRequestService.updateParticipationRequestsOfEvent(userId, id,
                eventRequestStatusUpdateRequest);
    }
}
