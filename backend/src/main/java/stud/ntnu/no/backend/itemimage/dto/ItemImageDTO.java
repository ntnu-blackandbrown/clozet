package stud.ntnu.no.backend.itemimage.dto;

public class ItemImageDTO {
    private Long id;
    private Long itemId;
    private String imageUrl;
    private boolean isPrimary;
    private int displayOrder;

    // Constructor
    public ItemImageDTO(Long id, Long itemId, String imageUrl, boolean isPrimary, int displayOrder) {
        this.id = id;
        this.itemId = itemId;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}