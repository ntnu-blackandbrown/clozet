package stud.ntnu.no.backend.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a user entity.
 * <p>
 * This entity is used to store user information including credentials and profile details.
 * </p>
 */
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  private String role;
  private String firstName;
  private String lastName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private boolean isActive;

  // Felter for e-postverifisering:
  private String verificationToken;
  private LocalDateTime verificationTokenExpiry;

  private String profilePictureUrl;

  // Getters and setters
  public String getProfilePictureUrl() {
    return profilePictureUrl;
  }

  public void setProfilePictureUrl(String profilePictureUrl) {
    this.profilePictureUrl = profilePictureUrl;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  public String getVerificationToken() {
    return verificationToken;
  }

  public void setVerificationToken(String verificationToken) {
    this.verificationToken = verificationToken;
  }

  public LocalDateTime getVerificationTokenExpiry() {
    return verificationTokenExpiry;
  }

  public void setVerificationTokenExpiry(LocalDateTime verificationTokenExpiry) {
    this.verificationTokenExpiry = verificationTokenExpiry;
  }

  /**
   * Sets the full name of the user by splitting the input string into first and last name.
   *
   * @param fullName the full name of the user
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
