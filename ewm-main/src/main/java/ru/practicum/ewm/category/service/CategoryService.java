package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createNewCategory(NewCategoryDto categoryDto);

    void deleteCategory(long id);

    CategoryDto updateCategory(long id, NewCategoryDto categoryDto);

    Category checkCategory(long id);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long id);
}
