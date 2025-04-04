package stud.ntnu.no.backend.message.service;

import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.dto.UpdateMessageRequest;

import java.util.List;

public interface MessageService {
    List<MessageDTO> getAllMessages();
    MessageDTO getMessageById(Long id);
    MessageDTO createMessage(CreateMessageRequest request);
    MessageDTO updateMessage(Long id, UpdateMessageRequest request);
    void deleteMessage(Long id);

    MessageDTO markAsRead(Long messageId);
}