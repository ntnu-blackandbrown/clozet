package stud.ntnu.no.backend.favorite.entity;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enkle tester for Favorite-entiteten.
 * Tester grunnleggende funksjonalitet som gettere, settere og konstruktører.
 */
class FavoriteEntityTest {

    /**
     * Tester standard konstruktør og tilhørende standardverdier.
     */
    @Test
    void testDefaultConstructor() {
        // Act
        Favorite favorite = new Favorite();
        
        // Assert
        assertNull(favorite.getId());
        assertNull(favorite.getUser());
        assertNull(favorite.getItem());
        assertNotNull(favorite.getCreatedAt());
        assertFalse(favorite.isActive());
    }
    
    /**
     * Tester parametrisert konstruktør.
     */
    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(1L);
        LocalDateTime createdAt = LocalDateTime.now();
        boolean active = true;
        
        // Act
        Favorite favorite = new Favorite(id, user, item, createdAt, active);
        
        // Assert
        assertEquals(id, favorite.getId());
        assertEquals(user, favorite.getUser());
        assertEquals(item, favorite.getItem());
        assertEquals(createdAt, favorite.getCreatedAt());
        assertTrue(favorite.isActive());
    }
    
    /**
     * Tester settere og gettere for alle felt.
     */
    @Test
    void testSettersAndGetters() {
        // Arrange
        Favorite favorite = new Favorite();
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(1L);
        LocalDateTime createdAt = LocalDateTime.now();
        boolean active = true;
        
        // Act
        favorite.setId(id);
        favorite.setUser(user);
        favorite.setItem(item);
        favorite.setCreatedAt(createdAt);
        favorite.setActive(active);
        
        // Assert
        assertEquals(id, favorite.getId());
        assertEquals(user, favorite.getUser());
        assertEquals(item, favorite.getItem());
        assertEquals(createdAt, favorite.getCreatedAt());
        assertEquals(active, favorite.isActive());
    }
    
    /**
     * Tester at getUserId() returnerer korrekt bruker-ID.
     */
    @Test
    void testGetUserId() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        
        // Act & Assert
        assertEquals("1", favorite.getUserId());
    }
    
    /**
     * Tester at getUserId() returnerer null når brukeren er null.
     */
    @Test
    void testGetUserId_whenUserIsNull() {
        // Arrange
        Favorite favorite = new Favorite();
        
        // Act & Assert
        assertNull(favorite.getUserId());
    }
    
    /**
     * Tester at getItemId() returnerer korrekt element-ID.
     */
    @Test
    void testGetItemId() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        Favorite favorite = new Favorite();
        favorite.setItem(item);
        
        // Act & Assert
        assertEquals(1L, favorite.getItemId());
    }
    
    /**
     * Tester at getItemId() returnerer null når elementet er null.
     */
    @Test
    void testGetItemId_whenItemIsNull() {
        // Arrange
        Favorite favorite = new Favorite();
        
        // Act & Assert
        assertNull(favorite.getItemId());
    }
} 