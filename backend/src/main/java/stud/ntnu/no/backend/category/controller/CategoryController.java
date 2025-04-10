package stud.ntnu.no.backend.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.service.CategoryService;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing category-related HTTP requests.
 * Provides endpoints for retrieving category information.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;

    /**
     * Constructor for dependency injection of the category service.
     * 
     * @param categoryService The service handling category business logic
     */
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves the top five most popular categories based on favorite items.
     * 
     * @return List of the top five category DTOs
     */
    @GetMapping("/top-five")
    public List<CategoryDTO> getTopFiveCategories() {
        logger.info("Request received to get top five categories");
        List<CategoryDTO> categories = categoryService.getTopFiveCategories();
        logger.debug("Returning {} top categories", categories.size());
        return categories;
    }
    
    /**
     * Retrieves all available categories.
     * 
     * @return List of all category DTOs
     */
    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        logger.info("Request received to get all categories");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        logger.debug("Returning {} categories", categories.size());
        return categories;
    }
    
    /**
     * Retrieves a specific category by its ID.
     * 
     * @param id ID of the category to retrieve
     * @return The category DTO
     */
    @GetMapping("/{id}")
    public CategoryDTO getCategory(@PathVariable Long id) {
        logger.info("Request received to get category with id: {}", id);
        return categoryService.getCategory(id);
    }
    
    /**
     * Creates a new category.
     * 
     * @param categoryDTO Data for the new category
     * @return The created category DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@RequestBody CategoryDTO categoryDTO) {
        logger.info("Request received to create a new category");
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        logger.info("Category created with id: {}", createdCategory.getId());
        return createdCategory;
    }
    
    /**
     * Updates an existing category.
     * 
     * @param id ID of the category to update
     * @param categoryDTO New data for the category
     * @return The updated category DTO
     */
    @PutMapping("/{id}")
    public CategoryDTO updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        logger.info("Request received to update category with id: {}", id);
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        logger.info("Category updated: {}", id);
        return updatedCategory;
    }
    
    /**
     * Deletes a category by its ID.
     * 
     * @param id ID of the category to delete
     * @return Empty response with status 204 No Content
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        logger.info("Request received to delete category with id: {}", id);
        categoryService.deleteCategory(id);
        logger.info("Category deleted: {}", id);
    }
}
