package ru.practicum.ewm.compilation.dto;

import lombok.Data;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {
    private long id;
    List<EventShortDto> events;
    private boolean pinned;
    private String title;
}
