package ru.practicum.ewm.repository;

import com.querydsl.core.BooleanBuilder;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.util.List;

public interface EndpointHitRepositoryCustom {
    List<ViewStatsDto> getViewsStats(String[] uris, BooleanBuilder predicates);

    List<ViewStatsDto> getUniqueViewsStats(String[] uris, BooleanBuilder predicates);
}
