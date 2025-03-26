package stud.ntnu.no.backend.Item.Mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.Item.DTOs.ItemDTO;
import stud.ntnu.no.backend.Item.DTOs.CreateItemDTO;
import stud.ntnu.no.backend.Item.Entity.Item;
import stud.ntnu.no.backend.Category.Entity.Category;
import stud.ntnu.no.backend.User.Entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDTO toDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setCategoryId(item.getCategory() != null ? item.getCategory().getId() : null);
        dto.setCategoryName(item.getCategory() != null ? item.getCategory().getName() : null);
        dto.setSellerId(item.getSeller() != null ? item.getSeller().getId() : null);
        dto.setSellerName(item.getSeller() != null ? item.getSeller().getUsername() : null);
        dto.setImageUrl(item.getImageUrl());
        dto.setActive(item.isActive());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        
        return dto;
    }

    public List<ItemDTO> toDtoList(List<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Item toEntity(CreateItemDTO dto, Category category, User seller) {
        if (dto == null) {
            return null;
        }

        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setCategory(category);
        item.setSeller(seller);
        item.setImageUrl(dto.getImageUrl());
        item.setActive(true);
        item.setCreatedAt(LocalDateTime.now());
        
        return item;
    }
}