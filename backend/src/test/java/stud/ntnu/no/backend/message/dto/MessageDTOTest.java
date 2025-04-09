package stud.ntnu.no.backend.message.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MessageDTOTest {

  @Test
  void testEmptyConstructor() {
    MessageDTO dto = new MessageDTO();

    assertNull(dto.getId());
    assertNull(dto.getSenderId());
    assertNull(dto.getReceiverId());
    assertNull(dto.getItemId());
    assertNull(dto.getContent());
    assertNull(dto.getCreatedAt());
    assertFalse(dto.isRead());
  }

  @Test
  void testParameterizedConstructor() {
    Long id = 1L;
    String senderId = "sender123";
    String receiverId = "receiver456";
    Long itemId = 789L;
    String content = "Hello, is this still available?";
    LocalDateTime createdAt = LocalDateTime.now();
    boolean isRead = false;

    MessageDTO dto = new MessageDTO(id, senderId, receiverId, itemId, content, createdAt, isRead);

    assertEquals(id, dto.getId());
    assertEquals(senderId, dto.getSenderId());
    assertEquals(receiverId, dto.getReceiverId());
    assertEquals(itemId, dto.getItemId());
    assertEquals(content, dto.getContent());
    assertEquals(createdAt, dto.getCreatedAt());
    assertEquals(isRead, dto.isRead());
  }

  @Test
  void testGettersAndSetters() {
    MessageDTO dto = new MessageDTO();

    Long id = 1L;
    String senderId = "sender123";
    String receiverId = "receiver456";
    Long itemId = 789L;
    String content = "Hello, is this still available?";
    LocalDateTime createdAt = LocalDateTime.now();
    boolean isRead = false;

    dto.setId(id);
    dto.setSenderId(senderId);
    dto.setReceiverId(receiverId);
    dto.setItemId(itemId);
    dto.setContent(content);
    dto.setCreatedAt(createdAt);
    dto.setRead(isRead);

    assertEquals(id, dto.getId());
    assertEquals(senderId, dto.getSenderId());
    assertEquals(receiverId, dto.getReceiverId());
    assertEquals(itemId, dto.getItemId());
    assertEquals(content, dto.getContent());
    assertEquals(createdAt, dto.getCreatedAt());
    assertEquals(isRead, dto.isRead());
  }
}
