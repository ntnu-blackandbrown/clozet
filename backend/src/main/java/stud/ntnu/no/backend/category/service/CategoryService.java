package stud.ntnu.no.backend.category.service;

import stud.ntnu.no.backend.category.dto.CategoryDTO;

import java.util.List;

/**
 * Service interface defining operations for category management.
 * Provides methods for CRUD operations and specialized queries.
 */
public interface CategoryService {
    /**
     * Retrieves all categories in the system.
     *
     * @return List of all category DTOs
     */
    List<CategoryDTO> getAllCategories();
    
    /**
     * Retrieves a specific category by its ID.
     *
     * @param id ID of the category to retrieve
     * @return The category DTO
     * @throws stud.ntnu.no.backend.category.exception.CategoryNotFoundException if the category is not found
     */
    CategoryDTO getCategory(Long id);
    
    /**
     * Creates a new category based on the provided data.
     *
     * @param categoryDTO Data for the new category
     * @return The created category DTO with generated ID
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    
    /**
     * Updates an existing category with new data.
     *
     * @param id ID of the category to update
     * @param categoryDTO New data for the category
     * @return The updated category DTO
     * @throws stud.ntnu.no.backend.category.exception.CategoryNotFoundException if the category is not found
     */
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    
    /**
     * Deletes a category by its ID.
     *
     * @param id ID of the category to delete
     * @throws stud.ntnu.no.backend.category.exception.CategoryNotFoundException if the category is not found
     */
    void deleteCategory(Long id);
    
    /**
     * Retrieves the top five most popular categories based on favorite items count.
     *
     * @return List of the top five category DTOs
     */
    List<CategoryDTO> getTopFiveCategories();
}
