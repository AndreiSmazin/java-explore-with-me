package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.entity.Category;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceDbImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto createNewCategory(CategoryCreateDto categoryDto) {
        log.debug("+ createNewCategory: {}", categoryDto);

        Category newCategory = categoryMapper.categoryCreateDtoToCategory(categoryDto);

        return categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(newCategory));
    }
}
