package stud.ntnu.no.backend.message.service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.dto.UpdateMessageRequest;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.exception.MessageNotFoundException;
import stud.ntnu.no.backend.message.mapper.MessageMapper;
import stud.ntnu.no.backend.message.repository.MessageRepository;

/**
 * Implementation of the MessageService interface.
 * <p>
 * This class provides methods for managing messages, including CRUD operations.
 */
@Service
public class MessageServiceImpl implements MessageService {

  private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  private final WebSocketService webSocketService;

  /**
   * Constructs a new MessageServiceImpl with the specified dependencies.
   *
   * @param messageRepository the MessageRepository
   * @param messageMapper     the MessageMapper
   * @param webSocketService  the WebSocketService
   */
  @Autowired
  public MessageServiceImpl(MessageRepository messageRepository, MessageMapper messageMapper,
      WebSocketService webSocketService) {
    this.messageRepository = messageRepository;
    this.messageMapper = messageMapper;
    this.webSocketService = webSocketService;
  }

  @Override
  public List<MessageDTO> getAllMessages() {
    logger.info("Retrieving all messages");
    return messageRepository.findAll().stream()
        .map(messageMapper::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  public MessageDTO getMessageById(Long id) {
    logger.info("Retrieving message with ID: {}", id);
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new MessageNotFoundException(id));
    return messageMapper.toDTO(message);
  }

  @Override
  public MessageDTO createMessage(CreateMessageRequest request) {
    logger.info("Creating new message from sender: {}", request.getSenderId());
    Message message = messageMapper.toEntity(request);
    Message savedMessage = messageRepository.save(message);
    MessageDTO messageDTO = messageMapper.toDTO(savedMessage);

    // Broadcast via WebSocket
    webSocketService.notifyMessageCreated(messageDTO);

    return messageDTO;
  }

  @Override
  public MessageDTO updateMessage(Long id, UpdateMessageRequest request) {
    logger.info("Updating message with ID: {}", id);
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
    logger.info("Marking message as read with ID: {}", id);
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
    logger.info("Deleting message with ID: {}", id);
    if (!messageRepository.existsById(id)) {
      throw new MessageNotFoundException(id);
    }

    messageRepository.deleteById(id);

    // Broadcast deletion after actually deleting
    webSocketService.notifyMessageDeleted(id);
  }
}
