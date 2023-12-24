package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHitCreateDto;

import java.time.LocalDateTime;

/**
 * Тестовый контроллер для проверки работы ewm-stats-client
 */
@RestController
@RequestMapping("/stat-test")
@RequiredArgsConstructor
public class StatsController {
    private final StatsClient statsClient;

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistics(@RequestParam(name = "start")
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                @RequestParam(name = "end")
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                @RequestParam(name = "uris", required = false) String[] uris,
                                                @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return statsClient.getViewStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public ResponseEntity<Object> createEndpointHit(@RequestBody EndpointHitCreateDto endpointHitDto) {

        return statsClient.postEndpointHit(endpointHitDto);
    }

}
