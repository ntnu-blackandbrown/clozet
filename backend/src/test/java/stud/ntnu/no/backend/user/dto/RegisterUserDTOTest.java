package stud.ntnu.no.backend.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegisterUserDTOTest {

  @Test
  void testGettersAndSetters() {
    RegisterUserDTO dto = new RegisterUserDTO();

    String username = "newuser";
    String email = "new@example.com";
    String password = "securePassword123";
    String firstName = "New";
    String lastName = "User";

    dto.setUsername(username);
    dto.setEmail(email);
    dto.setPassword(password);
    dto.setFirstName(firstName);
    dto.setLastName(lastName);

    assertEquals(username, dto.getUsername());
    assertEquals(email, dto.getEmail());
    assertEquals(password, dto.getPassword());
    assertEquals(firstName, dto.getFirstName());
    assertEquals(lastName, dto.getLastName());
  }

  @Test
  void testDefaultValues() {
    RegisterUserDTO dto = new RegisterUserDTO();

    assertNull(dto.getUsername());
    assertNull(dto.getEmail());
    assertNull(dto.getPassword());
    assertNull(dto.getFirstName());
    assertNull(dto.getLastName());
  }
}
