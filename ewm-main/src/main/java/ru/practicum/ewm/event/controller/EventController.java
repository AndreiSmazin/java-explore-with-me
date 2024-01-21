package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.event.entity.SortingBy;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.service.ParticipationRequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
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

    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsForAdmin(
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(name = "users", required = false) Long[] users,
            @RequestParam(name = "states", required = false) EventState[] states,
            @RequestParam(name = "categories", required = false) Long[] categories,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd) {
        return eventService.getEvents(from, size, users, states, categories, rangeStart, rangeEnd);
    }

    @PatchMapping("/admin/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable(name = "id") long id,
                                           @Valid @RequestBody UpdateEventAdminRequest eventDto) {
        log.debug("Received PATCH-request /admin/events/{} with body: {}", id, eventDto);

        return eventService.updateEventByAdmin(id, eventDto);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsForPublic(
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(name = "text", required = false) @NotBlank String text,
            @RequestParam(name = "categories", required = false) Long[] categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) SortingBy sortingBy,
            HttpServletRequest request) {
        return eventService.getEventsWithFilters(from, size, text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sortingBy, request);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventForPublic(@PathVariable(name = "id") long id, HttpServletRequest request) {
        return eventService.getPublishedEvent(id, request);
    }

    @GetMapping("/events/location/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsInLocation(
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @PathVariable(name = "locationId") long locationId,
            HttpServletRequest request) {
        return eventService.getEventsInLocation(from, size, locationId, request);
    }
}
