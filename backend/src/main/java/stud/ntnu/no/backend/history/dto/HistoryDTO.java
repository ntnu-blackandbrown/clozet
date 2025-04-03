package stud.ntnu.no.backend.history.dto;

import java.time.LocalDateTime;

public class HistoryDTO {
    private Long id;
    private Long userId;
    private Long itemId;
    private String itemTitle;
    private LocalDateTime viewedAt;
    private boolean active;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getItemTitle() {
        return itemTitle;
    }
    
    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}