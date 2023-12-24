package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EndpointHitCreateDto;
import ru.practicum.ewm.dto.EndpointHitResponseDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.service.StatsService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatistics(@RequestParam(name = "start") String start,
                                            @RequestParam(name = "end") String end,
                                            @RequestParam(name = "uris", required = false) String[] uris,
                                            @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        System.out.println(start);
        System.out.println(end);
        return statsService.getStatsByUri(uris, start, end, unique);
    }

    @PostMapping("/hit")
    public EndpointHitResponseDto createEndpointHit(@RequestBody EndpointHitCreateDto endpointHitDto) {
        log.debug("Received POST-request /hit with and body: {}", endpointHitDto);

        return statsService.createNewEndpointHit(endpointHitDto);
    }
}
