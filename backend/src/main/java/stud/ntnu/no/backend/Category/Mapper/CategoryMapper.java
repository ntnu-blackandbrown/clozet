package stud.ntnu.no.backend.Category.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import stud.ntnu.no.backend.Category.DTOs.CategoryDTO;
import stud.ntnu.no.backend.Category.Model.Category;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    @Mapping(source = "subcategories", target = "subcategoryIds", qualifiedByName = "subcategoriesToIds")
    CategoryDTO toDto(Category category);
    
    @Named("subcategoriesToIds")
    default List<Long> subcategoriesToIds(List<Category> subcategories) {
        if (subcategories == null) {
            return null;
        }
        return subcategories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());
    }
    
    @Mapping(target = "parent.id", source = "parentId")
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "items", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);
    
    List<CategoryDTO> toDtoList(List<Category> categories);
}