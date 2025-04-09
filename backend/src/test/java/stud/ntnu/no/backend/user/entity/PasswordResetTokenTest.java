package stud.ntnu.no.backend.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class PasswordResetTokenTest {

  @Test
  void testConstructorAndGetters() {
    User user = new User();
    LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);
    PasswordResetToken token = new PasswordResetToken("testToken", expiryDate, user);

    assertEquals("testToken", token.getToken());
    assertEquals(expiryDate, token.getExpiryDate());
    assertEquals(user, token.getUser());
  }

  @Test
  void testIsExpired() {
    User user = new User();
    LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
    PasswordResetToken expiredToken = new PasswordResetToken("expiredToken", pastDate, user);

    assertTrue(expiredToken.isExpired());

    LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
    PasswordResetToken validToken = new PasswordResetToken("validToken", futureDate, user);

    assertFalse(validToken.isExpired());
  }
}
