package stud.ntnu.no.backend.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.message.dto.ConversationDTO;
import stud.ntnu.no.backend.message.service.ConversationService;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    
    private final ConversationService conversationService;
    
    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }
    
    @GetMapping
    public ResponseEntity<List<ConversationDTO>> getUserConversations(@RequestParam String userId) {
        return ResponseEntity.ok(conversationService.getUserConversations(userId));
    }
    
    @PostMapping("/{conversationId}/archive")
    public ResponseEntity<Void> archiveConversation(
            @PathVariable String conversationId,
            @RequestParam String userId) {
        conversationService.archiveConversation(conversationId, userId);
        return ResponseEntity.ok().build();
    }
}