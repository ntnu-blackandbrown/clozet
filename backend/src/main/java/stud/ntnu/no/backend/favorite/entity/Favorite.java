package stud.ntnu.no.backend.favorite.entity;

import jakarta.persistence.*;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "item_id", insertable = false, updatable = false)
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId().toString();
        }
    }

    public Long getItemId() {
        return item != null ? item.getId() : itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        if (item != null) {
            this.itemId = item.getId();
        }
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