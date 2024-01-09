package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createNewCategory(CategoryCreateDto categoryDto);

    void deleteCategory(long id);

    CategoryResponseDto updateCategory(long id, CategoryCreateDto categoryDto);

    boolean checkCategory(long id);

    List<CategoryResponseDto> getCategories(int from, int size);

    CategoryResponseDto getCategoryById(long id);
}
