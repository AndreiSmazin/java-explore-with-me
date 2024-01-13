package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.entity.Compilation;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceDbImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createNewCompilation(NewCompilationDto compilationDto) {
        log.debug("+ createNewCompilation: {}", compilationDto);

        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(compilationDto);

        if (compilationDto.getEvents() == null) {
            compilation.setEvents(new ArrayList<>());
        } else {
            compilation.setEvents(eventRepository.findAllById(compilationDto.getEvents()));
        }

        return compilationMapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }
}
