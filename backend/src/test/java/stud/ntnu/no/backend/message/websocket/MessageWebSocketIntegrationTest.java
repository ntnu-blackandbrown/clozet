package stud.ntnu.no.backend.message.websocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.service.MessageService;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MessageWebSocketIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(MessageWebSocketIntegrationTest.class);

    @LocalServerPort
    private int port;

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private WebSocketTestClient webSocketClient;

    @BeforeEach
    void setUp() throws Exception {
        // Clean database
        jdbcTemplate.execute("DELETE FROM messages");
        
        logger.info("Connecting WebSocket client to port: {}", port);
        // Connect WebSocket client
        webSocketClient = new WebSocketTestClient();
        webSocketClient.connect("ws://localhost:" + port + "/ws");
        
        // Add larger delay to ensure connection is established
        TimeUnit.SECONDS.sleep(1);
        logger.info("WebSocket client connected");
    }

    @AfterEach
    void tearDown() {
        webSocketClient.disconnect();
        logger.info("WebSocket client disconnected");
    }

    @Test
    void testSendAndReceiveMessage() throws Exception {
        logger.info("Starting testSendAndReceiveMessage");
        // Set up WebSocket subscriber first
        CompletableFuture<MessageDTO> messageFuture = new CompletableFuture<>();
        webSocketClient.subscribe("/topic/messages", MessageDTO.class, messageFuture);
        
        // Add delay to ensure subscription is established
        TimeUnit.SECONDS.sleep(1);
        logger.info("WebSocket subscription established");

        // Create a unique message for this test run
        String uniqueContent = "Message-" + System.currentTimeMillis();
        CreateMessageRequest request = new CreateMessageRequest(
                "seller1",
                "buyer1",
                uniqueContent,
                LocalDateTime.now()
        );

        // Send message via service
        logger.info("Sending message via service: {}", uniqueContent);
        MessageDTO createdMessage = messageService.createMessage(request);
        assertNotNull(createdMessage.getId());
        logger.info("Message created with ID: {}", createdMessage.getId());

        // Wait longer for the message
        MessageDTO receivedMessage = messageFuture.get(30, TimeUnit.SECONDS);
        logger.info("Message received via WebSocket: {}", receivedMessage.getId());
        assertEquals(createdMessage.getId(), receivedMessage.getId());
        assertEquals(uniqueContent, receivedMessage.getContent());
    }

    @Test
    void testDirectWebSocketCommunication() throws Exception {
        logger.info("Starting testDirectWebSocketCommunication");
        // Set up WebSocket subscriber
        CompletableFuture<MessageDTO> messageFuture = new CompletableFuture<>();
        webSocketClient.subscribe("/topic/messages", MessageDTO.class, messageFuture);
        
        // Add delay to ensure subscription is established
        TimeUnit.SECONDS.sleep(1);
        logger.info("WebSocket subscription established");

        // Create unique message for this test
        String uniqueContent = "DirectWS-" + System.currentTimeMillis();
        CreateMessageRequest request = new CreateMessageRequest(
                "buyer1",
                "seller1",
                uniqueContent,
                LocalDateTime.now()
        );

        // Send via WebSocket
        logger.info("Sending message via WebSocket: {}", uniqueContent);
        webSocketClient.send("/app/chat.sendMessage", request);
        logger.info("Message sent via WebSocket");

        // Wait longer for message
        MessageDTO receivedMessage = messageFuture.get(30, TimeUnit.SECONDS);
        logger.info("Message received via WebSocket: {}", receivedMessage.getId());
        assertNotNull(receivedMessage.getId());
        assertEquals(uniqueContent, receivedMessage.getContent());
    }
}