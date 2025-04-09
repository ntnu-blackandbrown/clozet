package stud.ntnu.no.backend.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordResetDTOTest {

  @Test
  void testGettersAndSetters() {
    PasswordResetDTO dto = new PasswordResetDTO();

    String token = "reset-token-123";
    String password = "newSecurePassword456";

    dto.setToken(token);
    dto.setPassword(password);

    assertEquals(token, dto.getToken());
    assertEquals(password, dto.getPassword());
  }

  @Test
  void testDefaultValues() {
    PasswordResetDTO dto = new PasswordResetDTO();

    assertNull(dto.getToken());
    assertNull(dto.getPassword());
  }
}
