package stud.ntnu.no.backend.message.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.repository.MessageRepository;
import stud.ntnu.no.backend.message.service.MessageService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WebSocketMessageReadTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private MessageRepository messageRepository;
    
    private TestStompFrameHandler stompFrameHandler;
    private BlockingQueue<String> blockingQueue;
    private StompSession stompSession;

    @BeforeEach
    void setup() {
        blockingQueue = new LinkedBlockingDeque<>();
        stompFrameHandler = new TestStompFrameHandler(blockingQueue);
        
        // Setup WebSocket connection (requires WebSocketStompClient setup)
    }
    
    @Test
    void testMarkMessageAsRead() throws Exception {
        // Create a test message
        CreateMessageRequest request = new CreateMessageRequest();
        request.setSenderId("user1");
        request.setReceiverId("user2");
        request.setContent("Test message");
        MessageDTO createdMessage = messageService.createMessage(request);
        
        // Verify it's not read
        assertFalse(createdMessage.isRead());
        
        // Connect to the read status topic
        stompSession.subscribe("/topic/messages.read", stompFrameHandler);
        
        // Send mark as read command
        stompSession.send("/app/chat.markRead", createdMessage.getId().toString().getBytes());
        
        // Wait for response (with timeout)
        String response = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);
        
        // Parse and verify response
        ObjectMapper mapper = new ObjectMapper();
        MessageDTO readMessage = mapper.readValue(response, MessageDTO.class);
        assertEquals(createdMessage.getId(), readMessage.getId());
        assertTrue(readMessage.isRead());
        
        // Verify database state
        Message savedMessage = messageRepository.findById(createdMessage.getId()).orElseThrow();
        assertTrue(savedMessage.isRead());
    }
}