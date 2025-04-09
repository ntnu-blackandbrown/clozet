package stud.ntnu.no.backend.message.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConversationDTOTest {

  @Test
  void testEmptyConstructor() {
    ConversationDTO dto = new ConversationDTO();

    assertNull(dto.getConversationId());
    assertNull(dto.getSenderId());
    assertNull(dto.getReceiverId());
    assertNull(dto.getItemId());
    assertNull(dto.getLastMessage());
    assertNull(dto.getLastMessageTime());
    assertFalse(dto.isArchived());
    assertNull(dto.getMessages());
  }

  @Test
  void testParameterizedConstructor() {
    String conversationId = "sender1_receiver1_item1";
    String senderId = "sender1";
    String receiverId = "receiver1";
    Long itemId = 1L;
    String lastMessage = "Hello, is this still available?";
    LocalDateTime lastMessageTime = LocalDateTime.now();
    boolean archived = false;
    List<MessageDTO> messages = new ArrayList<>();

    ConversationDTO dto =
        new ConversationDTO(
            conversationId,
            senderId,
            receiverId,
            itemId,
            lastMessage,
            lastMessageTime,
            archived,
            messages);

    assertEquals(conversationId, dto.getConversationId());
    assertEquals(senderId, dto.getSenderId());
    assertEquals(receiverId, dto.getReceiverId());
    assertEquals(itemId, dto.getItemId());
    assertEquals(lastMessage, dto.getLastMessage());
    assertEquals(lastMessageTime, dto.getLastMessageTime());
    assertEquals(archived, dto.isArchived());
    assertEquals(messages, dto.getMessages());
  }

  @Test
  void testGettersAndSetters() {
    ConversationDTO dto = new ConversationDTO();

    String conversationId = "sender1_receiver1_item1";
    String senderId = "sender1";
    String receiverId = "receiver1";
    Long itemId = 1L;
    String lastMessage = "Hello, is this still available?";
    LocalDateTime lastMessageTime = LocalDateTime.now();
    boolean archived = false;
    List<MessageDTO> messages = new ArrayList<>();

    dto.setConversationId(conversationId);
    dto.setSenderId(senderId);
    dto.setReceiverId(receiverId);
    dto.setItemId(itemId);
    dto.setLastMessage(lastMessage);
    dto.setLastMessageTime(lastMessageTime);
    dto.setArchived(archived);
    dto.setMessages(messages);

    assertEquals(conversationId, dto.getConversationId());
    assertEquals(senderId, dto.getSenderId());
    assertEquals(receiverId, dto.getReceiverId());
    assertEquals(itemId, dto.getItemId());
    assertEquals(lastMessage, dto.getLastMessage());
    assertEquals(lastMessageTime, dto.getLastMessageTime());
    assertEquals(archived, dto.isArchived());
    assertEquals(messages, dto.getMessages());
  }
}
