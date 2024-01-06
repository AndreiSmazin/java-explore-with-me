package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;

public interface CategoryService {
    CategoryResponseDto createNewCategory(CategoryCreateDto categoryDto);
}
