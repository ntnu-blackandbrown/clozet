package stud.ntnu.no.backend.Category.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.Category.DTOs.CategoryDTO;
import stud.ntnu.no.backend.Category.Exceptions.CategoryNotFoundException;
import stud.ntnu.no.backend.Category.Service.CategoryService;
import stud.ntnu.no.backend.security.config.SecurityConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private CategoryDTO testCategory;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        fixedTime = LocalDateTime.now();
        testCategory = new CategoryDTO();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
        testCategory.setDescription("Electronic items");
        testCategory.setCreatedAt(fixedTime);
        testCategory.setUpdatedAt(fixedTime);
        testCategory.setParentId(null);
    }

    @Test
    void getAllCategories_returnsList() throws Exception {
        // Arrange
        List<CategoryDTO> categories = Arrays.asList(testCategory);
        given(categoryService.getAllCategories()).willReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].description").value("Electronic items"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].updatedAt").exists());
    }

    @Test
    void getCategory_returnsCategory() throws Exception {
        // Arrange
        given(categoryService.getCategory(1L)).willReturn(testCategory);

        // Act & Assert
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic items"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.parentId").doesNotExist());
    }

    @Test
    void createCategory_returnsCreatedCategory() throws Exception {
        // Arrange
        CategoryDTO inputCategory = new CategoryDTO();
        inputCategory.setName("Electronics");
        inputCategory.setDescription("Electronic items");

        given(categoryService.createCategory(any(CategoryDTO.class))).willReturn(testCategory);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic items"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void createCategory_withParentId_returnsCreatedCategory() throws Exception {
        // Arrange
        CategoryDTO inputCategory = new CategoryDTO();
        inputCategory.setName("Laptops");
        inputCategory.setDescription("Laptop computers");
        inputCategory.setParentId(1L);

        CategoryDTO responseCategory = new CategoryDTO();
        responseCategory.setId(2L);
        responseCategory.setName("Laptops");
        responseCategory.setDescription("Laptop computers");
        responseCategory.setParentId(1L);
        responseCategory.setCreatedAt(fixedTime);
        responseCategory.setUpdatedAt(fixedTime);

        given(categoryService.createCategory(any(CategoryDTO.class))).willReturn(responseCategory);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Laptops"))
                .andExpect(jsonPath("$.description").value("Laptop computers"))
                .andExpect(jsonPath("$.parentId").value(1))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getCategory_notFound_returnsNotFound() throws Exception {
        // Arrange
        given(categoryService.getCategory(999L))
                .willThrow(new CategoryNotFoundException("Category not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategory_withInvalidData_returnsBadRequest() throws Exception {
        // Arrange
        CategoryDTO invalidCategory = new CategoryDTO();
        // Leave required fields empty

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest());
    }
}