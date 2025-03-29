package stud.ntnu.no.backend.message.mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.dto.UpdateMessageRequest;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Component
public class MessageMapper {
    @Autowired
    private ItemRepository itemRepository;

    public MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getCreatedAt(),
                message.isRead()
        );
    }

    public Message toEntity(CreateMessageRequest request) {
        Message message = new Message();
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getContent());
        message.setRead(false); // Default to false as there's no isRead in the DTO
        message.setCreatedAt(request.getTimestamp() != null ? 
                            request.getTimestamp() : 
                            LocalDateTime.now());

        // Item association removed since itemId is not in the DTO anymore
        
        return message;
    }

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