package stud.ntnu.no.backend.message.websocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.service.MessageService;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageWebSocketIntegrationTest {

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
        
        // Connect WebSocket client
        webSocketClient = new WebSocketTestClient();
        webSocketClient.connect("ws://localhost:" + port + "/ws");
        
        // Add small delay to ensure connection is established
        TimeUnit.MILLISECONDS.sleep(500);
    }

    @AfterEach
    void tearDown() {
        webSocketClient.disconnect();
    }

    @Test
    void testSendAndReceiveMessage() throws Exception {
        // Set up WebSocket subscriber first
        CompletableFuture<MessageDTO> messageFuture = new CompletableFuture<>();
        webSocketClient.subscribe("/topic/messages", MessageDTO.class, messageFuture);
        
        // Add delay to ensure subscription is established
        TimeUnit.MILLISECONDS.sleep(500);

        // Create a unique message for this test run
        String uniqueContent = "Message-" + System.currentTimeMillis();
        CreateMessageRequest request = new CreateMessageRequest(
                "seller1",
                "buyer1",
                uniqueContent,
                LocalDateTime.now()
        );

        // Send message via service
        MessageDTO createdMessage = messageService.createMessage(request);
        assertNotNull(createdMessage.getId());

        // Wait longer for the message
        MessageDTO receivedMessage = messageFuture.get(10, TimeUnit.SECONDS);
        assertEquals(createdMessage.getId(), receivedMessage.getId());
        assertEquals(uniqueContent, receivedMessage.getContent());
    }

    @Test
    void testDirectWebSocketCommunication() throws Exception {
        // Set up WebSocket subscriber
        CompletableFuture<MessageDTO> messageFuture = new CompletableFuture<>();
        webSocketClient.subscribe("/topic/messages", MessageDTO.class, messageFuture);
        
        // Add delay to ensure subscription is established
        TimeUnit.MILLISECONDS.sleep(500);

        // Create unique message for this test
        String uniqueContent = "DirectWS-" + System.currentTimeMillis();
        CreateMessageRequest request = new CreateMessageRequest(
                "buyer1",
                "seller1",
                uniqueContent,
                LocalDateTime.now()
        );

        // Send via WebSocket
        webSocketClient.send("/app/chat.sendMessage", request);

        // Wait longer for message
        MessageDTO receivedMessage = messageFuture.get(10, TimeUnit.SECONDS);
        assertNotNull(receivedMessage.getId());
        assertEquals(uniqueContent, receivedMessage.getContent());
    }
}