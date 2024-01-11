package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
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
    public CategoryDto createNewCategory(NewCategoryDto categoryDto) {
        log.debug("+ createNewCategory: {}", categoryDto);

        Category newCategory = categoryMapper.newCategoryDtoToCategory(categoryDto);

        return categoryMapper.categoryToCategoryDto(categoryRepository.save(newCategory));
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
    public CategoryDto updateCategory(long id, NewCategoryDto categoryDto) {
        log.debug("+ updateCategory: {}, {}", id, categoryDto);

        Category category = checkCategory(id);
        category.setName(categoryDto.getName());

        return categoryMapper.categoryToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public Category checkCategory(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            String message = "No Category entity with id " + id + " exists!";
            throw new ObjectNotFoundException("Category", id, message);
        });
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).stream()
                .map(categoryMapper::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long id) {
        return categoryMapper.categoryToCategoryDto(checkCategory(id));
    }
}
