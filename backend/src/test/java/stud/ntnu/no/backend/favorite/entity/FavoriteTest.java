package stud.ntnu.no.backend.favorite.entity;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class FavoriteTest {

    @Test
    void testDefaultConstructor() {
        Favorite favorite = new Favorite();
        
        assertNull(favorite.getId());
        assertNull(favorite.getUser());
        assertNull(favorite.getItem());
        assertNotNull(favorite.getCreatedAt());
        assertFalse(favorite.isActive());
    }
    
    @Test
    void testParameterizedConstructor() {
        Long id = 1L;
        User user = new User();
        Item item = new Item();
        LocalDateTime createdAt = LocalDateTime.now();
        boolean active = true;
        
        Favorite favorite = new Favorite(id, user, item, createdAt, active);
        
        assertEquals(id, favorite.getId());
        assertEquals(user, favorite.getUser());
        assertEquals(item, favorite.getItem());
        assertEquals(createdAt, favorite.getCreatedAt());
        assertTrue(favorite.isActive());
    }
    
    @Test
    void testGetterAndSetters() {
        Favorite favorite = new Favorite();
        
        Long id = 1L;
        User user = new User();
        user.setId(2L);
        Item item = new Item();
        item.setId(3L);
        LocalDateTime createdAt = LocalDateTime.now();
        boolean active = true;
        
        favorite.setId(id);
        favorite.setUser(user);
        favorite.setItem(item);
        favorite.setCreatedAt(createdAt);
        favorite.setActive(active);
        
        assertEquals(id, favorite.getId());
        assertEquals(user, favorite.getUser());
        assertEquals(item, favorite.getItem());
        assertEquals(createdAt, favorite.getCreatedAt());
        assertTrue(favorite.isActive());
        assertEquals("2", favorite.getUserId());
        assertEquals(3L, favorite.getItemId());
    }
    
    @Test
    void testGetUserIdWithNullUser() {
        Favorite favorite = new Favorite();
        assertNull(favorite.getUserId());
    }
    
    @Test
    void testGetItemIdWithNullItem() {
        Favorite favorite = new Favorite();
        assertNull(favorite.getItemId());
    }
} 