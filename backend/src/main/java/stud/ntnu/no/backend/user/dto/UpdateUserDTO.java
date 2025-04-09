package stud.ntnu.no.backend.user.dto;

/**
 * Data Transfer Object for updating a user's information.
 *
 * <p>This class holds the information required to update a user's details, including username,
 * email, first name, last name, active status, and role.
 */
public class UpdateUserDTO {

  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private boolean active;
  private String role;

  // Getters og setters

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
   * Returns the first name.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the first name.
   *
   * @param firstName the first name to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Returns the last name.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the last name.
   *
   * @param lastName the last name to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
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
