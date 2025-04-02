package stud.ntnu.no.backend.itemimage.dto;

public class ImageDTO {
    private Long id;
    private String url;
    private Long itemId;
    private boolean isPrimary;
    private int displayOrder;

    public ImageDTO() {
    }

    public ImageDTO(Long id, String url, Long itemId, boolean isPrimary, int displayOrder) {
        this.id = id;
        this.url = url;
        this.itemId = itemId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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