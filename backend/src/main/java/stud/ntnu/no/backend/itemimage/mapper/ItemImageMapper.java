package stud.ntnu.no.backend.itemimage.mapper;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.itemimage.dto.CreateItemImageDTO;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between ItemImage entity and DTO objects.
 */
@Component
public class ItemImageMapper {
    @Autowired
    private ItemRepository itemRepository;

    /**
     * Converts an ItemImage entity to an ItemImageDTO.
     *
     * @param entity The ItemImage entity to convert
     * @return The converted ItemImageDTO
     */
    public ItemImageDTO toDTO(ItemImage entity) {
        if (entity == null) {
            return null;
        }
        
        return new ItemImageDTO(
            entity.getId(),
            entity.getImageUrl(),
            entity.isPrimary(),
            entity.getDisplayOrder()
        );
    }

    /**
     * Converts a list of ItemImage entities to a list of ItemImageDTOs.
     *
     * @param entities The list of ItemImage entities to convert
     * @return The list of converted ItemImageDTOs
     */
    public List<ItemImageDTO> toDTOList(List<ItemImage> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Converts a CreateItemImageDTO to an ItemImage entity.
     *
     * @param dto The CreateItemImageDTO to convert
     * @return The converted ItemImage entity
     */
    public ItemImage toEntity(CreateItemImageDTO dto) {
        ItemImage itemImage = new ItemImage();
        itemImage.setImageUrl(dto.getImageUrl());
        itemImage.setPrimary(dto.isPrimary());
        itemImage.setDisplayOrder(dto.getDisplayOrder());

        if (dto.getItemId() != null) {
            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + dto.getItemId()));
            itemImage.setItem(item);
        }

        return itemImage;
    }

    /**
     * Updates an existing ItemImage entity from a CreateItemImageDTO.
     *
     * @param dto The CreateItemImageDTO containing updated data
     * @param itemImage The ItemImage entity to update
     */
    public void updateItemImageFromDto(CreateItemImageDTO dto, ItemImage itemImage) {
        if (dto.getImageUrl() != null) {
            itemImage.setImageUrl(dto.getImageUrl());
        }
        if (dto.getItemId() != null && (itemImage.getItem() == null || !itemImage.getItem().getId().equals(dto.getItemId()))) {
            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + dto.getItemId()));
            itemImage.setItem(item);
        }
        itemImage.setPrimary(dto.isPrimary());
        itemImage.setDisplayOrder(dto.getDisplayOrder());
    }
}