package stud.ntnu.no.backend.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemMarketPlaceDTOTest {

    @Test
    void testEmptyConstructor() {
        ItemMarketPlaceDTO dto = new ItemMarketPlaceDTO();
        
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertEquals(0.0, dto.getPrice());
        assertNull(dto.getCategory());
        assertNull(dto.getImage());
        assertNull(dto.getLocation());
        assertFalse(dto.isVippsPaymentEnabled());
        assertFalse(dto.isWishlisted());
    }

    @Test
    void testParameterizedConstructor() {
        Long id = 1L;
        String title = "Test Item";
        double price = 99.99;
        String category = "Test Category";
        String image = "test_image.jpg";
        String location = "Test Location";
        boolean isVippsPaymentEnabled = true;
        boolean isWishlisted = true;
        
        ItemMarketPlaceDTO dto = new ItemMarketPlaceDTO(
            id, title, price, category, 
            image, location, isVippsPaymentEnabled, 
            isWishlisted
        );
        
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(price, dto.getPrice());
        assertEquals(category, dto.getCategory());
        assertEquals(image, dto.getImage());
        assertEquals(location, dto.getLocation());
        assertEquals(isVippsPaymentEnabled, dto.isVippsPaymentEnabled());
        assertEquals(isWishlisted, dto.isWishlisted());
    }

    @Test
    void testGettersAndSetters() {
        ItemMarketPlaceDTO dto = new ItemMarketPlaceDTO();
        
        Long id = 1L;
        String title = "Test Item";
        double price = 99.99;
        String category = "Test Category";
        String image = "test_image.jpg";
        String location = "Test Location";
        boolean isVippsPaymentEnabled = true;
        boolean isWishlisted = true;
        
        dto.setId(id);
        dto.setTitle(title);
        dto.setPrice(price);
        dto.setCategory(category);
        dto.setImage(image);
        dto.setLocation(location);
        dto.setVippsPaymentEnabled(isVippsPaymentEnabled);
        dto.setWishlisted(isWishlisted);
        
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(price, dto.getPrice());
        assertEquals(category, dto.getCategory());
        assertEquals(image, dto.getImage());
        assertEquals(location, dto.getLocation());
        assertEquals(isVippsPaymentEnabled, dto.isVippsPaymentEnabled());
        assertEquals(isWishlisted, dto.isWishlisted());
    }
} 