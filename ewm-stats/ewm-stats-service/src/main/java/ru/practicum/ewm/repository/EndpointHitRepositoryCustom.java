package ru.practicum.ewm.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.util.List;

public interface EndpointHitRepositoryCustom {
    List<ViewStatsDto> getViewsStats(String[] uris, BooleanExpression predicates);

    List<ViewStatsDto> getUniqueViewsStats(String[] uris, BooleanExpression predicates);
}
