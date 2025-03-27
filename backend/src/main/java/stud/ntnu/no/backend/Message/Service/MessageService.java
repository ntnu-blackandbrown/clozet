package stud.ntnu.no.backend.Message.Service;

import stud.ntnu.no.backend.Message.DTOs.CreateMessageRequest;
import stud.ntnu.no.backend.Message.DTOs.MessageDTO;
import stud.ntnu.no.backend.Message.DTOs.UpdateMessageRequest;

import java.util.List;

public interface MessageService {
    List<MessageDTO> getAllMessages();
    MessageDTO getMessageById(Long id);
    MessageDTO createMessage(CreateMessageRequest request);
    MessageDTO updateMessage(Long id, UpdateMessageRequest request);
    void deleteMessage(Long id);
}