package stud.ntnu.no.backend.message.websocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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

    private WebSocketTestClient webSocketClient;

    @BeforeEach
    void setUp() throws Exception {
        webSocketClient = new WebSocketTestClient();
        webSocketClient.connect("ws://localhost:" + port + "/ws");
    }

    @AfterEach
    void tearDown() {
        webSocketClient.disconnect();
    }

    @Test
    void testSendAndReceiveMessage() throws Exception {
        // Set up WebSocket subscriber
        CompletableFuture<MessageDTO> messageFuture = new CompletableFuture<>();
        webSocketClient.subscribe("/topic/messages", MessageDTO.class, messageFuture);

        // Create a message via REST API
        CreateMessageRequest request = new CreateMessageRequest(
                "seller1",
                "buyer1",
                "Hi, I'm interested in your item",
                LocalDateTime.now()
        );

        // Send message via service
        MessageDTO createdMessage = messageService.createMessage(request);
        assertNotNull(createdMessage.getId());

        // Verify message was received via WebSocket
        MessageDTO receivedMessage = messageFuture.get(5, TimeUnit.SECONDS);
        assertEquals(createdMessage.getId(), receivedMessage.getId());
        assertEquals("Hi, I'm interested in your item", receivedMessage.getContent());
        assertEquals("seller1", receivedMessage.getSenderId());
    }

    @Test
    void testDirectWebSocketCommunication() throws Exception {
        // Set up WebSocket subscriber
        CompletableFuture<MessageDTO> messageFuture = new CompletableFuture<>();
        webSocketClient.subscribe("/topic/messages", MessageDTO.class, messageFuture);

        // Create message request
        CreateMessageRequest request = new CreateMessageRequest(
                "buyer1",
                "seller1",
                "I want to buy this item",
                LocalDateTime.now()
        );

        // Send via WebSocket
        webSocketClient.send("/app/chat.sendMessage", request);

        // Verify received message
        MessageDTO receivedMessage = messageFuture.get(5, TimeUnit.SECONDS);
        assertNotNull(receivedMessage.getId());
        assertEquals("I want to buy this item", receivedMessage.getContent());
        assertEquals("buyer1", receivedMessage.getSenderId());
        assertEquals("seller1", receivedMessage.getReceiverId());
    }
}