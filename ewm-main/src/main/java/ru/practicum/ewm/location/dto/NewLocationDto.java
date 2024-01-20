package ru.practicum.ewm.location.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewLocationDto {
    @NotNull
    private float lat;
    @NotNull
    private float lon;
}
