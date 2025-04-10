package stud.ntnu.no.backend.itemimage.dto;

/**
 * Data Transfer Object (DTO) for item images. Contains information about the image ID, item ID,
 * image URL, primary status, and display order.
 */
public class ItemImageDTO {

  private Long id;
  private Long itemId;
  private String imageUrl;
  private boolean isPrimary;
  private int displayOrder;

  /**
   * Default constructor
   */
  public ItemImageDTO() {
  }

  /**
   * Constructor with fields
   *
   * @param id           The ID of the image
   * @param itemId       The ID of the item
   * @param imageUrl     The URL of the image
   * @param isPrimary    The primary status of the image
   * @param displayOrder The display order of the image
   */
  public ItemImageDTO(Long id, Long itemId, String imageUrl, boolean isPrimary, int displayOrder) {
    this.id = id;
    this.itemId = itemId;
    this.imageUrl = imageUrl;
    this.isPrimary = isPrimary;
    this.displayOrder = displayOrder;
  }

  /**
   * Retrieves the ID of the image.
   *
   * @return The ID of the image
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the image.
   *
   * @param id The ID of the image
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retrieves the ID of the item.
   *
   * @return The ID of the item
   */
  public Long getItemId() {
    return itemId;
  }

  /**
   * Sets the ID of the item.
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
   * @param primary The primary status of the image
   */
  public void setPrimary(boolean primary) {
    isPrimary = primary;
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