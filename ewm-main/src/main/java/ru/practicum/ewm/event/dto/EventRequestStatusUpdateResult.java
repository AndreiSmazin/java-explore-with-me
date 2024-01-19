package ru.practicum.ewm.event.dto;

import lombok.Data;
import ru.practicum.ewm.participation.dto.ParticipationRequestDto;

import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
