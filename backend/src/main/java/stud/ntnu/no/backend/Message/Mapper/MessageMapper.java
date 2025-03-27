package stud.ntnu.no.backend.Message.Mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.Message.DTOs.CreateMessageRequest;
import stud.ntnu.no.backend.Message.DTOs.MessageDTO;
import stud.ntnu.no.backend.Message.DTOs.UpdateMessageRequest;
import stud.ntnu.no.backend.Message.Entity.Message;

@Component
public class MessageMapper {
    public MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender(),
                message.getReceiver(),
                message.getContent(),
                message.getTimestamp(),
                message.getIsRead()
        );
    }

    public Message toEntity(CreateMessageRequest request) {
        return new Message(
                request.getSender(),
                request.getReceiver(),
                request.getContent(),
                request.getIsRead()
        );
    }

    public void updateEntityFromRequest(Message message, UpdateMessageRequest request) {
        if (request.getContent() != null) {
            message.setContent(request.getContent());
        }
        if (request.getIsRead() != null) {
            message.setIsRead(request.getIsRead());
        }
    }
}