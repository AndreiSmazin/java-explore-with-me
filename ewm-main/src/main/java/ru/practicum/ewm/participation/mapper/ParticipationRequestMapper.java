package ru.practicum.ewm.participation.mapper;


import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation.entity.ParticipationRequest;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public abstract class ParticipationRequestMapper {
    @AfterMapping
    protected void setEvent(ParticipationRequest participationRequest,
                            @MappingTarget ParticipationRequestDto participationRequestDto) {
        participationRequestDto.setEvent(participationRequest.getEvent().getId());
        participationRequestDto.setRequester(participationRequest.getRequester().getId());
    }

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "event", ignore = true)
    public abstract ParticipationRequestDto participationRequestToParticipationRequestDto(
            ParticipationRequest participationRequest);
}
