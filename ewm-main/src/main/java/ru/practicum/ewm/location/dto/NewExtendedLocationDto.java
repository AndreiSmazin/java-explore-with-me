package ru.practicum.ewm.location.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NewExtendedLocationDto {
    @NotBlank
    @Size(min = 3, max = 120)
    private String name;
    @NotBlank
    @Size(min = 20, max = 2000)
    private String description;
    @NotNull
    @Min(-90)
    @Max(90)
    private Float lat;
    @NotNull
    @Min(-180)
    @Max(180)
    private Float lon;
}
