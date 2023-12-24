package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.dto.EndpointHitCreateDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${ewm-stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> postEndpointHit(EndpointHitCreateDto endpointHitDto) {
        log.debug("+ postEndpointHit: {}", endpointHitDto);

        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getViewStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        log.debug("+ getViewStats: {}, {}, {}, {}", start, end, uris, unique);

        String startEncoded = UriUtils.encode(start.format(timeFormatter), StandardCharsets.UTF_8);
        String endEncoded = UriUtils.encode(end.format(timeFormatter), StandardCharsets.UTF_8);

        Map<String, Object> parameters = Map.of("start", startEncoded, "end", endEncoded, "unique", unique,
                "uris", uris);
        String path = "/stats?start={start}&end={end}&unique={unique}&uris={uris}";


        return get(path.toString(), parameters);
    }
}
