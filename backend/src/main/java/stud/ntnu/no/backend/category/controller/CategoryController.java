package stud.ntnu.no.backend.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.service.CategoryService;

import java.util.List;

/**
 * REST controller for managing category-related HTTP requests.
 * Provides endpoints for retrieving category information.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

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
        return categoryService.getTopFiveCategories();
    }
    
    /**
     * Retrieves all available categories.
     * 
     * @return List of all category DTOs
     */
    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
