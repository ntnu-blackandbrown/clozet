package stud.ntnu.no.backend.favorite.entity;

import jakarta.persistence.*;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;
import java.time.LocalDateTime;

/**
 * Entitetsklasse for favoritter.
 * Representerer en databasepost for en favoritt med tilhørende attributter.
 */
@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "active", nullable = false, columnDefinition = "boolean default false")
    private boolean active;

    /**
     * Tom konstruktør for Favorite.
     * Kreves av JPA.
     */
    public Favorite() {
        this.createdAt = LocalDateTime.now();
        this.active = false;
    }

    /**
     * Konstruktør for å opprette et nytt Favorite-objekt med alle feltene.
     *
     * @param id ID for favoritten
     * @param user Brukeren som eier favoritten
     * @param item Elementet som er favorisert
     * @param createdAt Tidspunkt for opprettelse
     * @param active Status for favoritten (aktiv/inaktiv)
     */
    public Favorite(Long id, User user, Item item, LocalDateTime createdAt, boolean active) {
        this.id = id;
        this.user = user;
        this.item = item;
        this.createdAt = createdAt;
        this.active = active;
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
     * @return Brukerens ID som string
     */
    public String getUserId() {
        return user != null ? user.getId().toString() : null;
    }

    /**
     * Henter brukerobjektet.
     *
     * @return Brukeren som eier favoritten
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter brukeren for favoritten.
     *
     * @param user Brukeren som skal eie favoritten
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Henter elementets ID.
     *
     * @return Elementets ID
     */
    public Long getItemId() {
        return item != null ? item.getId() : null;
    }

    /**
     * Henter elementobjektet.
     *
     * @return Elementet som er favorisert
     */
    public Item getItem() {
        return item;
    }

    /**
     * Setter elementet for favoritten.
     *
     * @param item Elementet som skal favoriseres
     */
    public void setItem(Item item) {
        this.item = item;
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
}
