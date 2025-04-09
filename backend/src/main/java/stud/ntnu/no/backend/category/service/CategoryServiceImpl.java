package stud.ntnu.no.backend.category.service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.exception.CategoryNotFoundException;
import stud.ntnu.no.backend.category.mapper.CategoryMapper;
import stud.ntnu.no.backend.category.repository.CategoryRepository;

/**
 * Implementation of the CategoryService interface for managing category operations.
 *
 * <p>This service handles business logic for category management, including creation, retrieval,
 * update, and deletion operations. It interacts with the repository layer for database operations
 * and uses a mapper for entity-DTO conversions.
 *
 * <p>Key responsibilities include:
 *
 * <ul>
 *   <li>Managing the lifecycle of category records
 *   <li>Ensuring data integrity and validation
 *   <li>Providing specialized query operations like finding top categories
 *   <li>Handling timestamps for creation and updates
 * </ul>
 */
@Service
public class CategoryServiceImpl implements CategoryService {

  private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  /**
   * Constructor for dependency injection.
   *
   * <p>Initializes the service with required dependencies allowing for loose coupling and easier
   * testing.
   *
   * @param categoryRepository Repository for database operations on categories
   * @param categoryMapper Mapper for entity-DTO conversions to separate data layers
   */
  public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Implementation retrieves all categories from the repository without filtering. Categories
   * are mapped to DTOs before being returned to the caller.
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
   *
   * <p>Implementation performs a repository lookup by primary key and throws a specific exception
   * if the category is not found, ensuring clear error messages.
   *
   * @throws CategoryNotFoundException with detailed message when the category is not found
   */
  @Override
  public CategoryDTO getCategory(Long id) {
    logger.info("Fetching category with id: {}", id);
    return categoryRepository
        .findById(id)
        .map(categoryMapper::toDto)
        .orElseThrow(
            () -> {
              logger.warn("Category not found with id: {}", id);
              return new CategoryNotFoundException("Category not found with id: " + id);
            });
  }

  /**
   * {@inheritDoc}
   *
   * <p>Implementation uses a multi-level fallback approach to find popular categories:
   *
   * <ol>
   *   <li>First tries to find categories with the most favorites
   *   <li>If none found, falls back to categories with the most items
   *   <li>If still none, returns any categories up to 5
   * </ol>
   *
   * <p>Uses Spring's PageRequest for efficient pagination limited to top 5 results.
   */
  @Override
  @Transactional(readOnly = true)
  public List<CategoryDTO> getTopFiveCategories() {
    logger.info("Fetching top five categories");
    Pageable topFive = PageRequest.of(0, 5);
    List<Category> topCategories = categoryRepository.findTopCategoriesByFavoriteCount(topFive);

    // If no categories with favorites, fall back to categories by item count
    if (topCategories.isEmpty()) {
      logger.info("No categories with favorites found, falling back to item count based ranking");
      topCategories = categoryRepository.findTopCategoriesByItemCount(topFive);

      // If still empty, just get any categories
      if (topCategories.isEmpty()) {
        logger.info("No categories with items found, falling back to any categories");
        topCategories = categoryRepository.findAll(topFive).getContent();
      }
    }

    List<CategoryDTO> categoryDTOs = categoryMapper.toDtoList(topCategories);
    logger.debug("Found {} top categories", categoryDTOs.size());
    return categoryDTOs;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Implementation ensures proper initialization of timestamp fields:
   *
   * <ul>
   *   <li>Sets creation timestamp to current time
   *   <li>Sets update timestamp to current time
   * </ul>
   *
   * <p>Delegates to the repository for persistence and returns the created category with its
   * generated ID.
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
   *
   * <p>Implementation uses a selective update approach:
   *
   * <ul>
   *   <li>Only updates fields that are not null in the DTO
   *   <li>Preserves existing values for fields not specified in the update
   *   <li>Always updates the updatedAt timestamp to track the latest change
   * </ul>
   *
   * @throws CategoryNotFoundException if the category to update cannot be found
   */
  @Override
  @Transactional
  public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
    logger.info("Updating category with id: {}", id);
    Category existingCategory =
        categoryRepository
            .findById(id)
            .orElseThrow(
                () -> {
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
   *
   * <p>Implementation performs a pre-deletion check to:
   *
   * <ul>
   *   <li>Verify the category exists before attempting deletion
   *   <li>Provide a clear error message if the category doesn't exist
   * </ul>
   *
   * <p>Note: This implementation does not check for related items, which could lead to database
   * constraint violations if there are items associated with the category. In production,
   * additional checks should be added.
   *
   * @throws CategoryNotFoundException if the category to delete cannot be found
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
