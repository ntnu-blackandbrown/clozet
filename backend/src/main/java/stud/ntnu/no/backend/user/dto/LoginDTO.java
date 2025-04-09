package stud.ntnu.no.backend.user.dto;

/**
 * Data Transfer Object for user login.
 *
 * <p>This class holds the username or email and password required for user login.
 */
public class LoginDTO {

  private String usernameOrEmail;
  private String password;

  // Getters and setters

  /**
   * Returns the username or email.
   *
   * @return the username or email
   */
  public String getUsernameOrEmail() {
    return usernameOrEmail;
  }

  /**
   * Sets the username or email.
   *
   * @param usernameOrEmail the username or email to set
   */
  public void setUsernameOrEmail(String usernameOrEmail) {
    this.usernameOrEmail = usernameOrEmail;
  }

  /**
   * Returns the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
