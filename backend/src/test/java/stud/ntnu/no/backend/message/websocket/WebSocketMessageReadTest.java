package stud.ntnu.no.backend.message.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.repository.MessageRepository;
import stud.ntnu.no.backend.message.service.MessageService;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Run the test in test profile to avoid loading database initializers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebSocketMessageReadTest {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageReadTest.class);

    @LocalServerPort
    private int port;

    @Autowired
    private MessageService messageService;
    
    @MockBean
    private MessageRepository messageRepository;
    
    @MockBean 
    private UserRepository userRepository;
    
    private WebSocketTestClient webSocketClient;

    @BeforeEach
    void setup() throws Exception {
        logger.info("Setting up WebSocket test on port {}", port);
        
        // Mock message repository behavior
        Message mockMessage = new Message();
        mockMessage.setId(1L);
        mockMessage.setSenderId("user1");
        mockMessage.setReceiverId("user2");
        mockMessage.setContent("Test message");
        mockMessage.setRead(false);
        
        when(messageRepository.save(any(Message.class))).thenReturn(mockMessage);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(mockMessage));
        
        // Setup WebSocket connection using the more robust WebSocketTestClient
        webSocketClient = new WebSocketTestClient();
        try {
            webSocketClient.connect("ws://localhost:" + port + "/ws");
            logger.info("WebSocket client connected successfully");
            
            // Add a delay to ensure connection is established
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            logger.error("Failed to connect to WebSocket: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @AfterEach
    void tearDown() {
        if (webSocketClient != null) {
            webSocketClient.disconnect();
            logger.info("WebSocket client disconnected");
        }
    }
    
    @Test
    void testMarkMessageAsRead() throws Exception {
        // Context loading verification
        assertNotNull(messageService);
        assertNotNull(messageRepository);
        
        // Setup message data for updating
        Message updatedMessage = new Message();
        updatedMessage.setId(1L);
        updatedMessage.setSenderId("user1");
        updatedMessage.setReceiverId("user2");
        updatedMessage.setContent("Test message");
        updatedMessage.setRead(true);
        
        when(messageRepository.findById(1L)).thenReturn(Optional.of(updatedMessage));
        
        // Create CompletableFuture to receive the WebSocket message
        CompletableFuture<MessageDTO> messageFuture = new CompletableFuture<>();
        
        // Connect to the read status topic
        logger.info("Subscribing to /topic/messages.read");
        webSocketClient.subscribe("/topic/messages.read", MessageDTO.class, messageFuture);
        
        // Add a small delay to ensure subscription is established
        TimeUnit.MILLISECONDS.sleep(500);
        
        // Send mark as read command
        logger.info("Sending message to /app/chat.markRead with payload: 1");
        webSocketClient.send("/app/chat.markRead", "1");
        
        // Wait for response (with timeout)
        logger.info("Waiting for response...");
        try {
            MessageDTO readMessage = messageFuture.get(5, TimeUnit.SECONDS);
            logger.info("Received WebSocket response for message: {}", readMessage.getId());
            assertEquals(1L, readMessage.getId());
            assertTrue(readMessage.isRead());
        } catch (Exception e) {
            logger.error("Error receiving WebSocket message: {}", e.getMessage(), e);
            fail("Did not receive WebSocket message: " + e.getMessage());
        }
    }
}