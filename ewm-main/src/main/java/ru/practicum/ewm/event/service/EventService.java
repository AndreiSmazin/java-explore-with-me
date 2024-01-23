package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.GetEventForAdminRequestParams;
import ru.practicum.ewm.event.dto.GetEventsForPublicRequestParams;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.PaginationParams;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entity.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createNewEvent(long userId, NewEventDto eventDto);

    List<EventShortDto> getEventsOfUser(PaginationParams paginationParams, long userId);

    Event checkEvent(long id);

    EventFullDto getEventById(long id);

    EventFullDto updateEventByUser(long userId, long id, UpdateEventUserRequest eventDto);

    List<EventFullDto> getEvents(GetEventForAdminRequestParams requestParams, PaginationParams paginationParams);

    EventFullDto updateEventByAdmin(long id, UpdateEventAdminRequest eventDto);

    List<EventShortDto> getEventsWithFilters(GetEventsForPublicRequestParams requestParams,
                                             PaginationParams paginationParams, HttpServletRequest request);

    EventFullDto getPublishedEvent(long id, HttpServletRequest request);

    List<EventShortDto> getEventsInLocation(PaginationParams paginationParams, long locationId,
                                            HttpServletRequest request);
}
