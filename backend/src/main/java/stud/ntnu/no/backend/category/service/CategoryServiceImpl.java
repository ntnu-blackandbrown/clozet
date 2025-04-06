package stud.ntnu.no.backend.category.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.exception.CategoryNotFoundException;
import stud.ntnu.no.backend.category.mapper.CategoryMapper;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the CategoryService interface.
 * Handles business logic for category operations and interacts with the repository layer.
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Constructor for dependency injection.
     *
     * @param categoryRepository Repository for database operations
     * @param categoryMapper Mapper for entity-DTO conversions
     */
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.toDtoList(categoryRepository.findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDTO getCategory(Long id) {
        return categoryRepository.findById(id)
            .map(categoryMapper::toDto)
            .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    /**
     * {@inheritDoc}
     * Uses a pageable request to limit results to top 5.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getTopFiveCategories() {
        Pageable topFive = PageRequest.of(0, 5);
        List<Category> topCategories = categoryRepository.findTopCategoriesByFavoriteCount(topFive);
        return categoryMapper.toDtoList(topCategories);
    }

    /**
     * {@inheritDoc}
     * Sets creation and update timestamps automatically.
     */
    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    /**
     * {@inheritDoc}
     * Updates only non-null fields and sets the updatedAt timestamp.
     */
    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        if (categoryDTO.getName() != null) {
            existingCategory.setName(categoryDTO.getName());
        }
        if (categoryDTO.getDescription() != null) {
            existingCategory.setDescription(categoryDTO.getDescription());
        }

        existingCategory.setUpdatedAt(LocalDateTime.now());
        return categoryMapper.toDto(categoryRepository.save(existingCategory));
    }

    /**
     * {@inheritDoc}
     * Verifies existence before deletion to provide clear error messages.
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
