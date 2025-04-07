package stud.ntnu.no.backend.message.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.dto.UpdateMessageRequest;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.exception.MessageNotFoundException;
import stud.ntnu.no.backend.message.mapper.MessageMapper;
import stud.ntnu.no.backend.message.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Message message;
    private MessageDTO messageDTO;
    private CreateMessageRequest createRequest;
    private UpdateMessageRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        message = new Message();
        message.setId(1L);
        message.setSenderId("sender123");
        message.setReceiverId("receiver456");
        message.setContent("Test message content");
        message.setRead(false);
        message.setCreatedAt(LocalDateTime.now());

        messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setSenderId("sender123");
        messageDTO.setReceiverId("receiver456");
        messageDTO.setContent("Test message content");
        messageDTO.setRead(false);
        messageDTO.setCreatedAt(LocalDateTime.now());

        createRequest = new CreateMessageRequest();
        createRequest.setSenderId("sender123");
        createRequest.setReceiverId("receiver456");
        createRequest.setContent("New message content");

        updateRequest = new UpdateMessageRequest();
        updateRequest.setContent("Updated content");
    }

    @Test
    void getAllMessages_shouldReturnAllMessages() {
        // Arrange
        List<Message> messages = Arrays.asList(message);
        when(messageRepository.findAll()).thenReturn(messages);
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);

        // Act
        List<MessageDTO> result = messageService.getAllMessages();

        // Assert
        assertEquals(1, result.size());
        assertEquals(messageDTO, result.get(0));
        verify(messageRepository, times(1)).findAll();
        verify(messageMapper, times(1)).toDTO(message);
    }

    @Test
    void getMessageById_withValidId_shouldReturnMessage() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);

        // Act
        MessageDTO result = messageService.getMessageById(1L);

        // Assert
        assertEquals(messageDTO, result);
        verify(messageRepository, times(1)).findById(1L);
        verify(messageMapper, times(1)).toDTO(message);
    }

    @Test
    void getMessageById_withInvalidId_shouldThrowException() {
        // Arrange
        when(messageRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MessageNotFoundException.class, () -> {
            messageService.getMessageById(99L);
        });
        verify(messageRepository, times(1)).findById(99L);
    }

    @Test
    void createMessage_shouldCreateAndBroadcast() {
        // Arrange
        when(messageMapper.toEntity(createRequest)).thenReturn(message);
        when(messageRepository.save(message)).thenReturn(message);
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);
        doNothing().when(webSocketService).notifyMessageCreated(messageDTO);

        // Act
        MessageDTO result = messageService.createMessage(createRequest);

        // Assert
        assertEquals(messageDTO, result);
        verify(messageMapper, times(1)).toEntity(createRequest);
        verify(messageRepository, times(1)).save(message);
        verify(messageMapper, times(1)).toDTO(message);
        verify(webSocketService, times(1)).notifyMessageCreated(messageDTO);
    }

    @Test
    void updateMessage_withValidId_shouldUpdateAndBroadcast() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        doNothing().when(messageMapper).updateEntityFromRequest(message, updateRequest);
        when(messageRepository.save(message)).thenReturn(message);
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);
        doNothing().when(webSocketService).notifyMessageUpdated(messageDTO);

        // Act
        MessageDTO result = messageService.updateMessage(1L, updateRequest);

        // Assert
        assertEquals(messageDTO, result);
        verify(messageRepository, times(1)).findById(1L);
        verify(messageMapper, times(1)).updateEntityFromRequest(message, updateRequest);
        verify(messageRepository, times(1)).save(message);
        verify(messageMapper, times(1)).toDTO(message);
        verify(webSocketService, times(1)).notifyMessageUpdated(messageDTO);
    }

    @Test
    void updateMessage_withInvalidId_shouldThrowException() {
        // Arrange
        when(messageRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MessageNotFoundException.class, () -> {
            messageService.updateMessage(99L, updateRequest);
        });
        verify(messageRepository, times(1)).findById(99L);
    }

    @Test
    void markAsRead_whenNotRead_shouldUpdateAndBroadcast() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageRepository.save(message)).thenReturn(message);
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);
        doNothing().when(webSocketService).notifyMessageRead(messageDTO);

        // Act
        MessageDTO result = messageService.markAsRead(1L);

        // Assert
        assertEquals(messageDTO, result);
        assertTrue(message.isRead());
        verify(messageRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(message);
        verify(messageMapper, times(1)).toDTO(message);
        verify(webSocketService, times(1)).notifyMessageRead(messageDTO);
    }

    @Test
    void markAsRead_whenAlreadyRead_shouldNotUpdateOrBroadcast() {
        // Arrange
        message.setRead(true);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);

        // Act
        MessageDTO result = messageService.markAsRead(1L);

        // Assert
        assertEquals(messageDTO, result);
        verify(messageRepository, times(1)).findById(1L);
        verify(messageRepository, never()).save(message);
        verify(webSocketService, never()).notifyMessageRead(messageDTO);
    }

    @Test
    void markAsRead_withInvalidId_shouldThrowException() {
        // Arrange
        when(messageRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MessageNotFoundException.class, () -> {
            messageService.markAsRead(99L);
        });
        verify(messageRepository, times(1)).findById(99L);
    }

    @Test
    void deleteMessage_withValidId_shouldDeleteAndBroadcast() {
        // Arrange
        when(messageRepository.existsById(1L)).thenReturn(true);
        doNothing().when(messageRepository).deleteById(1L);
        doNothing().when(webSocketService).notifyMessageDeleted(1L);

        // Act
        messageService.deleteMessage(1L);

        // Assert
        verify(messageRepository, times(1)).existsById(1L);
        verify(messageRepository, times(1)).deleteById(1L);
        verify(webSocketService, times(1)).notifyMessageDeleted(1L);
    }

    @Test
    void deleteMessage_withInvalidId_shouldThrowException() {
        // Arrange
        when(messageRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(MessageNotFoundException.class, () -> {
            messageService.deleteMessage(99L);
        });
        verify(messageRepository, times(1)).existsById(99L);
        verify(messageRepository, never()).deleteById(anyLong());
        verify(webSocketService, never()).notifyMessageDeleted(anyLong());
    }
} 