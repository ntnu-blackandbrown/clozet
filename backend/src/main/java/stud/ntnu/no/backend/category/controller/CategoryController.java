package stud.ntnu.no.backend.category.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.service.CategoryService;
import stud.ntnu.no.backend.category.repository.CategoryRepository;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
    }

    // Add to CategoryController.java
    @GetMapping("/top-five")
    public ResponseEntity<List<CategoryDTO>> getTopFiveCategories() {
        return ResponseEntity.ok(categoryService.getTopFiveCategories());
    }
}