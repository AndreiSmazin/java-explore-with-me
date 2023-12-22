package ru.practicum.ewm.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EndpointHitResponseDto {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
