package stud.ntnu.no.backend.common.security.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import stud.ntnu.no.backend.common.security.model.CustomUserDetails;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomUserDetailsServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private CustomUserDetailsService userDetailsService;

  private User testUser;
  private final String testUsername = "testuser";
  private final String nonExistingUsername = "nonexisting";

  @BeforeEach
  void setUp() {
    testUser = mock(User.class);
    when(testUser.getUsername()).thenReturn(testUsername);
    when(testUser.isActive()).thenReturn(true);
  }

  @Test
  void loadUserByUsername_WithExistingUsername_ShouldReturnUserDetails() {
    when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

    UserDetails userDetails = userDetailsService.loadUserByUsername(testUsername);

    assertNotNull(userDetails);
    assertTrue(userDetails instanceof CustomUserDetails);
    assertEquals(testUsername, userDetails.getUsername());
    verify(userRepository, times(1)).findByUsername(testUsername);
  }

  @Test
  void loadUserByUsername_WithNonExistingUsername_ShouldThrowException() {
    when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(nonExistingUsername));

    String expectedMessage = "User not found: " + nonExistingUsername;
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
    verify(userRepository, times(1)).findByUsername(nonExistingUsername);
  }
}
