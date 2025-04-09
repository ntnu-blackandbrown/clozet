package stud.ntnu.no.backend.common.security.model;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import stud.ntnu.no.backend.user.entity.User;

/**
 * Custom implementation of UserDetails for Spring Security.
 *
 * <p>This class wraps a User entity and provides user details for authentication.
 *
 * <p>.0
 */
public class CustomUserDetails implements UserDetails {

  private final User user;

  public CustomUserDetails(User user) {
    this.user = user;
  }

  /**
   * Returns the authorities granted to the user.
   *
   * @return the authorities
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    String role = user.getRole();
    if (role == null || role.trim().isEmpty() || !role.equals("ROLE_ADMIN")) {
      role = "ROLE_USER";
    }
    return Collections.singletonList(new SimpleGrantedAuthority(role));
  }

  /**
   * Returns the password used to authenticate the user.
   *
   * @return the password
   */
  @Override
  public String getPassword() {
    return user.getPasswordHash();
  }

  /**
   * Returns the username used to authenticate the user.
   *
   * @return the username
   */
  @Override
  public String getUsername() {
    return user.getUsername();
  }

  /**
   * Indicates whether the user's account has expired.
   *
   * @return true if the account is non-expired, false otherwise
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is locked or unlocked.
   *
   * @return true if the account is non-locked, false otherwise
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Indicates whether the user's credentials have expired.
   *
   * @return true if the credentials are non-expired, false otherwise
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Indicates whether the user is enabled or disabled.
   *
   * @return true if the user is enabled, false otherwise
   */
  @Override
  public boolean isEnabled() {
    return user.isActive();
  }

  /**
   * Returns the underlying User entity.
   *
   * @return the user
   */
  public User getUser() {
    return user;
  }
}
