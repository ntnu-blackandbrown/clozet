package stud.ntnu.no.backend.message.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stud.ntnu.no.backend.Message.Controller.MessageController;
import stud.ntnu.no.backend.Message.DTOs.CreateMessageRequest;
import stud.ntnu.no.backend.Message.DTOs.MessageDTO;
import stud.ntnu.no.backend.Message.DTOs.UpdateMessageRequest;
import stud.ntnu.no.backend.Message.Exceptions.MessageNotFoundException;
import stud.ntnu.no.backend.Message.Service.MessageService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private MessageDTO messageDTO;
    private MessageDTO secondMessageDTO;
    private List<MessageDTO> messageDTOList;
    private CreateMessageRequest createMessageRequest;
    private UpdateMessageRequest updateMessageRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        messageDTO = new MessageDTO(
                1L,
                "sender1@example.com",
                "receiver1@example.com",
                "Test Message",
                LocalDateTime.now(),
                false
        );

        secondMessageDTO = new MessageDTO(
                2L,
                "sender2@example.com",
                "receiver2@example.com",
                "Another Message",
                LocalDateTime.now(),
                true
        );

        messageDTOList = Arrays.asList(messageDTO, secondMessageDTO);

        createMessageRequest = new CreateMessageRequest(
                "sender3@example.com",
                "receiver3@example.com",
                "New Message",
                false
        );

        updateMessageRequest = new UpdateMessageRequest(
                "Updated Message Content",
                true
        );
    }

    @Test
    void getAllMessages_ShouldReturnListOfMessages() {
        when(messageService.getAllMessages()).thenReturn(messageDTOList);

        ResponseEntity<List<MessageDTO>> response = messageController.getAllMessages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());
        verify(messageService, times(1)).getAllMessages();
    }

    @Test
    void getMessageById_WithValidId_ShouldReturnMessage() {
        when(messageService.getMessageById(1L)).thenReturn(messageDTO);

        ResponseEntity<MessageDTO> response = messageController.getMessageById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Message", response.getBody().getContent());
        verify(messageService, times(1)).getMessageById(1L);
    }

    @Test
    void getMessageById_WithInvalidId_ShouldThrowException() {
        when(messageService.getMessageById(99L)).thenThrow(new MessageNotFoundException(99L));

        try {
            messageController.getMessageById(99L);
        } catch (MessageNotFoundException e) {
            assertEquals("Message not found with id: 99", e.getMessage());
        }

        verify(messageService, times(1)).getMessageById(99L);
    }

    @Test
    void createMessage_WithValidData_ShouldReturnCreatedMessage() {
        when(messageService.createMessage(any(CreateMessageRequest.class))).thenReturn(messageDTO);

        ResponseEntity<MessageDTO> response = messageController.createMessage(createMessageRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(messageService, times(1)).createMessage(any(CreateMessageRequest.class));
    }

    @Test
    void updateMessage_WithValidData_ShouldReturnUpdatedMessage() {
        when(messageService.updateMessage(eq(1L), any(UpdateMessageRequest.class))).thenReturn(messageDTO);

        ResponseEntity<MessageDTO> response = messageController.updateMessage(1L, updateMessageRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(messageService, times(1)).updateMessage(eq(1L), any(UpdateMessageRequest.class));
    }

    @Test
    void deleteMessage_ShouldReturnNoContent() {
        doNothing().when(messageService).deleteMessage(1L);

        ResponseEntity<Void> response = messageController.deleteMessage(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(messageService, times(1)).deleteMessage(1L);
    }
}