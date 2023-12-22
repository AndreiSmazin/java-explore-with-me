package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitCreateDto;
import ru.practicum.ewm.dto.EndpointHitResponseDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.entity.EndpointHit;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceDbImpl implements StatsService {
    private final EndpointHitMapper endpointHitMapper;
    private final EndpointHitRepository endpointHitRepository;

    @Override
    public EndpointHitResponseDto createNewEndpointHit(EndpointHitCreateDto endpointHitDto) {
        log.debug("+ createNewEndpointHit: {}", endpointHitDto);

        EndpointHit endpointHit = endpointHitMapper.endpointHitCreateDtoToendpointHit(endpointHitDto);

        return endpointHitMapper.endpointHitToEndpointHitResponseDto(endpointHitRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDto> getStatsByUri(String[] uris, LocalDateTime start, LocalDateTime end, boolean unique) {
        if (unique) {

            if (uris == null) {
                return endpointHitRepository.findStatsUniqueId(start, end);
            }
            return endpointHitRepository.findStatsByUriUniqueId(uris, start, end);
        } else {

            if (uris == null) {
                return endpointHitRepository.findStats(start, end);
            }
            return endpointHitRepository.findStatsByUri(uris, start, end);
        }
    }
}
