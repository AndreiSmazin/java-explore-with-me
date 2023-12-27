package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitCreateDto;
import ru.practicum.ewm.dto.EndpointHitResponseDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    EndpointHitResponseDto createNewEndpointHit(EndpointHitCreateDto endpointHitDto);

    List<ViewStatsDto> getStatsByUri(String[] uris, String start, String end, boolean unique);
}
