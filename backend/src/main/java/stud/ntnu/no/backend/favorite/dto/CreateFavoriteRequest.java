package stud.ntnu.no.backend.favorite.dto;

import java.util.Objects;

/**
 * DTO for å opprette eller oppdatere en favoritt.
 * Inneholder nødvendig informasjon for å lage eller endre en favoritt.
 */
public class CreateFavoriteRequest {

    private String userId;
    private Long itemId;
    private boolean active;

    /**
     * Tom konstruktør for CreateFavoriteRequest.
     */
    public CreateFavoriteRequest() {
    }

    /**
     * Konstruktør for å opprette et nytt CreateFavoriteRequest-objekt.
     *
     * @param userId Brukerens ID
     * @param itemId ID for elementet som skal favoriseres
     * @param active Angir om favoritten er aktiv eller ikke
     */
    public CreateFavoriteRequest(String userId, Long itemId, boolean active) {
        this.userId = userId;
        this.itemId = itemId;
        this.active = active;
    }

    /**
     * Henter brukerens ID.
     *
     * @return Brukerens ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Setter brukerens ID.
     *
     * @param userId Brukerens ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Henter elementets ID.
     *
     * @return Elementets ID
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * Setter elementets ID.
     *
     * @param itemId Elementets ID
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * Sjekker om favoritten er aktiv.
     *
     * @return true hvis favoritten er aktiv, ellers false
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Setter favorittens aktive status.
     *
     * @param active Favorittens aktive status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
