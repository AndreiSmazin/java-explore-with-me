package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.GetEventForAdminRequestParams;
import ru.practicum.ewm.event.dto.GetEventsForPublicRequestParams;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.PaginationParams;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.service.ParticipationRequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    public List<EventShortDto> getEventsOfUser(@Valid PaginationParams paginationParams,
                                               @PathVariable(name = "userId") long userId) {
        return eventService.getEventsOfUser(paginationParams, userId);
    }

    @GetMapping("/users/{userId}/events/{id}")
    public EventFullDto getEventById(@PathVariable(name = "userId") long userId,
                                     @PathVariable(name = "id") long id) {
        return eventService.getEventById(id);
    }

    @PatchMapping("/users/{userId}/events/{id}")
    public EventFullDto updateEventByUser(@PathVariable(name = "userId") long userId,
                                          @PathVariable(name = "id") long id,
                                          @Valid @RequestBody UpdateEventUserRequest eventDto) {
        log.debug("Received PATCH-request /users/{}/events/{} with body: {}", userId, id, eventDto);

        return eventService.updateEventByUser(userId, id, eventDto);
    }

    @GetMapping("/users/{userId}/events/{id}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsOfEvent(@PathVariable(name = "userId") long userId,
                                                                         @PathVariable(name = "id") long id) {
        return participationRequestService.getParticipationRequestsOfEvent(userId, id);
    }

    @PatchMapping("/users/{userId}/events/{id}/requests")
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
    public List<EventFullDto> getEventsForAdmin(@Valid PaginationParams paginationParams,
                                                GetEventForAdminRequestParams requestParams) {
        return eventService.getEvents(requestParams, paginationParams);
    }

    @PatchMapping("/admin/events/{id}")
    public EventFullDto updateEventByAdmin(@PathVariable(name = "id") long id,
                                           @Valid @RequestBody UpdateEventAdminRequest eventDto) {
        log.debug("Received PATCH-request /admin/events/{} with body: {}", id, eventDto);

        return eventService.updateEventByAdmin(id, eventDto);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsForPublic(GetEventsForPublicRequestParams requestParams,
                                                  @Valid PaginationParams paginationParams,
                                                  HttpServletRequest request) {
        return eventService.getEventsWithFilters(requestParams, paginationParams, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventForPublic(@PathVariable(name = "id") long id, HttpServletRequest request) {
        return eventService.getPublishedEvent(id, request);
    }

    @GetMapping("/events/location/{locationId}")
    public List<EventShortDto> getEventsInLocation(@Valid PaginationParams paginationParams,
                                                   @PathVariable(name = "locationId") long locationId,
                                                   HttpServletRequest request) {
        return eventService.getEventsInLocation(paginationParams, locationId, request);
    }
}
