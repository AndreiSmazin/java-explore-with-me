package ru.practicum.ewm.event.dto;

public abstract class EventDto {
    public abstract void setConfirmedRequests(long number);

    public abstract long getId();
}
