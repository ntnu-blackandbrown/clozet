package stud.ntnu.no.backend.favorite.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CreateFavoriteRequestTest {

  @Test
  void testEmptyConstructor() {
    CreateFavoriteRequest request = new CreateFavoriteRequest();
    assertNull(request.getUserId());
    assertNull(request.getItemId());
    assertFalse(request.getActive());
  }

  @Test
  void testParameterizedConstructor() {
    String userId = "user123";
    Long itemId = 456L;
    boolean active = true;

    CreateFavoriteRequest request = new CreateFavoriteRequest(userId, itemId, active);

    assertEquals(userId, request.getUserId());
    assertEquals(itemId, request.getItemId());
    assertEquals(active, request.getActive());
  }

  @Test
  void testGettersAndSetters() {
    CreateFavoriteRequest request = new CreateFavoriteRequest();

    String userId = "user123";
    Long itemId = 456L;
    boolean active = true;

    request.setUserId(userId);
    request.setItemId(itemId);
    request.setActive(active);

    assertEquals(userId, request.getUserId());
    assertEquals(itemId, request.getItemId());
    assertEquals(active, request.getActive());
  }

  @Test
  void testEqualsAndHashCode() {
    CreateFavoriteRequest request1 = new CreateFavoriteRequest("user123", 456L, true);
    CreateFavoriteRequest request2 = new CreateFavoriteRequest("user123", 456L, true);
    CreateFavoriteRequest request3 = new CreateFavoriteRequest("user456", 456L, true);
    CreateFavoriteRequest request4 = new CreateFavoriteRequest("user123", 789L, true);
    CreateFavoriteRequest request5 = new CreateFavoriteRequest("user123", 456L, false);

    // Test equals
    assertEquals(request1, request2);
    assertNotEquals(request1, request3);
    assertNotEquals(request1, request4);
    assertNotEquals(request1, request5);
    assertNotEquals(request1, null);
    assertNotEquals(request1, new Object());
    assertEquals(request1, request1); // Same object

    // Test hashCode
    assertEquals(request1.hashCode(), request2.hashCode());
    assertNotEquals(request1.hashCode(), request3.hashCode());
    assertNotEquals(request1.hashCode(), request4.hashCode());
    assertNotEquals(request1.hashCode(), request5.hashCode());
  }

  @Test
  void testToString() {
    CreateFavoriteRequest request = new CreateFavoriteRequest("user123", 456L, true);
    String toString = request.toString();

    assertTrue(toString.contains("userId='user123'"));
    assertTrue(toString.contains("itemId=456"));
    assertTrue(toString.contains("active=true"));
  }
}
