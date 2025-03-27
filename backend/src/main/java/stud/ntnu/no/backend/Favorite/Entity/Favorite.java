package stud.ntnu.no.backend.Favorite.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private Long itemId;
    private String itemType;
    private LocalDateTime createdAt;

    public Favorite() {
    }

    public Favorite(Long id, String userId, Long itemId, String itemType, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.createdAt = createdAt;
    }

    public Favorite(String userId, Long itemId, String itemType) {
        this.userId = userId;
        this.itemId = itemId;
        this.itemType = itemType;
        this.createdAt = LocalDateTime.now();
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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}