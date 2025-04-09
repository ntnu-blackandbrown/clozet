package stud.ntnu.no.backend.message.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.message.dto.ConversationDTO;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.mapper.MessageMapper;
import stud.ntnu.no.backend.message.repository.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class ConversationServiceTest {

  @Mock private MessageRepository messageRepository;

  @Mock private MessageMapper messageMapper;

  @Mock private WebSocketService webSocketService;

  @InjectMocks private ConversationService conversationService;

  private Message message1;
  private Message message2;
  private Message message3;
  private MessageDTO messageDTO1;
  private MessageDTO messageDTO2;
  private MessageDTO messageDTO3;
  private Item item;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    now = LocalDateTime.now();

    // Setup mock Item
    item = new Item();
    item.setId(1L);
    item.setTitle("Test Item");

    // Create messages from same conversation (same users, same item)
    message1 = new Message();
    message1.setId(1L);
    message1.setSenderId("user1");
    message1.setReceiverId("user2");
    message1.setItem(item);
    message1.setContent("Message 1");
    message1.setCreatedAt(now.minusHours(2));

    message2 = new Message();
    message2.setId(2L);
    message2.setSenderId("user2");
    message2.setReceiverId("user1");
    message2.setItem(item);
    message2.setContent("Message 2");
    message2.setCreatedAt(now.minusHours(1));

    // Create another message from different conversation (different users, same item)
    message3 = new Message();
    message3.setId(3L);
    message3.setSenderId("user3");
    message3.setReceiverId("user4");
    message3.setItem(item);
    message3.setContent("Message 3");
    message3.setCreatedAt(now);

    // Create matching DTOs
    messageDTO1 = new MessageDTO();
    messageDTO1.setId(1L);
    messageDTO1.setSenderId("user1");
    messageDTO1.setReceiverId("user2");
    messageDTO1.setItemId(1L);
    messageDTO1.setContent("Message 1");
    messageDTO1.setCreatedAt(now.minusHours(2));

    messageDTO2 = new MessageDTO();
    messageDTO2.setId(2L);
    messageDTO2.setSenderId("user2");
    messageDTO2.setReceiverId("user1");
    messageDTO2.setItemId(1L);
    messageDTO2.setContent("Message 2");
    messageDTO2.setCreatedAt(now.minusHours(1));

    messageDTO3 = new MessageDTO();
    messageDTO3.setId(3L);
    messageDTO3.setSenderId("user3");
    messageDTO3.setReceiverId("user4");
    messageDTO3.setItemId(1L);
    messageDTO3.setContent("Message 3");
    messageDTO3.setCreatedAt(now);
  }

  // Helper method to generate conversation ID in the same way as the service
  private String generateConversationId(String senderId, String receiverId, Long itemId) {
    String[] parties = {senderId, receiverId};
    Arrays.sort(parties);
    return parties[0] + "_" + parties[1] + "_" + (itemId != null ? itemId : "null");
  }

  @Test
  void getUserConversations_shouldGroupAndSortConversationsCorrectly() {
    // Arrange
    String userId = "user1";
    when(messageRepository.findBySenderIdOrReceiverId(userId, userId))
        .thenReturn(Arrays.asList(message1, message2));
    when(messageMapper.toDTO(message1)).thenReturn(messageDTO1);
    when(messageMapper.toDTO(message2)).thenReturn(messageDTO2);

    // Act
    List<ConversationDTO> result = conversationService.getUserConversations(userId);

    // Assert
    assertEquals(1, result.size());
    ConversationDTO conversation = result.get(0);
    assertEquals("Message 2", conversation.getLastMessage());
    assertEquals(now.minusHours(1), conversation.getLastMessageTime());
    assertEquals(2, conversation.getMessages().size());

    // Use the same logic to generate the expected conversation ID
    String expectedConversationId = generateConversationId("user1", "user2", 1L);
    assertEquals(expectedConversationId, conversation.getConversationId());

    verify(messageRepository, times(1)).findBySenderIdOrReceiverId(userId, userId);
    verify(messageMapper, times(1)).toDTO(message1);
    verify(messageMapper, times(1)).toDTO(message2);
  }

  @Test
  void getUserConversations_withMultipleConversations_shouldGroupSeparately() {
    // Arrange
    String userId = "user1";

    // Add a new message to a different conversation but with same user
    Message message4 = new Message();
    message4.setId(4L);
    message4.setSenderId("user1");
    message4.setReceiverId("user3");
    message4.setContent("Message 4");
    message4.setCreatedAt(now.minusMinutes(30));
    // Different item
    Item item2 = new Item();
    item2.setId(2L);
    message4.setItem(item2);

    MessageDTO messageDTO4 = new MessageDTO();
    messageDTO4.setId(4L);
    messageDTO4.setSenderId("user1");
    messageDTO4.setReceiverId("user3");
    messageDTO4.setItemId(2L);
    messageDTO4.setContent("Message 4");
    messageDTO4.setCreatedAt(now.minusMinutes(30));

    when(messageRepository.findBySenderIdOrReceiverId(userId, userId))
        .thenReturn(Arrays.asList(message1, message2, message4));
    when(messageMapper.toDTO(message1)).thenReturn(messageDTO1);
    when(messageMapper.toDTO(message2)).thenReturn(messageDTO2);
    when(messageMapper.toDTO(message4)).thenReturn(messageDTO4);

    // Act
    List<ConversationDTO> result = conversationService.getUserConversations(userId);

    // Assert
    assertEquals(2, result.size());

    // Generate expected conversation IDs
    String expectedConversationId1 = generateConversationId("user1", "user2", 1L);
    String expectedConversationId2 = generateConversationId("user1", "user3", 2L);

    // Create a map of conversation IDs to ConversationDTOs for easier assertions
    var conversationsById =
        result.stream().collect(Collectors.toMap(ConversationDTO::getConversationId, c -> c));

    // Assert on first conversation
    assertTrue(conversationsById.containsKey(expectedConversationId1));
    ConversationDTO conversation1 = conversationsById.get(expectedConversationId1);
    assertEquals("Message 2", conversation1.getLastMessage());
    assertEquals(2, conversation1.getMessages().size());

    // Assert on second conversation
    assertTrue(conversationsById.containsKey(expectedConversationId2));
    ConversationDTO conversation2 = conversationsById.get(expectedConversationId2);
    assertEquals("Message 4", conversation2.getLastMessage());
    assertEquals(1, conversation2.getMessages().size());

    // Verify the service interactions
    verify(messageRepository, times(1)).findBySenderIdOrReceiverId(userId, userId);
  }

  @Test
  void getUserConversations_withNoMessages_shouldReturnEmptyList() {
    // Arrange
    String userId = "user99";
    when(messageRepository.findBySenderIdOrReceiverId(userId, userId))
        .thenReturn(new ArrayList<>());

    // Act
    List<ConversationDTO> result = conversationService.getUserConversations(userId);

    // Assert
    assertTrue(result.isEmpty());
    verify(messageRepository, times(1)).findBySenderIdOrReceiverId(userId, userId);
  }

  @Test
  void archiveConversation_shouldNotifyViaWebSocket() {
    // Arrange
    String conversationId = "user1_user2_1";
    String userId = "user1";
    doNothing().when(webSocketService).notifyConversationArchived(conversationId, userId);

    // Act
    conversationService.archiveConversation(conversationId, userId);

    // Assert
    verify(webSocketService, times(1)).notifyConversationArchived(conversationId, userId);
  }
}
