package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.entity.Compilation;

import java.util.List;

public interface CompilationService {
    CompilationDto createNewCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(long id);

    Compilation checkCompilation(long id);

    CompilationDto updateCompilation(long id, UpdateCompilationRequest compilationDto);

    List<CompilationDto> getCompilations(int from, int size, Boolean pinned);

    CompilationDto getCompilationById(long id);
}
