package stud.ntnu.no.backend.history.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class HistoryDTOTest {

  @Test
  void testGettersAndSetters() {
    // Setup test data
    Long id = 1L;
    Long userId = 100L;
    Long itemId = 200L;
    String itemTitle = "Test Item";
    LocalDateTime viewedAt = LocalDateTime.now();
    boolean active = true;

    // Create instance and set values
    HistoryDTO historyDTO = new HistoryDTO();
    historyDTO.setId(id);
    historyDTO.setUserId(userId);
    historyDTO.setItemId(itemId);
    historyDTO.setItemTitle(itemTitle);
    historyDTO.setViewedAt(viewedAt);
    historyDTO.setActive(active);

    // Verify values are set correctly
    assertEquals(id, historyDTO.getId());
    assertEquals(userId, historyDTO.getUserId());
    assertEquals(itemId, historyDTO.getItemId());
    assertEquals(itemTitle, historyDTO.getItemTitle());
    assertEquals(viewedAt, historyDTO.getViewedAt());
    assertEquals(active, historyDTO.isActive());
  }

  @Test
  void testDefaultValues() {
    // Create a new instance without setting any values
    HistoryDTO historyDTO = new HistoryDTO();

    // Verify default values
    assertNull(historyDTO.getId());
    assertNull(historyDTO.getUserId());
    assertNull(historyDTO.getItemId());
    assertNull(historyDTO.getItemTitle());
    assertNull(historyDTO.getViewedAt());
    assertFalse(historyDTO.isActive());
  }
}
