package stud.ntnu.no.backend.history.entity;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class HistoryTest {

    @Test
    void testDefaultConstructor() {
        History history = new History();
        
        assertNull(history.getId());
        assertNull(history.getUser());
        assertNull(history.getItem());
        assertNull(history.getViewedAt());
        assertTrue(history.isActive());
    }
    
    @Test
    void testGettersAndSetters() {
        History history = new History();
        
        Long id = 1L;
        User user = new User();
        Item item = new Item();
        LocalDateTime viewedAt = LocalDateTime.now();
        boolean active = false;
        
        history.setId(id);
        history.setUser(user);
        history.setItem(item);
        history.setViewedAt(viewedAt);
        history.setActive(active);
        
        assertEquals(id, history.getId());
        assertEquals(user, history.getUser());
        assertEquals(item, history.getItem());
        assertEquals(viewedAt, history.getViewedAt());
        assertFalse(history.isActive());
    }
    
    @Test
    void testUserRelationship() {
        History history = new History();
        User user = new User();
        user.setId(123L);
        user.setUsername("testuser");
        
        history.setUser(user);
        
        assertEquals(user, history.getUser());
        assertEquals(123L, history.getUser().getId());
        assertEquals("testuser", history.getUser().getUsername());
    }
    
    @Test
    void testItemRelationship() {
        History history = new History();
        Item item = new Item();
        item.setId(456L);
        item.setTitle("Test Item");
        
        history.setItem(item);
        
        assertEquals(item, history.getItem());
        assertEquals(456L, history.getItem().getId());
        assertEquals("Test Item", history.getItem().getTitle());
    }
    
    @Test
    void testActivateDeactivate() {
        History history = new History();
        assertTrue(history.isActive()); // Default is true
        
        history.setActive(false);
        assertFalse(history.isActive());
        
        history.setActive(true);
        assertTrue(history.isActive());
    }
} 