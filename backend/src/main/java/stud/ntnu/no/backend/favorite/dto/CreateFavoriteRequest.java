package stud.ntnu.no.backend.favorite.dto;

import java.util.Objects;

/**
 * DTO for creating or updating a favorite. Contains necessary information to create or modify a
 * favorite.
 */
public class CreateFavoriteRequest {

  private String userId;
  private Long itemId;
  private boolean active;

  /**
   * Empty constructor for CreateFavoriteRequest.
   */
  public CreateFavoriteRequest() {
  }

  /**
   * Constructor to create a new CreateFavoriteRequest object.
   *
   * @param userId The ID of the user
   * @param itemId The ID of the item to be favorited
   * @param active Indicates whether the favorite is active or not
   */
  public CreateFavoriteRequest(String userId, Long itemId, boolean active) {
    this.userId = userId;
    this.itemId = itemId;
    this.active = active;
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
  public boolean getActive() {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateFavoriteRequest that = (CreateFavoriteRequest) o;
    return active == that.active &&
        Objects.equals(userId, that.userId) &&
        Objects.equals(itemId, that.itemId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, itemId, active);
  }

  @Override
  public String toString() {
    return "CreateFavoriteRequest{" +
        "userId='" + userId + '\'' +
        ", itemId=" + itemId +
        ", active=" + active +
        '}';
  }
}
