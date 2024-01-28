package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.event.entity.UserEventStateAction;
import ru.practicum.ewm.location.dto.NewLocationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest extends UpdateEventRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private NewLocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private UserEventStateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
