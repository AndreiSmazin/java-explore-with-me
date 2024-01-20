package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.location.dto.NewLocationDto;

import java.time.LocalDateTime;

public abstract class UpdateEventRequest {
    public abstract String getAnnotation();

    public abstract Long getCategory();

    public abstract String getDescription();

    public abstract LocalDateTime getEventDate();

    public abstract NewLocationDto getLocation();

    public abstract Boolean getPaid();

    public abstract Integer getParticipantLimit();

    public abstract Boolean getRequestModeration();

    public abstract String getTitle();
}
