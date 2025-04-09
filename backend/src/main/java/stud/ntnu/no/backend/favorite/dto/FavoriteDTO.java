package stud.ntnu.no.backend.favorite.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for favorites. Used to transfer favorite data between client and
 * server.
 */
public class FavoriteDTO {

  private Long id;
  private String userId;
  private Long itemId;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  /** Empty constructor for FavoriteDTO. */
  public FavoriteDTO() {}

  /**
   * Constructor to create a new FavoriteDTO object with all fields.
   *
   * @param id The ID of the favorite
   * @param userId The ID of the user
   * @param itemId The ID of the item that is favorited
   * @param active The status of the favorite (active/inactive)
   * @param createdAt The creation timestamp
   * @param updatedAt The last update timestamp
   */
  public FavoriteDTO(
      Long id,
      String userId,
      Long itemId,
      boolean active,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.userId = userId;
    this.itemId = itemId;
    this.active = active;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Retrieves the ID of the favorite.
   *
   * @return The ID of the favorite
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the favorite.
   *
   * @param id The ID of the favorite
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retrieves the user's ID.
   *
   * @return The user's ID
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Sets the user's ID.
   *
   * @param userId The user's ID
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Retrieves the item's ID.
   *
   * @return The item's ID
   */
  public Long getItemId() {
    return itemId;
  }

  /**
   * Sets the item's ID.
   *
   * @param itemId The item's ID
   */
  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  /**
   * Checks if the favorite is active.
   *
   * @return true if the favorite is active, otherwise false
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the active status of the favorite.
   *
   * @param active The active status of the favorite
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Retrieves the creation timestamp.
   *
   * @return The creation timestamp
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the creation timestamp.
   *
   * @param createdAt The creation timestamp
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Retrieves the last update timestamp.
   *
   * @return The last update timestamp
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Sets the last update timestamp.
   *
   * @param updatedAt The last update timestamp
   */
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FavoriteDTO that = (FavoriteDTO) o;
    return active == that.active
        && Objects.equals(id, that.id)
        && Objects.equals(userId, that.userId)
        && Objects.equals(itemId, that.itemId)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, itemId, active, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "FavoriteDTO{"
        + "id="
        + id
        + ", userId='"
        + userId
        + '\''
        + ", itemId="
        + itemId
        + ", active="
        + active
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
