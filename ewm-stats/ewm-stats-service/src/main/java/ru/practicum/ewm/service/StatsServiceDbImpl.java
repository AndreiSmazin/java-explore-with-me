package ru.practicum.ewm.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.dto.EndpointHitCreateDto;
import ru.practicum.ewm.dto.EndpointHitResponseDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.entity.EndpointHit;
import ru.practicum.ewm.entity.QEndpointHit;
import ru.practicum.ewm.exception.RequestValidationException;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.repository.EndpointHitRepository;
import ru.practicum.ewm.repository.EndpointHitRepositoryCustom;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceDbImpl implements StatsService {
    private final EndpointHitMapper endpointHitMapper;
    private final EndpointHitRepository endpointHitRepository;
    private final EndpointHitRepositoryCustom endpointHitRepositoryCustom;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public EndpointHitResponseDto createNewEndpointHit(EndpointHitCreateDto endpointHitDto) {
        log.debug("+ createNewEndpointHit: {}", endpointHitDto);

        EndpointHit endpointHit = endpointHitMapper.endpointHitCreateDtoToendpointHit(endpointHitDto);

        return endpointHitMapper.endpointHitToEndpointHitResponseDto(endpointHitRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDto> getStatsByUri(String[] uris, String start, String end, boolean unique) {
        LocalDateTime rangeStart = decodeTime(start);
        LocalDateTime rangeEnd = decodeTime(end);

        validateTimeRange(rangeStart, rangeEnd);

        BooleanExpression predicates = Expressions.asBoolean(true).isTrue();

        if (uris != null) {
            predicates.and(QEndpointHit.endpointHit.uri.in(uris));
        }
        predicates.and(QEndpointHit.endpointHit.timestamp.between(rangeStart, rangeEnd));

        if (unique) {
            return endpointHitRepositoryCustom.getUniqueViewsStats(uris, predicates);
        } else {
            return endpointHitRepositoryCustom.getViewsStats(uris, predicates);
        }
    }

    private LocalDateTime decodeTime(String time) {
        return LocalDateTime.parse(UriUtils.decode(time, StandardCharsets.UTF_8), timeFormatter);
    }

    private void validateTimeRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if ((rangeStart != null && rangeEnd != null) && (rangeStart.isAfter(rangeEnd))) {
            throw new RequestValidationException("Field: end. Error: must be not earlier then start." +
                    " Value: " + rangeEnd.format(timeFormatter));
        }
    }
}
