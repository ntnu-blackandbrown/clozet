package stud.ntnu.no.backend.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StatusUserDTOTest {

  @Test
  void testGettersAndSetters() {
    StatusUserDTO dto = new StatusUserDTO();

    Long id = 1L;
    String username = "statususer";
    String email = "status@example.com";
    boolean active = true;
    String role = "USER";

    dto.setId(id);
    dto.setUsername(username);
    dto.setEmail(email);
    dto.setActive(active);
    dto.setRole(role);

    assertEquals(id, dto.getId());
    assertEquals(username, dto.getUsername());
    assertEquals(email, dto.getEmail());
    assertEquals(active, dto.isActive());
    assertEquals(role, dto.getRole());
  }

  @Test
  void testDefaultValues() {
    StatusUserDTO dto = new StatusUserDTO();

    assertNull(dto.getId());
    assertNull(dto.getUsername());
    assertNull(dto.getEmail());
    assertFalse(dto.isActive());
    assertNull(dto.getRole());
  }
}
