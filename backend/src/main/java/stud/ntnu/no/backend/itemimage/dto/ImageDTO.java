package stud.ntnu.no.backend.itemimage.dto;

/**
 * Data Transfer Object (DTO) for images. Contains information about the image ID, URL, item ID,
 * primary status, and display order.
 */
public class ImageDTO {

  private Long id;
  private String url;
  private Long itemId;
  private boolean isPrimary;
  private int displayOrder;

  /**
   * Default constructor for ImageDTO.
   */
  public ImageDTO() {
  }

  /**
   * Constructor to create an ImageDTO with all fields.
   *
   * @param id           The ID of the image
   * @param url          The URL of the image
   * @param itemId       The ID of the item
   * @param isPrimary    The primary status of the image
   * @param displayOrder The display order of the image
   */
  public ImageDTO(Long id, String url, Long itemId, boolean isPrimary, int displayOrder) {
    this.id = id;
    this.url = url;
    this.itemId = itemId;
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
   * Retrieves the URL of the image.
   *
   * @return The URL of the image
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the URL of the image.
   *
   * @param url The URL of the image
   */
  public void setUrl(String url) {
    this.url = url;
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