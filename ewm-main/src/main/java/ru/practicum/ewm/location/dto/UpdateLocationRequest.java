package ru.practicum.ewm.location.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdateLocationRequest {
    @NotBlank
    @Size(min = 3, max = 120)
    private String name;
    @NotBlank
    @Size(min = 20, max = 2000)
    private String description;
    @Min(-90)
    @Max(90)
    private Float lat;
    @Min(-180)
    @Max(180)
    private Float lon;
}
