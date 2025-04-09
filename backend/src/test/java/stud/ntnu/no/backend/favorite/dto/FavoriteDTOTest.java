package stud.ntnu.no.backend.favorite.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class FavoriteDTOTest {

  @Test
  void testEmptyConstructor() {
    FavoriteDTO dto = new FavoriteDTO();
    assertNull(dto.getId());
    assertNull(dto.getUserId());
    assertNull(dto.getItemId());
    assertFalse(dto.isActive());
    assertNull(dto.getCreatedAt());
    assertNull(dto.getUpdatedAt());
  }

  @Test
  void testParameterizedConstructor() {
    Long id = 1L;
    String userId = "user123";
    Long itemId = 456L;
    boolean active = true;
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now().plusHours(1);

    FavoriteDTO dto = new FavoriteDTO(id, userId, itemId, active, createdAt, updatedAt);

    assertEquals(id, dto.getId());
    assertEquals(userId, dto.getUserId());
    assertEquals(itemId, dto.getItemId());
    assertEquals(active, dto.isActive());
    assertEquals(createdAt, dto.getCreatedAt());
    assertEquals(updatedAt, dto.getUpdatedAt());
  }

  @Test
  void testGettersAndSetters() {
    FavoriteDTO dto = new FavoriteDTO();

    Long id = 1L;
    String userId = "user123";
    Long itemId = 456L;
    boolean active = true;
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now().plusHours(1);

    dto.setId(id);
    dto.setUserId(userId);
    dto.setItemId(itemId);
    dto.setActive(active);
    dto.setCreatedAt(createdAt);
    dto.setUpdatedAt(updatedAt);

    assertEquals(id, dto.getId());
    assertEquals(userId, dto.getUserId());
    assertEquals(itemId, dto.getItemId());
    assertEquals(active, dto.isActive());
    assertEquals(createdAt, dto.getCreatedAt());
    assertEquals(updatedAt, dto.getUpdatedAt());
  }

  @Test
  void testEqualsAndHashCode() {
    LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 1, 13, 0);

    FavoriteDTO dto1 = new FavoriteDTO(1L, "user123", 456L, true, createdAt, updatedAt);
    FavoriteDTO dto2 = new FavoriteDTO(1L, "user123", 456L, true, createdAt, updatedAt);
    FavoriteDTO dto3 = new FavoriteDTO(2L, "user123", 456L, true, createdAt, updatedAt);
    FavoriteDTO dto4 = new FavoriteDTO(1L, "user456", 456L, true, createdAt, updatedAt);
    FavoriteDTO dto5 = new FavoriteDTO(1L, "user123", 789L, true, createdAt, updatedAt);
    FavoriteDTO dto6 = new FavoriteDTO(1L, "user123", 456L, false, createdAt, updatedAt);

    // Test equals
    assertEquals(dto1, dto2);
    assertNotEquals(dto1, dto3);
    assertNotEquals(dto1, dto4);
    assertNotEquals(dto1, dto5);
    assertNotEquals(dto1, dto6);
    assertNotEquals(dto1, null);
    assertNotEquals(dto1, new Object());
    assertEquals(dto1, dto1); // Same object

    // Test hashCode
    assertEquals(dto1.hashCode(), dto2.hashCode());
    assertNotEquals(dto1.hashCode(), dto3.hashCode());
    assertNotEquals(dto1.hashCode(), dto4.hashCode());
    assertNotEquals(dto1.hashCode(), dto5.hashCode());
    assertNotEquals(dto1.hashCode(), dto6.hashCode());
  }

  @Test
  void testToString() {
    LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 1, 13, 0);

    FavoriteDTO dto = new FavoriteDTO(1L, "user123", 456L, true, createdAt, updatedAt);
    String toString = dto.toString();

    assertTrue(toString.contains("id=1"));
    assertTrue(toString.contains("userId='user123'"));
    assertTrue(toString.contains("itemId=456"));
    assertTrue(toString.contains("active=true"));
    assertTrue(toString.contains("createdAt=" + createdAt));
    assertTrue(toString.contains("updatedAt=" + updatedAt));
  }
}
