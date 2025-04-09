package stud.ntnu.no.backend.user.dto;

/**
 * Data Transfer Object for requesting a password reset.
 *
 * <p>This class holds the email required to request a password reset.
 */
public class PasswordResetRequestDTO {

  private String email;

  /**
   * Returns the email for password reset request.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email for password reset request.
   *
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
}
