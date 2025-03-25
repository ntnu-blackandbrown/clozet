// CategoryService.java
package stud.ntnu.no.backend.Category.Service;

import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.Category.DTOs.CategoryDTO;
import stud.ntnu.no.backend.Category.Exceptions.CategoryNotFoundException;
import stud.ntnu.no.backend.Category.Model.Category;
import stud.ntnu.no.backend.Category.Repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategory(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = convertToEntity(categoryDTO);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return convertToDTO(categoryRepository.save(category));
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }

        return dto;
    }

    private Category convertToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedAt(dto.getCreatedAt());
        category.setUpdatedAt(dto.getUpdatedAt());

        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                .orElseThrow(() -> new CategoryNotFoundException("Parent category not found with id: " + dto.getParentId()));
            category.setParent(parent);
        }

        return category;
    }
}