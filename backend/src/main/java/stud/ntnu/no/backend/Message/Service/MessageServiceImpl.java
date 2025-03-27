package stud.ntnu.no.backend.Message.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.Message.DTOs.CreateMessageRequest;
import stud.ntnu.no.backend.Message.DTOs.MessageDTO;
import stud.ntnu.no.backend.Message.DTOs.UpdateMessageRequest;
import stud.ntnu.no.backend.Message.Entity.Message;
import stud.ntnu.no.backend.Message.Exceptions.MessageNotFoundException;
import stud.ntnu.no.backend.Message.Mapper.MessageMapper;
import stud.ntnu.no.backend.Message.Repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
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
        return messageMapper.toDTO(savedMessage);
    }

    @Override
    public MessageDTO updateMessage(Long id, UpdateMessageRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
        
        messageMapper.updateEntityFromRequest(message, request);
        Message updatedMessage = messageRepository.save(message);
        return messageMapper.toDTO(updatedMessage);
    }

    @Override
    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new MessageNotFoundException(id);
        }
        messageRepository.deleteById(id);
    }
}