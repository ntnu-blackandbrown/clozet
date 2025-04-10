package stud.ntnu.no.backend.user.dto;

/**
 * Data Transfer Object for resetting a user's password.
 * <p>
 * This class holds the token and new password required to reset a user's password.
 */
public class PasswordResetDTO {

  private String token;
  private String password;

  /**
   * Returns the token for password reset.
   *
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets the token for password reset.
   *
   * @param token the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * Returns the new password.
   *
   * @return the new password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the new password.
   *
   * @param password the new password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
}