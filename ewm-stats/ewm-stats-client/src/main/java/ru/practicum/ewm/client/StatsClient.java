package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.dto.EndpointHitCreateDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.exception.EwmStatsServerException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${ewm-stats-service.url}")
    private String statsServiceUrl;

    public void postEndpointHit(EndpointHitCreateDto endpointHitDto) {
        log.debug("+ postEndpointHit: {}", endpointHitDto);

        String url = statsServiceUrl + "/hit";

        HttpEntity<EndpointHitCreateDto> request = new HttpEntity<>(endpointHitDto, getDefaultHeaders());

        try {
            restTemplate.postForObject(url, request, Object.class);
        } catch (HttpStatusCodeException e) {
            throw new EwmStatsServerException("Stats server error: " + e.getResponseBodyAsString() + " Stats server" +
                    " response status: " + e.getStatusText());
        }
    }

    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, String[] uris,
                                           Boolean unique) {
        log.debug("+ getViewStats: {}, {}, {}, {}", start, end, uris, unique);

        String startEncoded = UriUtils.encode(start.format(timeFormatter), StandardCharsets.UTF_8);
        String endEncoded = UriUtils.encode(end.format(timeFormatter), StandardCharsets.UTF_8);

        Map<String, Object> parameters = Map.of("start", startEncoded, "end", endEncoded, "unique", unique,
                "uris", uris);
        String uri = statsServiceUrl + "/stats?start={start}&end={end}&unique={unique}&uris={uris}";

        ViewStatsDto[] stats;
        try {
            stats = restTemplate.getForObject(uri, ViewStatsDto[].class, parameters);
        } catch (HttpStatusCodeException e) {
            throw new EwmStatsServerException("Stats server error: " + e.getResponseBodyAsString() + " Stats server" +
                    " response status: " + e.getStatusText());
        }

        return List.of(stats);
    }

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }
}
