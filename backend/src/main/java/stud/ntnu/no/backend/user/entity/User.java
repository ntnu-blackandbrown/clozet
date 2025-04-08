package stud.ntnu.no.backend.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a user entity in the system.
 * <p>
 * This entity is mapped to the "users" table in the database and stores comprehensive user information
 * including authentication credentials, personal profile details, account status, and verification data.
 * </p>
 * <p>
 * Users can have various roles that determine their access rights within the application.
 * Each user has a unique username and email address which are used for identification and authentication.
 * </p>
 */
@Entity
@Table(name = "users")
public class User {
  /**
   * The unique identifier for the user.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The unique username chosen by the user.
   * Used for authentication and identification.
   */
  @Column(unique = true, nullable = false)
  private String username;

  /**
   * The unique email address of the user.
   * Used for communication and can be used for authentication.
   */
  @Column(unique = true, nullable = false)
  private String email;

  /**
   * The hashed password of the user.
   * Raw passwords should never be stored in the database.
   */
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  /**
   * The role of the user within the system.
   * Example values might include "ROLE_USER", "ROLE_ADMIN", etc.
   */
  private String role;
  
  /**
   * The first name of the user.
   */
  private String firstName;
  
  /**
   * The last name of the user.
   */
  private String lastName;
  
  /**
   * The timestamp when the user account was created.
   */
  private LocalDateTime createdAt;
  
  /**
   * The timestamp when the user account was last updated.
   */
  private LocalDateTime updatedAt;

  /**
   * Flag indicating whether the user account is active.
   * Inactive accounts cannot log in to the system.
   */
  @Column(nullable = false)
  private boolean isActive;

  /**
   * Token used for email verification when registering.
   */
  private String verificationToken;
  
  /**
   * The expiration time of the verification token.
   */
  private LocalDateTime verificationTokenExpiry;

  /**
   * URL to the user's profile picture.
   */
  private String profilePictureUrl;

  // Getters and setters
  /**
   * Returns the URL to the user's profile picture.
   *
   * @return the profile picture URL
   */
  public String getProfilePictureUrl() {
    return profilePictureUrl;
  }

  /**
   * Sets the URL to the user's profile picture.
   *
   * @param profilePictureUrl the profile picture URL to set
   */
  public void setProfilePictureUrl(String profilePictureUrl) {
    this.profilePictureUrl = profilePictureUrl;
  }

  /**
   * Returns the unique identifier of the user.
   *
   * @return the user ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique identifier of the user.
   *
   * @param id the user ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the username of the user.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user.
   *
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the email address of the user.
   *
   * @return the email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address of the user.
   *
   * @param email the email address to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns the hashed password of the user.
   *
   * @return the password hash
   */
  public String getPasswordHash() {
    return passwordHash;
  }

  /**
   * Sets the hashed password of the user.
   *
   * @param passwordHash the password hash to set
   */
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  /**
   * Returns the role of the user.
   *
   * @return the user role
   */
  public String getRole() {
    return role;
  }

  /**
   * Sets the role of the user.
   *
   * @param role the user role to set
   */
  public void setRole(String role) {
    this.role = role;
  }

  /**
   * Returns the first name of the user.
   *
   * @return the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the first name of the user.
   *
   * @param firstName the first name to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Returns the last name of the user.
   *
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the last name of the user.
   *
   * @param lastName the last name to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Returns the timestamp when the user account was created.
   *
   * @return the creation timestamp
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the timestamp when the user account was created.
   *
   * @param createdAt the creation timestamp to set
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Returns the timestamp when the user account was last updated.
   *
   * @return the last update timestamp
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Sets the timestamp when the user account was last updated.
   *
   * @param updatedAt the last update timestamp to set
   */
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  /**
   * Checks if the user account is active.
   *
   * @return true if the account is active, false otherwise
   */
  public boolean isActive() {
    return isActive;
  }

  /**
   * Sets the active status of the user account.
   *
   * @param active the active status to set
   */
  public void setActive(boolean active) {
    isActive = active;
  }

  /**
   * Returns the verification token of the user.
   *
   * @return the verification token
   */
  public String getVerificationToken() {
    return verificationToken;
  }

  /**
   * Sets the verification token of the user.
   *
   * @param verificationToken the verification token to set
   */
  public void setVerificationToken(String verificationToken) {
    this.verificationToken = verificationToken;
  }

  /**
   * Returns the expiration time of the verification token.
   *
   * @return the verification token expiry timestamp
   */
  public LocalDateTime getVerificationTokenExpiry() {
    return verificationTokenExpiry;
  }

  /**
   * Sets the expiration time of the verification token.
   *
   * @param verificationTokenExpiry the verification token expiry timestamp to set
   */
  public void setVerificationTokenExpiry(LocalDateTime verificationTokenExpiry) {
    this.verificationTokenExpiry = verificationTokenExpiry;
  }

  /**
   * Sets the full name of the user by splitting the input string into first and last name.
   * If the full name contains a space, the text before the first space is set as the first name,
   * and everything after it is set as the last name.
   *
   * @param fullName the full name of the user to be split into first and last name
   */
  public void setFullName(String fullName) {
    if (fullName != null) {
      String[] parts = fullName.split(" ", 2);
      if (parts.length > 0) {
        this.firstName = parts[0];
      }
      if (parts.length > 1) {
        this.lastName = parts[1];
      }
    }
  }
}
