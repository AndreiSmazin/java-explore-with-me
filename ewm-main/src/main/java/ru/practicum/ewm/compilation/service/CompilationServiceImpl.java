package ru.practicum.ewm.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.entity.Compilation;
import ru.practicum.ewm.compilation.entity.QCompilation;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
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

    @Override
    public void deleteCompilation(long id) {
        log.debug("+ deleteCompilation: {}", id);

        try {
            compilationRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Compilation", id, e.getMessage());
        }
    }

    @Override
    public CompilationDto updateCompilation(long id, UpdateCompilationRequest compilationDto) {
        log.debug("+ updateCompilation: {}, {}", id, compilationDto);

        Compilation compilation = checkCompilation(id);

        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(compilationDto.getEvents()));
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }

        return compilationMapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public Compilation checkCompilation(long id) {
        return compilationRepository.findById(id).orElseThrow(() -> {
            String message = "No Compilation entity with id " + id + " exists!";
            throw new ObjectNotFoundException("Compilation", id, message);
        });
    }

    @Override
    public List<CompilationDto> getCompilations(int from, int size, Boolean pinned) {
        BooleanExpression predicates = Expressions.asBoolean(true).isTrue();

        if (pinned != null) {
            predicates = predicates.and(QCompilation.compilation.pinned.eq(pinned));
        }

        return compilationRepository.findAll(predicates, PageRequest.of(from, size)).stream()
                .map(compilationMapper::compilationToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long id) {
        return compilationMapper.compilationToCompilationDto(checkCompilation(id));
    }
}
