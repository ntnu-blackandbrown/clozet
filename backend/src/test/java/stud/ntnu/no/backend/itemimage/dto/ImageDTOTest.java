package stud.ntnu.no.backend.itemimage.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageDTOTest {

    @Test
    void testEmptyConstructor() {
        ImageDTO dto = new ImageDTO();
        
        assertNull(dto.getId());
        assertNull(dto.getUrl());
        assertNull(dto.getItemId());
        assertFalse(dto.isPrimary());
        assertEquals(0, dto.getDisplayOrder());
    }
    
    @Test
    void testParameterizedConstructor() {
        Long id = 1L;
        String url = "https://example.com/images/test.jpg";
        Long itemId = 2L;
        boolean isPrimary = true;
        int displayOrder = 0;
        
        ImageDTO dto = new ImageDTO(id, url, itemId, isPrimary, displayOrder);
        
        assertEquals(id, dto.getId());
        assertEquals(url, dto.getUrl());
        assertEquals(itemId, dto.getItemId());
        assertEquals(isPrimary, dto.isPrimary());
        assertEquals(displayOrder, dto.getDisplayOrder());
    }
    
    @Test
    void testGettersAndSetters() {
        ImageDTO dto = new ImageDTO();
        
        Long id = 1L;
        String url = "https://example.com/images/test.jpg";
        Long itemId = 2L;
        boolean isPrimary = true;
        int displayOrder = 0;
        
        dto.setId(id);
        dto.setUrl(url);
        dto.setItemId(itemId);
        dto.setPrimary(isPrimary);
        dto.setDisplayOrder(displayOrder);
        
        assertEquals(id, dto.getId());
        assertEquals(url, dto.getUrl());
        assertEquals(itemId, dto.getItemId());
        assertEquals(isPrimary, dto.isPrimary());
        assertEquals(displayOrder, dto.getDisplayOrder());
    }
} 