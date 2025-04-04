package stud.ntnu.no.backend.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.dto.UpdateMessageRequest;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.exception.MessageNotFoundException;
import stud.ntnu.no.backend.message.mapper.MessageMapper;
import stud.ntnu.no.backend.message.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final WebSocketService webSocketService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper, WebSocketService webSocketService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.webSocketService = webSocketService;
    }

    @Override
    public List<MessageDTO> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
        return messageMapper.toDTO(message);
    }

    @Override
    public MessageDTO createMessage(CreateMessageRequest request) {
        Message message = messageMapper.toEntity(request);
        Message savedMessage = messageRepository.save(message);
        MessageDTO messageDTO = messageMapper.toDTO(savedMessage);
        
        // Broadcast via WebSocket
        webSocketService.notifyMessageCreated(messageDTO);
        
        return messageDTO;
    }

    @Override
    public MessageDTO updateMessage(Long id, UpdateMessageRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
        
        messageMapper.updateEntityFromRequest(message, request);
        Message updatedMessage = messageRepository.save(message);
        MessageDTO messageDTO = messageMapper.toDTO(updatedMessage);
        
        // Broadcast via WebSocket
        webSocketService.notifyMessageUpdated(messageDTO);
        
        return messageDTO;
    }

    @Override
    public MessageDTO markAsRead(Long id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new MessageNotFoundException(id));

        // Only update if not already read
        if (!message.isRead()) {
            message.setRead(true);
            Message updatedMessage = messageRepository.save(message);
            MessageDTO messageDTO = messageMapper.toDTO(updatedMessage);

            // Notify via WebSocket
            webSocketService.notifyMessageRead(messageDTO);

            return messageDTO;
        }

        return messageMapper.toDTO(message);
    }

    @Override
    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new MessageNotFoundException(id);
        }
        
        messageRepository.deleteById(id);
        
        // Broadcast deletion after actually deleting
        webSocketService.notifyMessageDeleted(id);
    }
}