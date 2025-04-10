package stud.ntnu.no.backend.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import stud.ntnu.no.backend.itemimage.dto.CreateItemImageDTO;

/**
 * DTO for creating a new item.
 *
 * <p>This class encapsulates all required data for creating a new item in the system,
 * including basic item details, location information, and associated images.</p>
 *
 * <p>All required fields are validated with appropriate constraints.</p>
 */
public class CreateItemDTO {

  @NotBlank(message = "Title is required")
  private String title;

  @NotBlank(message = "Short description is required")
  private String shortDescription;

  @NotBlank(message = "Long description is required")
  private String longDescription;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  private double price;

  @NotNull(message = "Category ID is required")
  private Long categoryId;

  @NotNull(message = "Location ID is required")
  private Long locationId;

  @NotNull(message = "Shipping option ID is required")
  private Long shippingOptionId;

  @NotNull(message = "Latitude is required")
  private double latitude;

  @NotNull(message = "Longitude is required")
  private double longitude;

  @NotBlank(message = "Condition is required")
  private String condition;

  @NotBlank(message = "Size is required")
  private String size;

  @NotBlank(message = "Brand is required")
  private String brand;

  @NotBlank(message = "Color is required")
  private String color;

  private boolean isVippsPaymentEnabled;

  private List<CreateItemImageDTO> images;

  /**
   * Default constructor.
   */
  public CreateItemDTO() {
  }

  /**
   * Full constructor with all item properties.
   *
   * @param title                 the item title
   * @param shortDescription      brief description of the item
   * @param longDescription       detailed description of the item
   * @param price                 the item price
   * @param categoryId            the ID of the item's category
   * @param locationId            the ID of the item's location
   * @param shippingOptionId      the ID of the shipping option
   * @param latitude              geographical latitude of the item
   * @param longitude             geographical longitude of the item
   * @param condition             physical condition of the item
   * @param size                  size of the item
   * @param brand                 brand of the item
   * @param color                 color of the item
   * @param isVippsPaymentEnabled whether Vipps payment is enabled
   * @param images                list of images associated with the item
   */
  public CreateItemDTO(String title, String shortDescription, String longDescription, double price,
      Long categoryId, Long locationId, Long shippingOptionId, double latitude, double longitude,
      String condition, String size, String brand, String color, boolean isVippsPaymentEnabled,
      List<CreateItemImageDTO> images) {
    this.title = title;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.price = price;
    this.categoryId = categoryId;
    this.locationId = locationId;
    this.shippingOptionId = shippingOptionId;
    this.latitude = latitude;
    this.longitude = longitude;
    this.condition = condition;
    this.size = size;
    this.brand = brand;
    this.color = color;
    this.isVippsPaymentEnabled = isVippsPaymentEnabled;
    this.images = images;
  }

  /**
   * Returns the list of images for this item.
   *
   * @return the images associated with this item
   */
  public List<CreateItemImageDTO> getImages() {
    return images;
  }

  /**
   * Sets the list of images for this item.
   *
   * @param images the images to associate with this item
   */
  public void setImages(List<CreateItemImageDTO> images) {
    this.images = images;
  }

  /**
   * Returns the title of this item.
   *
   * @return the item title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of this item.
   *
   * @param title the item title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the short description of this item.
   *
   * @return the short description
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Sets the short description of this item.
   *
   * @param shortDescription the short description to set
   */
  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  /**
   * Returns the long description of this item.
   *
   * @return the long description
   */
  public String getLongDescription() {
    return longDescription;
  }

  /**
   * Sets the long description of this item.
   *
   * @param longDescription the long description to set
   */
  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  /**
   * Returns the price of this item.
   *
   * @return the item price
   */
  public double getPrice() {
    return price;
  }

  /**
   * Sets the price of this item.
   *
   * @param price the item price to set
   */
  public void setPrice(double price) {
    this.price = price;
  }

  /**
   * Returns the category ID of this item.
   *
   * @return the category ID
   */
  public Long getCategoryId() {
    return categoryId;
  }

  /**
   * Sets the category ID of this item.
   *
   * @param categoryId the category ID to set
   */
  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * Returns the location ID of this item.
   *
   * @return the location ID
   */
  public Long getLocationId() {
    return locationId;
  }

  /**
   * Sets the location ID of this item.
   *
   * @param locationId the location ID to set
   */
  public void setLocationId(Long locationId) {
    this.locationId = locationId;
  }

  /**
   * Returns the shipping option ID of this item.
   *
   * @return the shipping option ID
   */
  public Long getShippingOptionId() {
    return shippingOptionId;
  }

  /**
   * Sets the shipping option ID of this item.
   *
   * @param shippingOptionId the shipping option ID to set
   */
  public void setShippingOptionId(Long shippingOptionId) {
    this.shippingOptionId = shippingOptionId;
  }

  /**
   * Returns the latitude of this item's location.
   *
   * @return the geographical latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * Sets the latitude of this item's location.
   *
   * @param latitude the geographical latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  /**
   * Returns the longitude of this item's location.
   *
   * @return the geographical longitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * Sets the longitude of this item's location.
   *
   * @param longitude the geographical longitude to set
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  /**
   * Returns the physical condition of this item.
   *
   * @return the item condition
   */
  public String getCondition() {
    return condition;
  }

  /**
   * Sets the physical condition of this item.
   *
   * @param condition the item condition to set
   */
  public void setCondition(String condition) {
    this.condition = condition;
  }

  /**
   * Returns the size of this item.
   *
   * @return the item size
   */
  public String getSize() {
    return size;
  }

  /**
   * Sets the size of this item.
   *
   * @param size the item size to set
   */
  public void setSize(String size) {
    this.size = size;
  }

  /**
   * Returns the brand of this item.
   *
   * @return the item brand
   */
  public String getBrand() {
    return brand;
  }

  /**
   * Sets the brand of this item.
   *
   * @param brand the item brand to set
   */
  public void setBrand(String brand) {
    this.brand = brand;
  }

  /**
   * Returns the color of this item.
   *
   * @return the item color
   */
  public String getColor() {
    return color;
  }

  /**
   * Sets the color of this item.
   *
   * @param color the item color to set
   */
  public void setColor(String color) {
    this.color = color;
  }

  /**
   * Returns whether Vipps payment is enabled for this item.
   *
   * @return true if Vipps payment is enabled, false otherwise
   */
  public boolean isVippsPaymentEnabled() {
    return isVippsPaymentEnabled;
  }

  /**
   * Sets whether Vipps payment is enabled for this item.
   *
   * @param vippsPaymentEnabled true to enable Vipps payment, false otherwise
   */
  public void setVippsPaymentEnabled(boolean vippsPaymentEnabled) {
    this.isVippsPaymentEnabled = vippsPaymentEnabled;
  }
}