package stud.ntnu.no.backend.item.dto;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemDTOTest {

    @Test
    void testEmptyConstructor() {
        ItemDTO dto = new ItemDTO();
        
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getShortDescription());
        assertNull(dto.getLongDescription());
        assertEquals(0.0, dto.getPrice());
        assertNull(dto.getCategoryId());
        assertNull(dto.getCategoryName());
        assertNull(dto.getSellerId());
        assertNull(dto.getSellerName());
        assertNull(dto.getLocationId());
        assertNull(dto.getLocationName());
        assertNull(dto.getShippingOptionId());
        assertNull(dto.getShippingOptionName());
        assertEquals(0.0, dto.getLatitude());
        assertEquals(0.0, dto.getLongitude());
        assertNull(dto.getCondition());
        assertNull(dto.getSize());
        assertNull(dto.getBrand());
        assertNull(dto.getColor());
        assertFalse(dto.isAvailable());
        assertFalse(dto.isVippsPaymentEnabled());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getImages());
    }

    @Test
    void testParameterizedConstructor() {
        Long id = 1L;
        String title = "Test Item";
        String shortDescription = "Short description";
        String longDescription = "Long description";
        double price = 199.99;
        Long categoryId = 2L;
        String categoryName = "Test Category";
        Long sellerId = 3L;
        String sellerName = "Test Seller";
        Long locationId = 4L;
        String locationName = "Test Location";
        Long shippingOptionId = 5L;
        String shippingOptionName = "Test Shipping";
        double latitude = 63.4305;
        double longitude = 10.3951;
        String condition = "Used";
        String size = "Large";
        String brand = "Test Brand";
        String color = "Blue";
        boolean isAvailable = true;
        boolean isVippsPaymentEnabled = true;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        List<ItemImageDTO> images = new ArrayList<>();
        
        ItemDTO dto = new ItemDTO(
            id, title, shortDescription, longDescription, price,
            categoryId, categoryName, sellerId, sellerName,
            locationId, locationName, shippingOptionId, shippingOptionName,
            latitude, longitude, condition, size,
            brand, color, isAvailable, isVippsPaymentEnabled,
            createdAt, updatedAt, images
        );
        
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(shortDescription, dto.getShortDescription());
        assertEquals(longDescription, dto.getLongDescription());
        assertEquals(price, dto.getPrice());
        assertEquals(categoryId, dto.getCategoryId());
        assertEquals(categoryName, dto.getCategoryName());
        assertEquals(sellerId, dto.getSellerId());
        assertEquals(sellerName, dto.getSellerName());
        assertEquals(locationId, dto.getLocationId());
        assertEquals(locationName, dto.getLocationName());
        assertEquals(shippingOptionId, dto.getShippingOptionId());
        assertEquals(shippingOptionName, dto.getShippingOptionName());
        assertEquals(latitude, dto.getLatitude());
        assertEquals(longitude, dto.getLongitude());
        assertEquals(condition, dto.getCondition());
        assertEquals(size, dto.getSize());
        assertEquals(brand, dto.getBrand());
        assertEquals(color, dto.getColor());
        assertEquals(isAvailable, dto.isAvailable());
        assertEquals(isVippsPaymentEnabled, dto.isVippsPaymentEnabled());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
        assertEquals(images, dto.getImages());
    }

    @Test
    void testGettersAndSetters() {
        ItemDTO dto = new ItemDTO();
        
        Long id = 1L;
        String title = "Test Item";
        String shortDescription = "Short description";
        String longDescription = "Long description";
        double price = 199.99;
        Long categoryId = 2L;
        String categoryName = "Test Category";
        Long sellerId = 3L;
        String sellerName = "Test Seller";
        Long locationId = 4L;
        String locationName = "Test Location";
        Long shippingOptionId = 5L;
        String shippingOptionName = "Test Shipping";
        double latitude = 63.4305;
        double longitude = 10.3951;
        String condition = "Used";
        String size = "Large";
        String brand = "Test Brand";
        String color = "Blue";
        boolean isAvailable = true;
        boolean isVippsPaymentEnabled = true;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        List<ItemImageDTO> images = new ArrayList<>();
        
        dto.setId(id);
        dto.setTitle(title);
        dto.setShortDescription(shortDescription);
        dto.setLongDescription(longDescription);
        dto.setPrice(price);
        dto.setCategoryId(categoryId);
        dto.setCategoryName(categoryName);
        dto.setSellerId(sellerId);
        dto.setSellerName(sellerName);
        dto.setLocationId(locationId);
        dto.setLocationName(locationName);
        dto.setShippingOptionId(shippingOptionId);
        dto.setShippingOptionName(shippingOptionName);
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setCondition(condition);
        dto.setSize(size);
        dto.setBrand(brand);
        dto.setColor(color);
        dto.setAvailable(isAvailable);
        dto.setVippsPaymentEnabled(isVippsPaymentEnabled);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        dto.setImages(images);
        
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(shortDescription, dto.getShortDescription());
        assertEquals(longDescription, dto.getLongDescription());
        assertEquals(price, dto.getPrice());
        assertEquals(categoryId, dto.getCategoryId());
        assertEquals(categoryName, dto.getCategoryName());
        assertEquals(sellerId, dto.getSellerId());
        assertEquals(sellerName, dto.getSellerName());
        assertEquals(locationId, dto.getLocationId());
        assertEquals(locationName, dto.getLocationName());
        assertEquals(shippingOptionId, dto.getShippingOptionId());
        assertEquals(shippingOptionName, dto.getShippingOptionName());
        assertEquals(latitude, dto.getLatitude());
        assertEquals(longitude, dto.getLongitude());
        assertEquals(condition, dto.getCondition());
        assertEquals(size, dto.getSize());
        assertEquals(brand, dto.getBrand());
        assertEquals(color, dto.getColor());
        assertEquals(isAvailable, dto.isAvailable());
        assertEquals(isVippsPaymentEnabled, dto.isVippsPaymentEnabled());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
        assertEquals(images, dto.getImages());
    }
} 