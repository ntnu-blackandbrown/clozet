package stud.ntnu.no.backend.favorite.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateFavoriteRequestTest {

    @Test
    void testDefaultConstructor() {
        // Act
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        
        // Assert
        assertNull(request.getUserId());
        assertNull(request.getItemId());
        assertFalse(request.getActive()); // Default value is false since not initialized
    }
    
    @Test
    void testParameterizedConstructor() {
        // Arrange
        String userId = "1";
        Long itemId = 2L;
        boolean active = true;
        
        // Act
        CreateFavoriteRequest request = new CreateFavoriteRequest(userId, itemId, active);
        
        // Assert
        assertEquals(userId, request.getUserId());
        assertEquals(itemId, request.getItemId());
        assertTrue(request.getActive());
    }
    
    @Test
    void testSettersAndGetters() {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        String userId = "1";
        Long itemId = 2L;
        boolean active = true;
        
        // Act
        request.setUserId(userId);
        request.setItemId(itemId);
        request.setActive(active);
        
        // Assert
        assertEquals(userId, request.getUserId());
        assertEquals(itemId, request.getItemId());
        assertTrue(request.getActive());
    }
    
    @Test
    void testEquals_withSameObject() {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest("1", 2L, true);
        
        // Act & Assert
        assertEquals(request, request);
    }
    
    @Test
    void testEquals_withEquivalentObject() {
        // Arrange
        CreateFavoriteRequest request1 = new CreateFavoriteRequest("1", 2L, true);
        CreateFavoriteRequest request2 = new CreateFavoriteRequest("1", 2L, true);
        
        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request2, request1);
    }
    
    @Test
    void testEquals_withDifferentObject() {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest("1", 2L, true);
        
        // Act & Assert
        assertNotEquals(request, "String");
    }
    
    @Test
    void testEquals_withDifferentUserId() {
        // Arrange
        CreateFavoriteRequest request1 = new CreateFavoriteRequest("1", 2L, true);
        CreateFavoriteRequest request2 = new CreateFavoriteRequest("2", 2L, true);
        
        // Act & Assert
        assertNotEquals(request1, request2);
    }
    
    @Test
    void testEquals_withDifferentItemId() {
        // Arrange
        CreateFavoriteRequest request1 = new CreateFavoriteRequest("1", 2L, true);
        CreateFavoriteRequest request2 = new CreateFavoriteRequest("1", 3L, true);
        
        // Act & Assert
        assertNotEquals(request1, request2);
    }
    
    @Test
    void testEquals_withDifferentIsActive() {
        // Arrange
        CreateFavoriteRequest request1 = new CreateFavoriteRequest("1", 2L, true);
        CreateFavoriteRequest request2 = new CreateFavoriteRequest("1", 2L, false);
        
        // Act & Assert
        assertNotEquals(request1, request2);
    }
    
    @Test
    void testHashCode() {
        // Arrange
        CreateFavoriteRequest request1 = new CreateFavoriteRequest("1", 2L, true);
        CreateFavoriteRequest request2 = new CreateFavoriteRequest("1", 2L, true);
        
        // Act & Assert
        assertEquals(request1.hashCode(), request2.hashCode());
    }
    
    @Test
    void testToString() {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest("1", 2L, true);
        
        // Act
        String toString = request.toString();
        
        // Assert
        // Just verify that the string contains the field names and values
        assertTrue(toString.contains("CreateFavoriteRequest"));
        assertTrue(toString.contains("userId='1'"));
        assertTrue(toString.contains("itemId=2"));
    }
} 