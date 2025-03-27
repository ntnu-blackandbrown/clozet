package stud.ntnu.no.backend.ItemImage.Mapper;

import org.mapstruct.*;
import stud.ntnu.no.backend.ItemImage.DTOs.CreateItemImageDTO;
import stud.ntnu.no.backend.ItemImage.DTOs.ItemImageDTO;
import stud.ntnu.no.backend.ItemImage.Entity.ItemImage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemImageMapper {
    ItemImageDTO toDto(ItemImage itemImage);
    List<ItemImageDTO> toDtoList(List<ItemImage> itemImages);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    ItemImage toEntity(CreateItemImageDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    void updateItemImageFromDto(CreateItemImageDTO dto, @MappingTarget ItemImage itemImage);
}