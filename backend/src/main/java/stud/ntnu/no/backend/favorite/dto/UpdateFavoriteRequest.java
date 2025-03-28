package stud.ntnu.no.backend.favorite.dto;

import java.util.Objects;

public class UpdateFavoriteRequest {
    private String itemType;

    public UpdateFavoriteRequest() {
    }

    public UpdateFavoriteRequest(String itemType) {
        this.itemType = itemType;
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
        UpdateFavoriteRequest that = (UpdateFavoriteRequest) o;
        return Objects.equals(itemType, that.itemType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemType);
    }

    @Override
    public String toString() {
        return "UpdateFavoriteRequest{" +
                "itemType='" + itemType + '\'' +
                '}';
    }
}