package stud.ntnu.no.backend.favorite.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class FavoriteDTO {
  private Long id;
  private String userId;
  private Long itemId;
  private LocalDateTime createdAt;

  public FavoriteDTO() {
  }

  public FavoriteDTO(Long id, String userId, Long itemId, LocalDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.itemId = itemId;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FavoriteDTO that = (FavoriteDTO) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(userId, that.userId) &&
        Objects.equals(itemId, that.itemId) &&
        Objects.equals(createdAt, that.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, itemId, createdAt);
  }

  @Override
  public String toString() {
    return "FavoriteDTO{" +
        "id=" + id +
        ", userId='" + userId + '\'' +
        ", itemId=" + itemId +
        ", createdAt=" + createdAt +
        '}';
  }
}