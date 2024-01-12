package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.participation.entity.ParticipationRequestStatus;

@Data
public class EventRequestStatusUpdateRequest {
    private Long[] requestIds;
    private ParticipationRequestStatus status;
}
