package stud.ntnu.no.backend.message.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import stud.ntnu.no.backend.message.dto.MessageDTO;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebSocketServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketService webSocketService;

    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setSenderId("sender123");
        messageDTO.setReceiverId("receiver456");
        messageDTO.setContent("Test message");
    }

    @Test
    void notifyMessageCreated_shouldSendToTopicAndUserDestination() {
        // Act
        webSocketService.notifyMessageCreated(messageDTO);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend("/topic/messages", messageDTO);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/user.receiver456", messageDTO);
    }

    @Test
    void notifyMessageRead_shouldSendToReadTopic() {
        // Act
        webSocketService.notifyMessageRead(messageDTO);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend("/topic/messages.read", messageDTO);
    }

    @Test
    void notifyMessageUpdated_shouldSendToUpdateTopic() {
        // Act
        webSocketService.notifyMessageUpdated(messageDTO);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend("/topic/messages.update", messageDTO);
    }

    @Test
    void notifyMessageDeleted_shouldSendToDeleteTopic() {
        // Arrange
        Long messageId = 1L;

        // Act
        webSocketService.notifyMessageDeleted(messageId);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend("/topic/messages.delete", messageId);
    }

    @Test
    void notifyConversationArchived_shouldSendToArchiveTopic() {
        // Arrange
        String conversationId = "conv123";
        String userId = "user123";

        // Act
        webSocketService.notifyConversationArchived(conversationId, userId);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/conversations.archive"),
                argThat((Map<String, Object> map) -> 
                    map.get("conversationId").equals(conversationId) && 
                    map.get("userId").equals(userId)
                )
        );
    }

    @Test
    void notifyConversationDeleted_shouldSendToDeleteTopic() {
        // Arrange
        String conversationId = "conv123";

        // Act
        webSocketService.notifyConversationDeleted(conversationId);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend("/topic/conversations.delete", conversationId);
    }

    @Test
    void notifyMessageCreated_withException_shouldHandleGracefully() {
        // Arrange
        doThrow(new RuntimeException("Test exception")).when(messagingTemplate).convertAndSend(anyString(), any(Object.class));

        // Act & Assert - should not throw exception
        webSocketService.notifyMessageCreated(messageDTO);
        
        // Verify first call attempted
        verify(messagingTemplate, times(1)).convertAndSend("/topic/messages", messageDTO);
    }
} 