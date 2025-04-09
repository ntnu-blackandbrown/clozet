package stud.ntnu.no.backend.itemimage.dto;

/**
 * Data Transfer Object (DTO) for updating an item image. Contains information about the image URL,
 * primary status, and display order.
 */
public class UpdateItemImageRequest {

  private String imageUrl;
  private Boolean isPrimary;
  private Integer displayOrder;

  // Constructor
  public UpdateItemImageRequest() {}

  // Getters and setters

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
   * Retrieves the primary status of the image.
   *
   * @return true if the image is primary, otherwise false
   */
  public Boolean getIsPrimary() {
    return isPrimary;
  }

  /**
   * Sets the primary status of the image.
   *
   * @param isPrimary The primary status of the image
   */
  public void setIsPrimary(Boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  /**
   * Retrieves the display order of the image.
   *
   * @return The display order of the image
   */
  public Integer getDisplayOrder() {
    return displayOrder;
  }

  /**
   * Sets the display order of the image.
   *
   * @param displayOrder The display order of the image
   */
  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }
}
