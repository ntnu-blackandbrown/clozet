package stud.ntnu.no.backend.Message.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.Message.DTOs.CreateMessageRequest;
import stud.ntnu.no.backend.Message.DTOs.MessageDTO;
import stud.ntnu.no.backend.Message.DTOs.UpdateMessageRequest;
import stud.ntnu.no.backend.Message.Service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@RequestBody CreateMessageRequest request) {
        return new ResponseEntity<>(messageService.createMessage(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessage(
            @PathVariable Long id,
            @RequestBody UpdateMessageRequest request) {
        return ResponseEntity.ok(messageService.updateMessage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}