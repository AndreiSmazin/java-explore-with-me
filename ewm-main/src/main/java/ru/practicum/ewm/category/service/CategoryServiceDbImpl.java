package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryCreateDto;
import ru.practicum.ewm.category.dto.CategoryResponseDto;
import ru.practicum.ewm.category.entity.Category;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public void deleteCategory(long id) {
        log.debug("+ deleteCategory: {}", id);

        //Сделать валидацию отсутствия событий

        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Category", id, e.getMessage());
        }
    }

    @Override
    public CategoryResponseDto updateCategory(long id, CategoryCreateDto categoryDto) {
        log.debug("+ updateCategory: {}, {}", id, categoryDto);

        if (!checkCategory(id)) {
            String massage = "No Category entity with id " + id + " exists!";
            throw new ObjectNotFoundException("Category", id, massage);
        }

        Category category = categoryMapper.categoryCreateDtoToCategory(categoryDto);
        category.setId(id);

        return categoryMapper.categoryToCategoryResponseDto(categoryRepository.save(category));
    }

    @Override
    public boolean checkCategory(long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public List<CategoryResponseDto> getCategories(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).stream()
                .map(categoryMapper::categoryToCategoryResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryById(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            String massage = "No Category entity with id " + id + " exists!";
            throw new ObjectNotFoundException("Category", id, massage);
        });

        return categoryMapper.categoryToCategoryResponseDto(category);
    }
}
