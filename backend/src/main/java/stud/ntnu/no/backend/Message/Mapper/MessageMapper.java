package stud.ntnu.no.backend.Message.Mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.Message.DTOs.CreateMessageRequest;
import stud.ntnu.no.backend.Message.DTOs.MessageDTO;
import stud.ntnu.no.backend.Message.DTOs.UpdateMessageRequest;
import stud.ntnu.no.backend.Message.Entity.Message;
import stud.ntnu.no.backend.Item.Entity.Item;
import stud.ntnu.no.backend.Item.Repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
        message.setSenderId(request.getSender());
        message.setReceiverId(request.getReceiver());
        message.setContent(request.getContent());
        message.setRead(request.getIsRead() != null ? request.getIsRead() : false);
        message.setCreatedAt(java.time.LocalDateTime.now());
        
        // Set the item if itemId is provided
        if (request.getItemId() != null) {
            Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + request.getItemId()));
            message.setItem(item);
        }
        
        return message;
    }

    public void updateEntityFromRequest(Message message, UpdateMessageRequest request) {
        if (request.getContent() != null) {
            message.setContent(request.getContent());
        }
        if (request.getIsRead() != null) {
            message.setRead(request.getIsRead());
        }
    }
}