package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.entity.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT NEW ru.practicum.ewm.dto.ViewStatsDto(e.app, e.uri, count(DISTINCT e.ip) AS hits)" +
            " FROM EndpointHit AS e" +
            " WHERE e.uri IN ?1" +
            " AND e.timestamp BETWEEN ?2 AND ?3" +
            " GROUP BY e.app, e.uri" +
            " ORDER BY hits DESC")
    List<ViewStatsDto> findStatsByUriUniqueId(String[] uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.ewm.dto.ViewStatsDto(e.app, e.uri, count(DISTINCT e.ip) AS hits)" +
            " FROM EndpointHit AS e" +
            " WHERE e.timestamp BETWEEN ?1 AND ?2" +
            " GROUP BY e.app, e.uri" +
            " ORDER BY hits DESC")
    List<ViewStatsDto> findStatsUniqueId(LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.ewm.dto.ViewStatsDto(e.app, e.uri, count(e.ip) AS hits)" +
            " FROM EndpointHit AS e" +
            " WHERE e.uri IN ?1" +
            " AND e.timestamp BETWEEN ?2 AND ?3" +
            " GROUP BY e.app, e.uri" +
            " ORDER BY hits DESC")
    List<ViewStatsDto> findStatsByUri(String[] uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.ewm.dto.ViewStatsDto(e.app, e.uri, count(e.ip) AS hits)" +
            " FROM EndpointHit AS e" +
            " WHERE e.timestamp BETWEEN ?1 AND ?2" +
            " GROUP BY e.app, e.uri" +
            " ORDER BY hits DESC")
    List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end);
}
