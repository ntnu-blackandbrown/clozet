package stud.ntnu.no.backend.message.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UpdateMessageRequestTest {

    @Test
    void testEmptyConstructor() {
        UpdateMessageRequest request = new UpdateMessageRequest();
        
        assertNull(request.getContent());
        assertNull(request.getTimestamp());
    }
    
    @Test
    void testParameterizedConstructor() {
        String content = "Updated message content";
        LocalDateTime timestamp = LocalDateTime.now();
        
        UpdateMessageRequest request = new UpdateMessageRequest(content, timestamp);
        
        assertEquals(content, request.getContent());
        assertEquals(timestamp, request.getTimestamp());
    }
    
    @Test
    void testGettersAndSetters() {
        UpdateMessageRequest request = new UpdateMessageRequest();
        
        String content = "Updated message content";
        LocalDateTime timestamp = LocalDateTime.now();
        
        request.setContent(content);
        request.setTimestamp(timestamp);
        
        assertEquals(content, request.getContent());
        assertEquals(timestamp, request.getTimestamp());
    }
} 