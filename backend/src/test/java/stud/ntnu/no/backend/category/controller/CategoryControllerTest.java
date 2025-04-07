package stud.ntnu.no.backend.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.service.CategoryService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void getTopFiveCategories_ShouldReturnListOfCategories() throws Exception {
        // Given
        CategoryDTO category1 = new CategoryDTO();
        category1.setId(1L);
        category1.setName("Category 1");
        
        CategoryDTO category2 = new CategoryDTO();
        category2.setId(2L);
        category2.setName("Category 2");
        
        List<CategoryDTO> categories = Arrays.asList(category1, category2);
        
        when(categoryService.getTopFiveCategories()).thenReturn(categories);

        // When/Then
        mockMvc.perform(get("/api/categories/top-five"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Category 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Category 2"))
                .andDo(document("categories-top-five",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Category ID"),
                                fieldWithPath("[].name").description("Category name"),
                                fieldWithPath("[].description").description("Category description"),
                                fieldWithPath("[].parentId").description("Parent category ID"),
                                fieldWithPath("[].createdAt").description("Date and time when category was created"),
                                fieldWithPath("[].updatedAt").description("Date and time when category was last updated"),
                                fieldWithPath("[].subcategoryIds").description("List of subcategory IDs"),
                                fieldWithPath("[].parentName").description("Name of the parent category")
                        )
                ));
                
        verify(categoryService).getTopFiveCategories();
    }

    @Test
    void getAllCategories_ShouldReturnListOfAllCategories() throws Exception {
        // Given
        CategoryDTO category1 = new CategoryDTO();
        category1.setId(1L);
        category1.setName("Category 1");
        
        CategoryDTO category2 = new CategoryDTO();
        category2.setId(2L);
        category2.setName("Category 2");
        
        List<CategoryDTO> categories = Arrays.asList(category1, category2);
        
        when(categoryService.getAllCategories()).thenReturn(categories);

        // When/Then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Category 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Category 2"))
                .andDo(document("categories-all",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Category ID"),
                                fieldWithPath("[].name").description("Category name"),
                                fieldWithPath("[].description").description("Category description"),
                                fieldWithPath("[].parentId").description("Parent category ID"),
                                fieldWithPath("[].createdAt").description("Date and time when category was created"),
                                fieldWithPath("[].updatedAt").description("Date and time when category was last updated"),
                                fieldWithPath("[].subcategoryIds").description("List of subcategory IDs"),
                                fieldWithPath("[].parentName").description("Name of the parent category")
                        )
                ));
                
        verify(categoryService).getAllCategories();
    }
} 