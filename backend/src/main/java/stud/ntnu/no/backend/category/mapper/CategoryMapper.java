package stud.ntnu.no.backend.category.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import stud.ntnu.no.backend.category.dto.CategoryDTO;
import stud.ntnu.no.backend.category.entity.Category;

/**
 * MapStruct mapper interface for conversion between Category entities and DTOs. Provides automatic
 * mapping with custom transformations for complex fields.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

  /**
   * Converts a Category entity to a CategoryDTO. Maps parent category ID and name, and converts
   * subcategories to a list of IDs.
   *
   * @param category The Category entity to convert
   * @return The resulting CategoryDTO
   */
  @Mapping(source = "parent.id", target = "parentId")
  @Mapping(source = "parent.name", target = "parentName")
  @Mapping(
      source = "subcategories",
      target = "subcategoryIds",
      qualifiedByName = "subcategoriesToIds")
  CategoryDTO toDto(Category category);

  /**
   * Custom mapping method to convert a list of subcategories to a list of their IDs.
   *
   * @param subcategories List of Category entities
   * @return List of category IDs
   */
  @Named("subcategoriesToIds")
  default List<Long> subcategoriesToIds(List<Category> subcategories) {
    if (subcategories == null) {
      return null;
    }
    return subcategories.stream().map(Category::getId).collect(Collectors.toList());
  }

  /**
   * Converts a CategoryDTO to a Category entity. Ignores fields that should be managed separately
   * (subcategories, items).
   *
   * @param categoryDTO The CategoryDTO to convert
   * @return The resulting Category entity
   */
  @Mapping(target = "parent.id", source = "parentId")
  @Mapping(target = "subcategories", ignore = true)
  @Mapping(target = "items", ignore = true)
  Category toEntity(CategoryDTO categoryDTO);

  /**
   * Converts a list of Category entities to a list of CategoryDTOs.
   *
   * @param categories List of Category entities
   * @return List of CategoryDTOs
   */
  List<CategoryDTO> toDtoList(List<Category> categories);
}
