package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.entity.Event;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EventMapper {
    EventFullDto eventToEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(source = "paid", target = "paid", defaultValue = "false")
    @Mapping(source = "participantLimit", target = "participantLimit", defaultValue = "0")
    @Mapping(source = "requestModeration", target = "requestModeration", defaultValue = "true")
    Event newEventDtoToEvent(NewEventDto eventDto);

    EventShortDto eventToEventShortDto(Event event);
}
