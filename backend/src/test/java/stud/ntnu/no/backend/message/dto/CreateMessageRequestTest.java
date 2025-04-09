package stud.ntnu.no.backend.message.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CreateMessageRequestTest {

  @Test
  void testEmptyConstructor() {
    CreateMessageRequest request = new CreateMessageRequest();

    assertNull(request.getSenderId());
    assertNull(request.getReceiverId());
    assertNull(request.getContent());
    assertNull(request.getTimestamp());
  }

  @Test
  void testParameterizedConstructor() {
    String senderId = "sender123";
    String receiverId = "receiver456";
    String content = "Hello, is this still available?";
    LocalDateTime timestamp = LocalDateTime.now();

    CreateMessageRequest request =
        new CreateMessageRequest(senderId, receiverId, content, timestamp);

    assertEquals(senderId, request.getSenderId());
    assertEquals(receiverId, request.getReceiverId());
    assertEquals(content, request.getContent());
    assertEquals(timestamp, request.getTimestamp());
  }

  @Test
  void testGettersAndSetters() {
    CreateMessageRequest request = new CreateMessageRequest();

    String senderId = "sender123";
    String receiverId = "receiver456";
    String content = "Hello, is this still available?";
    LocalDateTime timestamp = LocalDateTime.now();

    request.setSenderId(senderId);
    request.setReceiverId(receiverId);
    request.setContent(content);
    request.setTimestamp(timestamp);

    assertEquals(senderId, request.getSenderId());
    assertEquals(receiverId, request.getReceiverId());
    assertEquals(content, request.getContent());
    assertEquals(timestamp, request.getTimestamp());
  }
}
