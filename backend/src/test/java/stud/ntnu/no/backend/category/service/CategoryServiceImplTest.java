package stud.ntnu.no.backend.category.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.exception.CategoryNotFoundException;
import stud.ntnu.no.backend.category.mapper.CategoryMapper;
import stud.ntnu.no.backend.category.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

  @Mock private CategoryRepository categoryRepository;

  @Mock private CategoryMapper categoryMapper;

  @InjectMocks private CategoryServiceImpl categoryService;

  private Category category1;
  private Category category2;
  private CategoryDTO categoryDTO1;
  private CategoryDTO categoryDTO2;

  @BeforeEach
  void setUp() {
    // Opprett test data
    category1 = new Category();
    category1.setId(1L);
    category1.setName("Electronics");
    category1.setDescription("Electronic gadgets");
    category1.setCreatedAt(LocalDateTime.now());
    category1.setUpdatedAt(LocalDateTime.now());

    category2 = new Category();
    category2.setId(2L);
    category2.setName("Clothing");
    category2.setDescription("Fashion items");
    category2.setCreatedAt(LocalDateTime.now());
    category2.setUpdatedAt(LocalDateTime.now());

    categoryDTO1 = new CategoryDTO();
    categoryDTO1.setId(1L);
    categoryDTO1.setName("Electronics");
    categoryDTO1.setDescription("Electronic gadgets");

    categoryDTO2 = new CategoryDTO();
    categoryDTO2.setId(2L);
    categoryDTO2.setName("Clothing");
    categoryDTO2.setDescription("Fashion items");
  }

  @Test
  void getAllCategories_shouldReturnAllCategories() {
    // Arrange
    when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
    when(categoryMapper.toDtoList(anyList())).thenReturn(Arrays.asList(categoryDTO1, categoryDTO2));

    // Act
    List<CategoryDTO> result = categoryService.getAllCategories();

    // Assert
    assertEquals(2, result.size());
    assertEquals("Electronics", result.get(0).getName());
    assertEquals("Clothing", result.get(1).getName());
    verify(categoryRepository, times(1)).findAll();
    verify(categoryMapper, times(1)).toDtoList(anyList());
  }

  @Test
  void getCategory_withValidId_shouldReturnCategory() {
    // Arrange
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
    when(categoryMapper.toDto(category1)).thenReturn(categoryDTO1);

    // Act
    CategoryDTO result = categoryService.getCategory(1L);

    // Assert
    assertNotNull(result);
    assertEquals("Electronics", result.getName());
    verify(categoryRepository, times(1)).findById(1L);
    verify(categoryMapper, times(1)).toDto(category1);
  }

  @Test
  void getCategory_withInvalidId_shouldThrowException() {
    // Arrange
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        CategoryNotFoundException.class,
        () -> {
          categoryService.getCategory(99L);
        });
    verify(categoryRepository, times(1)).findById(99L);
  }

  @Test
  void getTopFiveCategories_shouldReturnTopCategories() {
    // Arrange
    Pageable topFive = PageRequest.of(0, 5);
    when(categoryRepository.findTopCategoriesByFavoriteCount(topFive))
        .thenReturn(Arrays.asList(category1, category2));
    when(categoryMapper.toDtoList(anyList())).thenReturn(Arrays.asList(categoryDTO1, categoryDTO2));

    // Act
    List<CategoryDTO> result = categoryService.getTopFiveCategories();

    // Assert
    assertEquals(2, result.size());
    verify(categoryRepository, times(1)).findTopCategoriesByFavoriteCount(any(Pageable.class));
    verify(categoryMapper, times(1)).toDtoList(anyList());
  }

  @Test
  void createCategory_shouldReturnCreatedCategory() {
    // Arrange
    CategoryDTO inputDTO = new CategoryDTO();
    inputDTO.setName("Sports");
    inputDTO.setDescription("Sports equipment");

    Category newCategory = new Category();
    newCategory.setName("Sports");
    newCategory.setDescription("Sports equipment");

    Category savedCategory = new Category();
    savedCategory.setId(3L);
    savedCategory.setName("Sports");
    savedCategory.setDescription("Sports equipment");
    savedCategory.setCreatedAt(LocalDateTime.now());
    savedCategory.setUpdatedAt(LocalDateTime.now());

    CategoryDTO savedDTO = new CategoryDTO();
    savedDTO.setId(3L);
    savedDTO.setName("Sports");
    savedDTO.setDescription("Sports equipment");

    when(categoryMapper.toEntity(inputDTO)).thenReturn(newCategory);
    when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
    when(categoryMapper.toDto(savedCategory)).thenReturn(savedDTO);

    // Act
    CategoryDTO result = categoryService.createCategory(inputDTO);

    // Assert
    assertNotNull(result);
    assertEquals(3L, result.getId());
    assertEquals("Sports", result.getName());
    verify(categoryMapper, times(1)).toEntity(inputDTO);
    verify(categoryRepository, times(1)).save(any(Category.class));
    verify(categoryMapper, times(1)).toDto(savedCategory);
  }

  @Test
  void updateCategory_withValidId_shouldReturnUpdatedCategory() {
    // Arrange
    CategoryDTO inputDTO = new CategoryDTO();
    inputDTO.setName("Updated Electronics");
    inputDTO.setDescription("Updated description");

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
    when(categoryRepository.save(any(Category.class))).thenReturn(category1);
    when(categoryMapper.toDto(category1)).thenReturn(categoryDTO1);

    // Act
    CategoryDTO result = categoryService.updateCategory(1L, inputDTO);

    // Assert
    assertNotNull(result);
    verify(categoryRepository, times(1)).findById(1L);
    verify(categoryRepository, times(1)).save(category1);
    verify(categoryMapper, times(1)).toDto(category1);
  }

  @Test
  void updateCategory_withInvalidId_shouldThrowException() {
    // Arrange
    CategoryDTO inputDTO = new CategoryDTO();
    inputDTO.setName("Updated Electronics");

    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        CategoryNotFoundException.class,
        () -> {
          categoryService.updateCategory(99L, inputDTO);
        });
    verify(categoryRepository, times(1)).findById(99L);
    verify(categoryRepository, never()).save(any(Category.class));
  }

  @Test
  void deleteCategory_withValidId_shouldDeleteCategory() {
    // Arrange
    when(categoryRepository.existsById(1L)).thenReturn(true);
    doNothing().when(categoryRepository).deleteById(1L);

    // Act
    categoryService.deleteCategory(1L);

    // Assert
    verify(categoryRepository, times(1)).existsById(1L);
    verify(categoryRepository, times(1)).deleteById(1L);
  }

  @Test
  void deleteCategory_withInvalidId_shouldThrowException() {
    // Arrange
    when(categoryRepository.existsById(99L)).thenReturn(false);

    // Act & Assert
    assertThrows(
        CategoryNotFoundException.class,
        () -> {
          categoryService.deleteCategory(99L);
        });
    verify(categoryRepository, times(1)).existsById(99L);
    verify(categoryRepository, never()).deleteById(anyLong());
  }
}
