package stud.ntnu.no.backend.favorite.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for favoritter.
 * Brukes for å overføre favoritt-data mellom klient og server.
 */
public class FavoriteDTO {

    private Long id;
    private String userId;
    private Long itemId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Tom konstruktør for FavoriteDTO.
     */
    public FavoriteDTO() {
    }

    /**
     * Konstruktør for å opprette et nytt FavoriteDTO-objekt med alle feltene.
     *
     * @param id ID for favoritten
     * @param userId Brukerens ID
     * @param itemId ID for elementet som er favorisert
     * @param active Status for favoritten (aktiv/inaktiv)
     * @param createdAt Tidspunkt for opprettelse
     * @param updatedAt Tidspunkt for siste oppdatering
     */
    public FavoriteDTO(Long id, String userId, Long itemId, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Henter favorittens ID.
     *
     * @return Favorittens ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter favorittens ID.
     *
     * @param id Favorittens ID
     */
    public void setId(Long id) {
        this.id = id;
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
    public boolean isActive() {
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

    /**
     * Henter tidspunktet for opprettelse.
     *
     * @return Tidspunkt for opprettelse
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Setter tidspunktet for opprettelse.
     *
     * @param createdAt Tidspunkt for opprettelse
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Henter tidspunktet for siste oppdatering.
     *
     * @return Tidspunkt for siste oppdatering
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Setter tidspunktet for siste oppdatering.
     *
     * @param updatedAt Tidspunkt for siste oppdatering
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteDTO that = (FavoriteDTO) o;
        return active == that.active &&
               Objects.equals(id, that.id) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(itemId, that.itemId) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, itemId, active, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "FavoriteDTO{" +
               "id=" + id +
               ", userId='" + userId + '\'' +
               ", itemId=" + itemId +
               ", active=" + active +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}