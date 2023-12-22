package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHitCreateDto;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {
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
        Map<String, Object> parameters = Map.of("start", start, "end", end, "unique", unique);
        StringBuilder path = new StringBuilder("/stats?start={start}&end={end}&unique={unique}");

        if (uris != null) {
            for (String uri : uris) {
                path.append("&uris={uris}");
                parameters.put("uris", uris);
            }
        }

        return get(path.toString(), parameters);
    }
}
