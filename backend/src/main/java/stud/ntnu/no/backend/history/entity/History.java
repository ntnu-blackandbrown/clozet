package stud.ntnu.no.backend.history.entity;

import jakarta.persistence.*;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;

/**
 * Entity representing a history record in the browsing history.
 */
@Entity
@Table(name = "browsing_history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private LocalDateTime viewedAt;

    private boolean active = true; // For pause functionality

    // Getters and setters

    /**
     * Gets the ID of the history record.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the history record.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user associated with the history record.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with the history record.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the item associated with the history record.
     */
    public Item getItem() {
        return item;
    }

    /**
     * Sets the item associated with the history record.
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Gets the timestamp when the item was viewed.
     */
    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    /**
     * Sets the timestamp when the item was viewed.
     */
    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }

    /**
     * Checks if the history record is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the history record.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
