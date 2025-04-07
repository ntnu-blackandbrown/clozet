package stud.ntnu.no.backend.message.entity;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.item.entity.Item;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void testDefaultConstructor() {
        Message message = new Message();
        
        assertNull(message.getId());
        assertNull(message.getSenderId());
        assertNull(message.getReceiverId());
        assertNull(message.getItem());
        assertNull(message.getContent());
        assertNull(message.getCreatedAt());
        assertFalse(message.isRead());
    }
    
    @Test
    void testGettersAndSetters() {
        Message message = new Message();
        
        Long id = 1L;
        String senderId = "sender123";
        String receiverId = "receiver456";
        Item item = new Item();
        String content = "Test message content";
        LocalDateTime createdAt = LocalDateTime.now();
        boolean isRead = true;
        
        message.setId(id);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setItem(item);
        message.setContent(content);
        message.setCreatedAt(createdAt);
        message.setRead(isRead);
        
        assertEquals(id, message.getId());
        assertEquals(senderId, message.getSenderId());
        assertEquals(receiverId, message.getReceiverId());
        assertEquals(item, message.getItem());
        assertEquals(content, message.getContent());
        assertEquals(createdAt, message.getCreatedAt());
        assertTrue(message.isRead());
    }
    
    @Test
    void testItemRelationship() {
        Message message = new Message();
        Item item = new Item();
        item.setId(123L);
        item.setTitle("Test Item");
        
        message.setItem(item);
        
        assertEquals(item, message.getItem());
        assertEquals(123L, message.getItem().getId());
        assertEquals("Test Item", message.getItem().getTitle());
    }
    
    @Test
    void testReadStatus() {
        Message message = new Message();
        assertFalse(message.isRead());
        
        message.setRead(true);
        assertTrue(message.isRead());
        
        message.setRead(false);
        assertFalse(message.isRead());
    }
} 