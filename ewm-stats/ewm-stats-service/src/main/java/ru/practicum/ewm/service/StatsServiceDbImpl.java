package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.dto.EndpointHitCreateDto;
import ru.practicum.ewm.dto.EndpointHitResponseDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.entity.EndpointHit;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.repository.EndpointHitRepository;

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
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndpointHitResponseDto createNewEndpointHit(EndpointHitCreateDto endpointHitDto) {
        log.debug("+ createNewEndpointHit: {}", endpointHitDto);

        EndpointHit endpointHit = endpointHitMapper.endpointHitCreateDtoToendpointHit(endpointHitDto);

        return endpointHitMapper.endpointHitToEndpointHitResponseDto(endpointHitRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDto> getStatsByUri(String[] uris, String start, String end, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(UriUtils.decode(start, StandardCharsets.UTF_8), timeFormatter);
        LocalDateTime endTime = LocalDateTime.parse(UriUtils.decode(end, StandardCharsets.UTF_8), timeFormatter);

        if (unique) {

            if (uris == null) {
                return endpointHitRepository.findStatsUniqueId(startTime, endTime);
            }
            return endpointHitRepository.findStatsByUriUniqueId(uris, startTime, endTime);
        } else {

            if (uris == null) {
                return endpointHitRepository.findStats(startTime, endTime);
            }
            return endpointHitRepository.findStatsByUri(uris, startTime, endTime);
        }
    }
}
