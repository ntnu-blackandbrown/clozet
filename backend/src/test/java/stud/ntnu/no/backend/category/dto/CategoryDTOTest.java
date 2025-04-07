package stud.ntnu.no.backend.category.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDTOTest {

    @Test
    void testCategoryDTOGettersAndSetters() {
        // Setup test data
        Long id = 1L;
        String name = "Test Category";
        String description = "Test Description";
        Long parentId = 2L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now().plusDays(1);
        List<Long> subcategoryIds = Arrays.asList(3L, 4L, 5L);
        String parentName = "Parent Category";

        // Create instance and set values
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(id);
        categoryDTO.setName(name);
        categoryDTO.setDescription(description);
        categoryDTO.setParentId(parentId);
        categoryDTO.setCreatedAt(createdAt);
        categoryDTO.setUpdatedAt(updatedAt);
        categoryDTO.setSubcategoryIds(subcategoryIds);
        categoryDTO.setParentName(parentName);

        // Verify values are set correctly
        assertEquals(id, categoryDTO.getId());
        assertEquals(name, categoryDTO.getName());
        assertEquals(description, categoryDTO.getDescription());
        assertEquals(parentId, categoryDTO.getParentId());
        assertEquals(createdAt, categoryDTO.getCreatedAt());
        assertEquals(updatedAt, categoryDTO.getUpdatedAt());
        assertEquals(subcategoryIds, categoryDTO.getSubcategoryIds());
        assertEquals(parentName, categoryDTO.getParentName());
    }

    @Test
    void testCategoryDTOValidation() {
        // Create instance with invalid data
        CategoryDTO categoryDTO = new CategoryDTO();
        
        // Name validation - should be not blank and between 3 and 100 chars
        categoryDTO.setName("ab"); // Too short
        // This test would need a validation framework to run
        // In a real test with validation, we would use a Validator instance
        
        // Description validation - should be not blank and max 255 chars
        StringBuilder longDescription = new StringBuilder();
        for (int i = 0; i < 260; i++) {
            longDescription.append("a");
        }
        categoryDTO.setDescription(longDescription.toString()); // Too long
        // This test would need a validation framework to run
    }
} 