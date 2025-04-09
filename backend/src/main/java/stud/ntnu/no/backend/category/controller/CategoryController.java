package stud.ntnu.no.backend.category.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.service.CategoryService;

/**
 * REST controller for managing category-related HTTP requests. Provides endpoints for retrieving
 * category information.
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
}
