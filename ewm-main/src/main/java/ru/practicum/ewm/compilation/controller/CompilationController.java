package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.event.dto.PaginationParams;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.debug("Received POST-request /admin/compilations with body: {}", compilationDto);

        return compilationService.createNewCompilation(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "id") long id) {
        log.debug("Received DELETE-request /admin/compilations/{} ", id);

        compilationService.deleteCompilation(id);
    }

    @PatchMapping("/admin/compilations/{id}")
    public CompilationDto updateCompilation(@PathVariable(name = "id") long id,
                                            @Valid @RequestBody UpdateCompilationRequest compilationDto) {
        log.debug("Received PATCH-request /admin/compilations/{} with body: {}", id, compilationDto);

        return compilationService.updateCompilation(id, compilationDto);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(
            @Valid PaginationParams paginationParams,
            @RequestParam(name = "pinned", required = false) Boolean pinned) {
        return compilationService.getCompilations(paginationParams, pinned);
    }

    @GetMapping("/compilations/{id}")
    public CompilationDto getCompilationById(@PathVariable(name = "id") long id) {
        return compilationService.getCompilationById(id);
    }
}
