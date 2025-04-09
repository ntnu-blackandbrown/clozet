package stud.ntnu.no.backend.user.dto;

/**
 * Data Transfer Object for representing a user's status.
 *
 * <p>This class holds information about a user's status, including ID, username, email, active
 * status, and role.
 */
public class StatusUserDTO {

  private Long id;
  private String username;
  private String email;
  private boolean active;
  private String role;

  // Getters og setters

  /**
   * Returns the user ID.
   *
   * @return the user ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the user ID.
   *
   * @param id the user ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username.
   *
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email.
   *
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns whether the user is active.
   *
   * @return true if the user is active, false otherwise
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the active status of the user.
   *
   * @param active true if the user is active, false otherwise
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Returns the role of the user.
   *
   * @return the role
   */
  public String getRole() {
    return role;
  }

  /**
   * Sets the role of the user.
   *
   * @param role the role to set
   */
  public void setRole(String role) {
    this.role = role;
  }
}
