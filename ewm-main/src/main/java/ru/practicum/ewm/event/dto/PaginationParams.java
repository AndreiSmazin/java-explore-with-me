package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
public class PaginationParams {
    @Min(0)
    private int from = 0;
    @Min(1)
    @Max(100000)
    private int size = 10;
}
