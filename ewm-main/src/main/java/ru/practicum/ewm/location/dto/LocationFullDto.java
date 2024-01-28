package ru.practicum.ewm.location.dto;

import lombok.Data;

@Data
public class LocationFullDto {
    private long id;
    private String name;
    private String description;
    private float lat;
    private float lon;
}
