package stud.ntnu.no.backend.itemimage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import stud.ntnu.no.backend.item.entity.Item;

/**
 * Entity class representing an image associated with an item. Maps to the "item_images" table in
 * the database.
 */
@Entity
@Table(name = "item_images")
public class ItemImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  private String imageUrl;
  private boolean isPrimary;
  private int displayOrder;

  /**
   * Default constructor for ItemImage.
   */
  public ItemImage() {
  }

  /**
   * Constructor to create an ItemImage with specified attributes.
   *
   * @param imageUrl     The URL of the image
   * @param isPrimary    Whether the image is primary
   * @param displayOrder The display order of the image
   */
  public ItemImage(String imageUrl, boolean isPrimary, int displayOrder) {
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
   * Retrieves the associated item.
   *
   * @return The item associated with the image
   */
  public Item getItem() {
    return item;
  }

  /**
   * Sets the associated item.
   *
   * @param item The item to associate with the image
   */
  public void setItem(Item item) {
    this.item = item;
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