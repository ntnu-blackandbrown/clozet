package stud.ntnu.no.backend.history.dto;

import java.time.LocalDateTime;

/** Data Transfer Object for History. */
public class HistoryDTO {

  private Long id;
  private Long userId;
  private Long itemId;
  private String itemTitle;
  private LocalDateTime viewedAt;
  private boolean active;

  // Getters and setters

  /** Gets the ID of the history record. */
  public Long getId() {
    return id;
  }

  /** Sets the ID of the history record. */
  public void setId(Long id) {
    this.id = id;
  }

  /** Gets the user ID associated with the history record. */
  public Long getUserId() {
    return userId;
  }

  /** Sets the user ID associated with the history record. */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  /** Gets the item ID associated with the history record. */
  public Long getItemId() {
    return itemId;
  }

  /** Sets the item ID associated with the history record. */
  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  /** Gets the title of the item associated with the history record. */
  public String getItemTitle() {
    return itemTitle;
  }

  /** Sets the title of the item associated with the history record. */
  public void setItemTitle(String itemTitle) {
    this.itemTitle = itemTitle;
  }

  /** Gets the timestamp when the item was viewed. */
  public LocalDateTime getViewedAt() {
    return viewedAt;
  }

  /** Sets the timestamp when the item was viewed. */
  public void setViewedAt(LocalDateTime viewedAt) {
    this.viewedAt = viewedAt;
  }

  /** Checks if the history record is active. */
  public boolean isActive() {
    return active;
  }

  /** Sets the active status of the history record. */
  public void setActive(boolean active) {
    this.active = active;
  }
}
