package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.event.entity.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GetEventForAdminRequestParams {
    private Long[] users;
    private EventState[] states;
    private Long[] categories;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

}
