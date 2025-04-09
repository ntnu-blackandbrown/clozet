package stud.ntnu.no.backend.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserDTOTest {

  @Test
  void testGettersAndSetters() {
    UserDTO dto = new UserDTO();

    Long id = 1L;
    String username = "testuser";
    String email = "test@example.com";
    String firstName = "Test";
    String lastName = "User";
    boolean active = true;
    String role = "USER";

    dto.setId(id);
    dto.setUsername(username);
    dto.setEmail(email);
    dto.setFirstName(firstName);
    dto.setLastName(lastName);
    dto.setActive(active);
    dto.setRole(role);

    assertEquals(id, dto.getId());
    assertEquals(username, dto.getUsername());
    assertEquals(email, dto.getEmail());
    assertEquals(firstName, dto.getFirstName());
    assertEquals(lastName, dto.getLastName());
    assertEquals(active, dto.isActive());
    assertEquals(role, dto.getRole());
  }

  @Test
  void testDefaultValues() {
    UserDTO dto = new UserDTO();

    assertNull(dto.getId());
    assertNull(dto.getUsername());
    assertNull(dto.getEmail());
    assertNull(dto.getFirstName());
    assertNull(dto.getLastName());
    assertFalse(dto.isActive());
    assertNull(dto.getRole());
  }
}
