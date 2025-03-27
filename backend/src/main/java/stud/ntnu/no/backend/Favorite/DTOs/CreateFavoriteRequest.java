package stud.ntnu.no.backend.Favorite.DTOs;

import java.util.Objects;

public class CreateFavoriteRequest {
    private String userId;
    private Long itemId;
    private String itemType;

    public CreateFavoriteRequest() {
    }

    public CreateFavoriteRequest(String userId, Long itemId, String itemType) {
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateFavoriteRequest that = (CreateFavoriteRequest) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(itemId, that.itemId) &&
                Objects.equals(itemType, that.itemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemId, itemType);
    }

    @Override
    public String toString() {
        return "CreateFavoriteRequest{" +
                "userId='" + userId + '\'' +
                ", itemId=" + itemId +
                ", itemType='" + itemType + '\'' +
                '}';
    }
}