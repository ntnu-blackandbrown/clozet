package stud.ntnu.no.backend.itemimage.dto;

/**
 * Data Transfer Object (DTO) for creating an item image. Contains information about the item ID,
 * image URL, primary status, and display order.
 */
public class CreateItemImageDTO {

  private Long itemId;
  private String imageUrl;
  private boolean isPrimary;
  private int displayOrder;

  // Constructor
  public CreateItemImageDTO() {
  }

  /**
   * Retrieves the item ID.
   *
   * @return The ID of the item
   */
  public Long getItemId() {
    return itemId;
  }

  /**
   * Sets the item ID.
   *
   * @param itemId The ID of the item
   */
  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  /**
   * Retrieves the image URL.
   *
   * @return The URL of the image
   */
  public String getImageUrl() {
    return imageUrl;
  }

  /**
   * Sets the image URL.
   *
   * @param imageUrl The URL of the image
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  /**
   * Checks if the image is primary.
   *
   * @return true if the image is primary, otherwise false
   */
  public boolean isPrimary() {
    return isPrimary;
  }

  /**
   * Sets the primary status of the image.
   *
   * @param isPrimary The primary status of the image
   */
  public void setIsPrimary(boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  /**
   * Retrieves the display order of the image.
   *
   * @return The display order of the image
   */
  public int getDisplayOrder() {
    return displayOrder;
  }

  /**
   * Sets the display order of the image.
   *
   * @param displayOrder The display order of the image
   */
  public void setDisplayOrder(int displayOrder) {
    this.displayOrder = displayOrder;
  }
}