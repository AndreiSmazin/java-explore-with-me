package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.event.entity.SortingBy;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GetEventsForPublicRequestParams {
    private String text;
    private Long[] categories;
    private Boolean paid;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;
    private boolean onlyAvailable = false;
    private SortingBy sortingBy;
}

