package ru.practicum.ewm.participation.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.entity.ParticipationRequest;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ParticipationRequestMapper {
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "requester", ignore = true)
    ParticipationRequestDto participationRequestToParticipationRequestDto(ParticipationRequest participationRequest);
}
