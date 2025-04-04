package stud.ntnu.no.backend.favorite.dto;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteDTOTest {

    @Test
    void testDefaultConstructor() {
        // Act
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        
        // Assert
        assertNull(favoriteDTO.getId());
        assertNull(favoriteDTO.getUserId());
        assertNull(favoriteDTO.getItemId());
        assertNull(favoriteDTO.getCreatedAt());
        assertNull(favoriteDTO.getUpdatedAt());
        assertFalse(favoriteDTO.isActive()); // Default value is false since not initialized
    }
    
    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long id = 1L;
        String userId = "1";
        Long itemId = 2L;
        boolean active = true;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        // Act
        FavoriteDTO favoriteDTO = new FavoriteDTO(id, userId, itemId, active, createdAt, updatedAt);
        
        // Assert
        assertEquals(id, favoriteDTO.getId());
        assertEquals(userId, favoriteDTO.getUserId());
        assertEquals(itemId, favoriteDTO.getItemId());
        assertEquals(createdAt, favoriteDTO.getCreatedAt());
        assertEquals(updatedAt, favoriteDTO.getUpdatedAt());
        assertTrue(favoriteDTO.isActive());
    }
    
    @Test
    void testSettersAndGetters() {
        // Arrange
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        Long id = 1L;
        String userId = "1";
        Long itemId = 2L;
        boolean active = true;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        // Act
        favoriteDTO.setId(id);
        favoriteDTO.setUserId(userId);
        favoriteDTO.setItemId(itemId);
        favoriteDTO.setActive(active);
        favoriteDTO.setCreatedAt(createdAt);
        favoriteDTO.setUpdatedAt(updatedAt);
        
        // Assert
        assertEquals(id, favoriteDTO.getId());
        assertEquals(userId, favoriteDTO.getUserId());
        assertEquals(itemId, favoriteDTO.getItemId());
        assertEquals(createdAt, favoriteDTO.getCreatedAt());
        assertEquals(updatedAt, favoriteDTO.getUpdatedAt());
        assertTrue(favoriteDTO.isActive());
    }
    
    @Test
    void testEquals_withSameObject() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO = new FavoriteDTO(1L, "1", 2L, true, now, now);
        
        // Act & Assert
        assertEquals(favoriteDTO, favoriteDTO);
    }
    
    @Test
    void testEquals_withEquivalentObject() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO1 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        FavoriteDTO favoriteDTO2 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        
        // Act & Assert
        assertEquals(favoriteDTO1, favoriteDTO2);
        assertEquals(favoriteDTO2, favoriteDTO1);
    }
    
    @Test
    void testEquals_withDifferentObject() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO = new FavoriteDTO(1L, "1", 2L, true, now, now);
        
        // Act & Assert
        assertNotEquals(favoriteDTO, "String");
    }
    
    @Test
    void testEquals_withDifferentId() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO1 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        FavoriteDTO favoriteDTO2 = new FavoriteDTO(2L, "1", 2L, true, now, now);
        
        // Act & Assert
        assertNotEquals(favoriteDTO1, favoriteDTO2);
    }
    
    @Test
    void testEquals_withDifferentUserId() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO1 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        FavoriteDTO favoriteDTO2 = new FavoriteDTO(1L, "2", 2L, true, now, now);
        
        // Act & Assert
        assertNotEquals(favoriteDTO1, favoriteDTO2);
    }
    
    @Test
    void testEquals_withDifferentItemId() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO1 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        FavoriteDTO favoriteDTO2 = new FavoriteDTO(1L, "1", 3L, true, now, now);
        
        // Act & Assert
        assertNotEquals(favoriteDTO1, favoriteDTO2);
    }
    
    @Test
    void testEquals_withDifferentIsActive() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO1 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        FavoriteDTO favoriteDTO2 = new FavoriteDTO(1L, "1", 2L, false, now, now);
        
        // Act & Assert
        assertNotEquals(favoriteDTO1, favoriteDTO2);
    }
    
    @Test
    void testEquals_withDifferentUpdatedAt() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(1);
        FavoriteDTO favoriteDTO1 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        FavoriteDTO favoriteDTO2 = new FavoriteDTO(1L, "1", 2L, true, now, later);
        
        // Act & Assert
        assertNotEquals(favoriteDTO1, favoriteDTO2);
    }
    
    @Test
    void testHashCode() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO1 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        FavoriteDTO favoriteDTO2 = new FavoriteDTO(1L, "1", 2L, true, now, now);
        
        // Act & Assert
        assertEquals(favoriteDTO1.hashCode(), favoriteDTO2.hashCode());
    }
    
    @Test
    void testToString() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        FavoriteDTO favoriteDTO = new FavoriteDTO(1L, "1", 2L, true, now, now);
        
        // Act
        String toString = favoriteDTO.toString();
        
        // Assert
        // Just verify that the string contains the field names and values
        assertTrue(toString.contains("FavoriteDTO"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("userId='1'"));
        assertTrue(toString.contains("itemId=2"));
    }
} 