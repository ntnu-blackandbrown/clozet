package stud.ntnu.no.backend.shippingoption.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateShippingOptionDTOTest {

    @Test
    void testGettersAndSetters() {
        CreateShippingOptionDTO dto = new CreateShippingOptionDTO();
        
        String name = "Standard Shipping";
        String description = "3-5 business days";
        int estimatedDays = 4;
        double price = 49.99;
        boolean isTracked = false;
        
        dto.setName(name);
        dto.setDescription(description);
        dto.setEstimatedDays(estimatedDays);
        dto.setPrice(price);
        dto.setTracked(isTracked);
        
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
        assertEquals(estimatedDays, dto.getEstimatedDays());
        assertEquals(price, dto.getPrice(), 0.001);
        assertEquals(isTracked, dto.isTracked());
    }
    
    @Test
    void testDefaultValues() {
        CreateShippingOptionDTO dto = new CreateShippingOptionDTO();
        
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertEquals(0, dto.getEstimatedDays());
        assertEquals(0.0, dto.getPrice(), 0.001);
        assertFalse(dto.isTracked());
    }
} 