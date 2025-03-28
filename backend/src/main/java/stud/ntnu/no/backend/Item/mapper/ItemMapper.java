package stud.ntnu.no.backend.Item.mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.Item.dto.CreateItemDTO;
import stud.ntnu.no.backend.Item.dto.ItemDTO;
import stud.ntnu.no.backend.Item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;

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
        dto.setShortDescription(item.getShortDescription());
        dto.setLongDescription(item.getLongDescription());
        dto.setPrice(item.getPrice());
        
        // Category information
        if (item.getCategory() != null) {
            dto.setCategoryId(item.getCategory().getId());
            dto.setCategoryName(item.getCategory().getName());
        }
        
        // Seller information
        if (item.getSeller() != null) {
            dto.setSellerId(item.getSeller().getId());
            dto.setSellerName(item.getSeller().getUsername());
        }
        
        // Location information
        if (item.getLocation() != null) {
            dto.setLocationId(item.getLocation().getId());
            dto.setLocationName(item.getLocation().getCity());
        }
        
        // Shipping option information
        if (item.getShippingOption() != null) {
            dto.setShippingOptionId(item.getShippingOption().getId());
            dto.setShippingOptionName(item.getShippingOption().getName());
        }
        
        // Geographic coordinates
        dto.setLatitude(item.getLatitude());
        dto.setLongitude(item.getLongitude());
        
        // Item details
        dto.setCondition(item.getCondition());
        dto.setSize(item.getSize());
        dto.setBrand(item.getBrand());
        dto.setColor(item.getColor());
        dto.setAvailable(item.isAvailable());
        dto.setVippsPaymentEnabled(item.isVippsPaymentEnabled());
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

    public Item toEntity(CreateItemDTO dto, Category category, User seller, Location location, ShippingOption shippingOption) {
        if (dto == null) {
            return null;
        }

        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setShortDescription(dto.getShortDescription());
        item.setLongDescription(dto.getLongDescription());
        item.setPrice(dto.getPrice());
        item.setCategory(category);
        item.setSeller(seller);
        item.setLocation(location);
        item.setShippingOption(shippingOption);
        item.setLatitude(dto.getLatitude());
        item.setLongitude(dto.getLongitude());
        item.setCondition(dto.getCondition());
        item.setSize(dto.getSize());
        item.setBrand(dto.getBrand());
        item.setColor(dto.getColor());
        item.setAvailable(true);
        item.setVippsPaymentEnabled(dto.isVippsPaymentEnabled());
        
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);

        return item;
    }
    
    public void updateEntityFromDto(Item item, CreateItemDTO dto, Category category, Location location, ShippingOption shippingOption) {
        if (item == null || dto == null) {
            return;
        }
        
        item.setTitle(dto.getTitle());
        item.setShortDescription(dto.getShortDescription());
        item.setLongDescription(dto.getLongDescription());
        item.setPrice(dto.getPrice());
        
        if (category != null) {
            item.setCategory(category);
        }
        
        if (location != null) {
            item.setLocation(location);
        }
        
        if (shippingOption != null) {
            item.setShippingOption(shippingOption);
        }
        
        item.setLatitude(dto.getLatitude());
        item.setLongitude(dto.getLongitude());
        item.setCondition(dto.getCondition());
        item.setSize(dto.getSize());
        item.setBrand(dto.getBrand());
        item.setColor(dto.getColor());
        item.setVippsPaymentEnabled(dto.isVippsPaymentEnabled());
        item.setUpdatedAt(LocalDateTime.now());
    }
}