package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.entity.EventState;
import ru.practicum.ewm.location.dto.LocationShortDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventFullDto extends EventDto {
    private String annotation;
    private CategoryDto category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private long confirmedRequests;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private long id;
    private UserShortDto initiator;
    private LocationShortDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventState state;
    private String title;
    private long views;
}
