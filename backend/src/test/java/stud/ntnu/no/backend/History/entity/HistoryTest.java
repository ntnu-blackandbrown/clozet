package stud.ntnu.no.backend.History.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.history.entity.History;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoryTest {

    private History history;
    private User user;
    private Item item;
    private LocalDateTime viewedAt;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        item = new Item();
        item.setId(1L);
        item.setTitle("Test Item");

        viewedAt = LocalDateTime.now();

        history = new History();
        history.setId(1L);
        history.setUser(user);
        history.setItem(item);
        history.setViewedAt(viewedAt);
        history.setActive(true);
    }

    @Test
    void testHistoryConstructor() {
        History emptyHistory = new History();
        assertNotNull(emptyHistory);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, history.getId());
        assertEquals(user, history.getUser());
        assertEquals(item, history.getItem());
        assertEquals(viewedAt, history.getViewedAt());
        assertTrue(history.isActive());

        // Test setters
        LocalDateTime newViewedAt = viewedAt.plusHours(1);
        history.setId(2L);
        history.setViewedAt(newViewedAt);
        history.setActive(false);

        assertEquals(2L, history.getId());
        assertEquals(newViewedAt, history.getViewedAt());
        assertFalse(history.isActive());
    }

    @Test
    void testUserRelationship() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");

        history.setUser(newUser);
        assertEquals(newUser, history.getUser());
        assertEquals(2L, history.getUser().getId());
        assertEquals("newuser", history.getUser().getUsername());
    }

    @Test
    void testItemRelationship() {
        Item newItem = new Item();
        newItem.setId(2L);
        newItem.setTitle("New Test Item");

        history.setItem(newItem);
        assertEquals(newItem, history.getItem());
        assertEquals(2L, history.getItem().getId());
        assertEquals("New Test Item", history.getItem().getTitle());
    }

    @Test
    void testPauseAndResume() {
        assertTrue(history.isActive());
        
        // Test pause
        history.setActive(false);
        assertFalse(history.isActive());
        
        // Test resume
        history.setActive(true);
        assertTrue(history.isActive());
    }
    
    @Test
    void testUpdateViewedTime() {
        LocalDateTime originalTime = history.getViewedAt();
        LocalDateTime newTime = LocalDateTime.now().plusHours(1);
        
        history.setViewedAt(newTime);
        
        assertNotEquals(originalTime, history.getViewedAt());
        assertEquals(newTime, history.getViewedAt());
    }
}