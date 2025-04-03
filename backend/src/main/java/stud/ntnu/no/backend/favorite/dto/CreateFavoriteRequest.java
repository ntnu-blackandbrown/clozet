package stud.ntnu.no.backend.favorite.dto;

import java.util.Objects;

public class CreateFavoriteRequest {
    private String userId;
    private Long itemId;
    private Boolean isActive; // Wrapper type for å tillate null

    public CreateFavoriteRequest() {
    }

    public CreateFavoriteRequest(String userId, Long itemId, boolean isActive) {
        this.userId = userId;
        this.itemId = itemId;
        this.isActive = isActive;
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

    // Endret fra boolean isActive() til Boolean getIsActive()
    public Boolean getIsActive() {
        return isActive;
    }

    // Setter kan beholdes som den er, eller endres til å bruke Boolean om du ønsker konsistens
    public void setIsActive(Boolean active) {
        this.isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateFavoriteRequest that = (CreateFavoriteRequest) o;
        return Objects.equals(userId, that.userId) &&
            Objects.equals(itemId, that.itemId) &&
            Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemId, isActive);
    }

    @Override
    public String toString() {
        return "CreateFavoriteRequest{" +
            "userId='" + userId + '\'' +
            ", itemId=" + itemId +
            ", isActive=" + isActive +
            '}';
    }
}
