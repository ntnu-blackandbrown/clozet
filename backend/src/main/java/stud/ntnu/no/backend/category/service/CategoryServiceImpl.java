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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the CategoryService interface.
 * Handles business logic for category operations and interacts with the repository layer.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
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
        logger.info("Fetching all categories");
        List<CategoryDTO> categories = categoryMapper.toDtoList(categoryRepository.findAll());
        logger.debug("Found {} categories", categories.size());
        return categories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDTO getCategory(Long id) {
        logger.info("Fetching category with id: {}", id);
        return categoryRepository.findById(id)
            .map(categoryMapper::toDto)
            .orElseThrow(() -> {
                logger.warn("Category not found with id: {}", id);
                return new CategoryNotFoundException("Category not found with id: " + id);
            });
    }

    /**
     * {@inheritDoc}
     * Uses a pageable request to limit results to top 5.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getTopFiveCategories() {
        logger.info("Fetching top five categories");
        Pageable topFive = PageRequest.of(0, 5);
        List<Category> topCategories = categoryRepository.findTopCategoriesByFavoriteCount(topFive);
        List<CategoryDTO> categoryDTOs = categoryMapper.toDtoList(topCategories);
        logger.debug("Found {} top categories", categoryDTOs.size());
        return categoryDTOs;
    }

    /**
     * {@inheritDoc}
     * Sets creation and update timestamps automatically.
     */
    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        logger.info("Creating a new category with name: {}", categoryDTO.getName());
        Category category = categoryMapper.toEntity(categoryDTO);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        Category savedCategory = categoryRepository.save(category);
        logger.debug("Category created with id: {}", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    /**
     * {@inheritDoc}
     * Updates only non-null fields and sets the updatedAt timestamp.
     */
    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        logger.info("Updating category with id: {}", id);
        Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Category not found with id: {}", id);
                return new CategoryNotFoundException("Category not found with id: " + id);
            });

        if (categoryDTO.getName() != null) {
            existingCategory.setName(categoryDTO.getName());
        }
        if (categoryDTO.getDescription() != null) {
            existingCategory.setDescription(categoryDTO.getDescription());
        }

        existingCategory.setUpdatedAt(LocalDateTime.now());
        Category updatedCategory = categoryRepository.save(existingCategory);
        logger.debug("Category updated with id: {}", updatedCategory.getId());
        return categoryMapper.toDto(updatedCategory);
    }

    /**
     * {@inheritDoc}
     * Verifies existence before deletion to provide clear error messages.
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        logger.info("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            logger.warn("Category not found with id: {}", id);
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        logger.debug("Category deleted with id: {}", id);
    }
}
