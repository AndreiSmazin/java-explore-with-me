package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.PaginationParams;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.event.entity.SortingBy;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createNewEvent(long userId, NewEventDto eventDto);

    List<EventShortDto> getEventsOfUser(int from, int size, long userId);

    Event checkEvent(long id);

    EventFullDto getEventById(long id);

    EventFullDto updateEventByUser(long userId, long id, UpdateEventUserRequest eventDto);

    List<EventFullDto> getEvents(int from, int size, Long[] users, EventState[] states, Long[] categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd);

    EventFullDto updateEventByAdmin(long id, UpdateEventAdminRequest eventDto);

    List<EventShortDto> getEventsWithFilters(int from, int size, String text, Long[] categories, Boolean paid,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                             SortingBy sortingBy, HttpServletRequest request);

    EventFullDto getPublishedEvent(long id, HttpServletRequest request);

    List<EventShortDto> getEventsInLocation(PaginationParams paginationParams, long locationId, HttpServletRequest request);
}
