package stud.ntnu.no.backend.message.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import stud.ntnu.no.backend.message.dto.ConversationDTO;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConversationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testConversationAggregation() {
        // Create a conversation between seller and buyer about item1
        String seller = "seller1";
        String buyer = "buyer1";
        Long itemId = 1L;

        // First message from buyer
        messageService.createMessage(new CreateMessageRequest(
                buyer, seller, "Hi, is this item still available?", LocalDateTime.now().minusMinutes(30)
        ));

        // Reply from seller
        messageService.createMessage(new CreateMessageRequest(
                seller, buyer, "Yes, it is still for sale", LocalDateTime.now().minusMinutes(25)
        ));

        // Another reply from buyer
        messageService.createMessage(new CreateMessageRequest(
                buyer, seller, "Great! I would like to purchase it", LocalDateTime.now().minusMinutes(20)
        ));

        // Create another conversation with a different buyer
        messageService.createMessage(new CreateMessageRequest(
                "buyer2", seller, "Hello, can you deliver this item?", LocalDateTime.now().minusMinutes(10)
        ));

        // Retrieve seller's conversations
        ResponseEntity<List<ConversationDTO>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/conversations?userId=" + seller,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ConversationDTO>>() {}
        );

        List<ConversationDTO> conversations = response.getBody();
        assertNotNull(conversations);
        assertEquals(2, conversations.size());

        // Find the conversation with buyer1
        ConversationDTO buyer1Conversation = conversations.stream()
                .filter(c -> c.getReceiverId().equals(buyer) || c.getSenderId().equals(buyer))
                .findFirst()
                .orElse(null);

        assertNotNull(buyer1Conversation);
        assertEquals(3, buyer1Conversation.getMessages().size());
        assertEquals("Great! I would like to purchase it", buyer1Conversation.getLastMessage());

        // Test archiving conversation
        ResponseEntity<Void> archiveResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/conversations/" + buyer1Conversation.getConversationId() + "/archive?userId=" + seller,
                null,
                Void.class
        );

        assertEquals(200, archiveResponse.getStatusCodeValue());
    }

    @Test
    void testWebSocketNotificationForConversation() throws Exception {
        // Set up WebSocket client
        WebSocketTestClient webSocketClient = new WebSocketTestClient();
        webSocketClient.connect("ws://localhost:" + port + "/ws");

        // Subscribe to conversation archive notifications
        CompletableFuture<Object> archiveFuture = new CompletableFuture<>();
        webSocketClient.subscribe("/topic/conversations.archive", Object.class, archiveFuture);

        try {
            // Create test messages
            String sender = "seller2";
            String receiver = "buyer2";
            
            // Create a message
            MessageDTO message = messageService.createMessage(new CreateMessageRequest(
                    sender, receiver, "Hello there", LocalDateTime.now()
            ));

            // Get the conversation
            ResponseEntity<List<ConversationDTO>> response = restTemplate.exchange(
                    "http://localhost:" + port + "/api/conversations?userId=" + sender,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ConversationDTO>>() {}
            );

            List<ConversationDTO> conversations = response.getBody();
            assertNotNull(conversations);
            assertTrue(conversations.size() > 0);
            
            // Archive the conversation
            String conversationId = conversations.get(0).getConversationId();
            restTemplate.postForEntity(
                    "http://localhost:" + port + "/api/conversations/" + conversationId + "/archive?userId=" + sender,
                    null,
                    Void.class
            );

            // Verify WebSocket notification was sent
            Object notification = archiveFuture.get(5, TimeUnit.SECONDS);
            assertNotNull(notification);
        } finally {
            webSocketClient.disconnect();
        }
    }
}