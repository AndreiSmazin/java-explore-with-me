package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.entity.Event;

import java.util.List;

public interface EventService {
    EventFullDto createNewEvent(long userId, NewEventDto eventDto);

    List<EventShortDto> getEventsOfUser(int from, int size, long userId);

    Event checkEvent(long id);

    EventFullDto getEventById(long id);

    EventFullDto updateEventByUser(long userId, long id, UpdateEventUserRequest eventDto);
}
