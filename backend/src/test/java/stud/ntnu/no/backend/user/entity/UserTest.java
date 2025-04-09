package stud.ntnu.no.backend.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void testConstructorAndGetters() {
    User user = new User();
    user.setUsername("testuser");
    user.setEmail("testuser@example.com");
    user.setPasswordHash("hashedpassword");
    user.setActive(true);

    assertEquals("testuser", user.getUsername());
    assertEquals("testuser@example.com", user.getEmail());
    assertEquals("hashedpassword", user.getPasswordHash());
    assertTrue(user.isActive());
  }

  @Test
  void testSetFullName() {
    User user = new User();
    user.setFullName("John Doe");

    assertEquals("John", user.getFirstName());
    assertEquals("Doe", user.getLastName());

    user.setFullName("Alice");

    assertEquals("Alice", user.getFirstName());
    assertEquals("Doe", user.getLastName());
  }
}
