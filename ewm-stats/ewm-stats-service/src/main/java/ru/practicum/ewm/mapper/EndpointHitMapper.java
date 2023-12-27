package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.EndpointHitCreateDto;
import ru.practicum.ewm.dto.EndpointHitResponseDto;
import ru.practicum.ewm.entity.EndpointHit;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EndpointHitMapper {
    EndpointHit endpointHitCreateDtoToendpointHit(EndpointHitCreateDto endpointHitDto);

    EndpointHitResponseDto endpointHitToEndpointHitResponseDto(EndpointHit endpointHit);
}
