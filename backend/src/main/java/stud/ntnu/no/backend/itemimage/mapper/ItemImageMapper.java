package stud.ntnu.no.backend.itemimage.mapper;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import stud.ntnu.no.backend.Item.entity.Item;
import stud.ntnu.no.backend.itemimage.dto.CreateItemImageDTO;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.Item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemImageMapper {
    @Autowired
    private ItemRepository itemRepository;

    // Changed method name from toDTO to toDto for consistency
    public ItemImageDTO toDto(ItemImage itemImage) {
        return new ItemImageDTO(
                itemImage.getId(),
                itemImage.getItem() != null ? itemImage.getItem().getId() : null,
                itemImage.getImageUrl(),
                itemImage.isPrimary(),
                itemImage.getDisplayOrder()
        );
    }

    // Added toDtoList method
    public List<ItemImageDTO> toDtoList(List<ItemImage> itemImages) {
        return itemImages.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Updated to use CreateItemImageDTO instead of CreateItemImageRequest
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

    // Added updateItemImageFromDto method
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