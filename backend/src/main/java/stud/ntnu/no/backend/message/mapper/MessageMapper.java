package stud.ntnu.no.backend.message.mapper;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.dto.UpdateMessageRequest;
import stud.ntnu.no.backend.message.entity.Message;

/**
 * Mapper for converting between Message entities and DTOs.
 * <p>
 * This class provides methods to convert between Message entities and their corresponding Data
 * Transfer Objects (DTOs).
 */
@Component
public class MessageMapper {

  @Autowired
  private ItemRepository itemRepository;

  /**
   * Converts a Message entity to a MessageDTO.
   *
   * @param message the Message entity
   * @return the MessageDTO
   */
  public MessageDTO toDTO(Message message) {
    return new MessageDTO(
        message.getId(),
        message.getSenderId(),
        message.getReceiverId(),
        message.getItem() != null ? message.getItem().getId() : null,
        message.getContent(),
        message.getCreatedAt(),
        message.isRead()
    );
  }

  /**
   * Converts a CreateMessageRequest to a Message entity.
   *
   * @param request the CreateMessageRequest
   * @return the Message entity
   */
  public Message toEntity(CreateMessageRequest request) {
    Message message = new Message();
    message.setSenderId(request.getSenderId());
    message.setReceiverId(request.getReceiverId());
    message.setContent(request.getContent());
    message.setRead(false); // Default to false as there's no isRead in the DTO
    message.setCreatedAt(request.getTimestamp() != null ?
        request.getTimestamp() :
        LocalDateTime.now());

    // Associate with an item if itemId is provided
    if (request.getItemId() != null) {
      itemRepository.findById(request.getItemId())
          .ifPresent(message::setItem);
    }

    return message;
  }

  /**
   * Updates a Message entity from an UpdateMessageRequest.
   *
   * @param message the Message entity to update
   * @param request the UpdateMessageRequest
   */
  public void updateEntityFromRequest(Message message, UpdateMessageRequest request) {
    if (request.getContent() != null) {
      message.setContent(request.getContent());
    }

    if (request.getTimestamp() != null) {
      message.setCreatedAt(request.getTimestamp());
    }

    // isRead update removed since it's not in UpdateMessageRequest
  }
}