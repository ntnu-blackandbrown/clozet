package stud.ntnu.no.backend.item.dto;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDisplayDTOTest {

    @Test
    void testEmptyConstructor() {
        ProductDisplayDTO dto = new ProductDisplayDTO();
        
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getFullDescription());
        assertEquals(0.0, dto.getPrice());
        assertNull(dto.getCategoryId());
        assertNull(dto.getCategoryName());
        assertNull(dto.getLocationName());
        assertNull(dto.getSellerFullName());
        assertNull(dto.getShippingOptionName());
        assertFalse(dto.isAvailable());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getImages());
    }

    @Test
    void testParameterizedConstructor() {
        Long id = 1L;
        String title = "Test Product";
        String fullDescription = "Full description";
        double price = 99.99;
        Long categoryId = 2L;
        String categoryName = "Test Category";
        String locationName = "Test Location";
        String sellerFullName = "Test Seller";
        String shippingOptionName = "Test Shipping";
        boolean isAvailable = true;
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        List<ItemImageDTO> images = new ArrayList<>();
        
        ProductDisplayDTO dto = new ProductDisplayDTO(
            id, title, fullDescription, price,
            categoryId, categoryName, locationName,
            sellerFullName, shippingOptionName, isAvailable,
            updatedAt, createdAt, images
        );
        
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(fullDescription, dto.getFullDescription());
        assertEquals(price, dto.getPrice());
        assertEquals(categoryId, dto.getCategoryId());
        assertEquals(categoryName, dto.getCategoryName());
        assertEquals(locationName, dto.getLocationName());
        assertEquals(sellerFullName, dto.getSellerFullName());
        assertEquals(shippingOptionName, dto.getShippingOptionName());
        assertEquals(isAvailable, dto.isAvailable());
        assertEquals(updatedAt, dto.getUpdatedAt());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(images, dto.getImages());
    }

    @Test
    void testGettersAndSetters() {
        ProductDisplayDTO dto = new ProductDisplayDTO();
        
        Long id = 1L;
        String title = "Test Product";
        String fullDescription = "Full description";
        double price = 99.99;
        Long categoryId = 2L;
        String categoryName = "Test Category";
        String locationName = "Test Location";
        String sellerFullName = "Test Seller";
        String shippingOptionName = "Test Shipping";
        boolean isAvailable = true;
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        List<ItemImageDTO> images = new ArrayList<>();
        
        dto.setId(id);
        dto.setTitle(title);
        dto.setFullDescription(fullDescription);
        dto.setPrice(price);
        dto.setCategoryId(categoryId);
        dto.setCategoryName(categoryName);
        dto.setLocationName(locationName);
        dto.setSellerFullName(sellerFullName);
        dto.setShippingOptionName(shippingOptionName);
        dto.setAvailable(isAvailable);
        dto.setUpdatedAt(updatedAt);
        dto.setCreatedAt(createdAt);
        dto.setImages(images);
        
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(fullDescription, dto.getFullDescription());
        assertEquals(price, dto.getPrice());
        assertEquals(categoryId, dto.getCategoryId());
        assertEquals(categoryName, dto.getCategoryName());
        assertEquals(locationName, dto.getLocationName());
        assertEquals(sellerFullName, dto.getSellerFullName());
        assertEquals(shippingOptionName, dto.getShippingOptionName());
        assertEquals(isAvailable, dto.isAvailable());
        assertEquals(updatedAt, dto.getUpdatedAt());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(images, dto.getImages());
    }
} 