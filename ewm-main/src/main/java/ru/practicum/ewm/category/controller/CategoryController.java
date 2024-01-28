package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.PaginationParams;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.debug("Received POST-request /admin/categories with body: {}", categoryDto);

        return categoryService.createNewCategory(categoryDto);
    }

    @DeleteMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "id") long id) {
        log.debug("Received DELETE-request /admin/categories/{}", id);

        categoryService.deleteCategory(id);
    }

    @PatchMapping("/admin/categories/{id}")
    public CategoryDto updateCategory(@PathVariable(name = "id") long id,
                                      @Valid @RequestBody NewCategoryDto categoryDto) {
        log.debug("Received PATCH-request /admin/categories/{} with body: {}", id, categoryDto);

        return categoryService.updateCategory(id, categoryDto);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@Valid PaginationParams paginationParams) {
        return categoryService.getCategories(paginationParams);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategoryById(@PathVariable(name = "id") long id) {
        return categoryService.getCategoryById(id);
    }
}
