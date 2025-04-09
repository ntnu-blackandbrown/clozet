package stud.ntnu.no.backend.common.security.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import stud.ntnu.no.backend.user.entity.User;

class CustomUserDetailsTest {

  private User mockUser;
  private CustomUserDetails userDetails;

  @BeforeEach
  void setUp() {
    mockUser = mock(User.class);
    when(mockUser.getUsername()).thenReturn("testuser");
    when(mockUser.getPasswordHash()).thenReturn("hashedpassword");
    when(mockUser.isActive()).thenReturn(true);
  }

  @Test
  void getAuthorities_WithAdminRole_ShouldReturnAdminAuthority() {
    when(mockUser.getRole()).thenReturn("ROLE_ADMIN");
    userDetails = new CustomUserDetails(mockUser);

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

    assertEquals(1, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
  }

  @Test
  void getAuthorities_WithNullRole_ShouldReturnUserAuthority() {
    when(mockUser.getRole()).thenReturn(null);
    userDetails = new CustomUserDetails(mockUser);

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

    assertEquals(1, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
  }

  @Test
  void getAuthorities_WithEmptyRole_ShouldReturnUserAuthority() {
    when(mockUser.getRole()).thenReturn("");
    userDetails = new CustomUserDetails(mockUser);

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

    assertEquals(1, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
  }

  @Test
  void getAuthorities_WithNonAdminRole_ShouldReturnUserAuthority() {
    when(mockUser.getRole()).thenReturn("ROLE_OTHER");
    userDetails = new CustomUserDetails(mockUser);

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

    assertEquals(1, authorities.size());
    assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
  }

  @Test
  void getPassword_ShouldReturnUserPasswordHash() {
    userDetails = new CustomUserDetails(mockUser);
    assertEquals("hashedpassword", userDetails.getPassword());
  }

  @Test
  void getUsername_ShouldReturnUserUsername() {
    userDetails = new CustomUserDetails(mockUser);
    assertEquals("testuser", userDetails.getUsername());
  }

  @Test
  void isAccountNonExpired_ShouldReturnTrue() {
    userDetails = new CustomUserDetails(mockUser);
    assertTrue(userDetails.isAccountNonExpired());
  }

  @Test
  void isAccountNonLocked_ShouldReturnTrue() {
    userDetails = new CustomUserDetails(mockUser);
    assertTrue(userDetails.isAccountNonLocked());
  }

  @Test
  void isCredentialsNonExpired_ShouldReturnTrue() {
    userDetails = new CustomUserDetails(mockUser);
    assertTrue(userDetails.isCredentialsNonExpired());
  }

  @Test
  void isEnabled_WithActiveUser_ShouldReturnTrue() {
    when(mockUser.isActive()).thenReturn(true);
    userDetails = new CustomUserDetails(mockUser);
    assertTrue(userDetails.isEnabled());
  }

  @Test
  void isEnabled_WithInactiveUser_ShouldReturnFalse() {
    when(mockUser.isActive()).thenReturn(false);
    userDetails = new CustomUserDetails(mockUser);
    assertFalse(userDetails.isEnabled());
  }

  @Test
  void getUser_ShouldReturnOriginalUser() {
    userDetails = new CustomUserDetails(mockUser);
    assertSame(mockUser, userDetails.getUser());
  }
}
