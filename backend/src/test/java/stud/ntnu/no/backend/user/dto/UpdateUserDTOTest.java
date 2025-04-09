package stud.ntnu.no.backend.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UpdateUserDTOTest {

  @Test
  void testGettersAndSetters() {
    UpdateUserDTO dto = new UpdateUserDTO();

    String username = "updateduser";
    String email = "updated@example.com";
    String firstName = "Updated";
    String lastName = "User";
    boolean active = true;
    String role = "ADMIN";

    dto.setUsername(username);
    dto.setEmail(email);
    dto.setFirstName(firstName);
    dto.setLastName(lastName);
    dto.setActive(active);
    dto.setRole(role);

    assertEquals(username, dto.getUsername());
    assertEquals(email, dto.getEmail());
    assertEquals(firstName, dto.getFirstName());
    assertEquals(lastName, dto.getLastName());
    assertEquals(active, dto.isActive());
    assertEquals(role, dto.getRole());
  }

  @Test
  void testDefaultValues() {
    UpdateUserDTO dto = new UpdateUserDTO();

    assertNull(dto.getUsername());
    assertNull(dto.getEmail());
    assertNull(dto.getFirstName());
    assertNull(dto.getLastName());
    assertFalse(dto.isActive());
    assertNull(dto.getRole());
  }
}
