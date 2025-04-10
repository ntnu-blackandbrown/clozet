package stud.ntnu.no.backend.item.dto;

import java.time.LocalDateTime;
import java.util.List;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;

/**
 * Data Transfer Object (DTO) for transferring complete item information.
 * <p>
 * This class represents a comprehensive view of an item with all its attributes and associated
 * data. It includes details such as basic item information (title, descriptions, price), category
 * information, seller information, location details, physical attributes, availability status,
 * payment options, timestamps, and associated images.
 * </p>
 * <p>
 * The ItemDTO is used for:
 * <ul>
 *   <li>Returning detailed item data in API responses</li>
 *   <li>Transferring complete item information between service layers</li>
 *   <li>Displaying item details in user interfaces</li>
 * </ul>
 * </p>
 * <p>
 * Unlike the {@link ItemMarketPlaceDTO} which provides a simplified view for marketplace browsing,
 * this DTO contains all information about an item, including administrative details.
 * </p>
 */
public class ItemDTO {

  private Long id;
  private String title;
  private String shortDescription;
  private String longDescription;
  private double price;
  private Long categoryId;
  private String categoryName;
  private Long sellerId;
  private String sellerName;
  private Long locationId;
  private String locationName;
  private Long shippingOptionId;
  private String shippingOptionName;
  private double latitude;
  private double longitude;
  private String condition;
  private String size;
  private String brand;
  private String color;
  private boolean isAvailable;
  private boolean isVippsPaymentEnabled;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<ItemImageDTO> images;


  // Constructors
  public ItemDTO() {
  }

  public ItemDTO(Long id, String title, String shortDescription, String longDescription,
      double price,
      Long categoryId, String categoryName, Long sellerId, String sellerName,
      Long locationId, String locationName, Long shippingOptionId, String shippingOptionName,
      double latitude, double longitude, String condition, String size,
      String brand, String color, boolean isAvailable, boolean isVippsPaymentEnabled,
      LocalDateTime createdAt, LocalDateTime updatedAt, List<ItemImageDTO> images) {
    this.id = id;
    this.title = title;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
    this.price = price;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.sellerId = sellerId;
    this.sellerName = sellerName;
    this.locationId = locationId;
    this.locationName = locationName;
    this.shippingOptionId = shippingOptionId;
    this.shippingOptionName = shippingOptionName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.condition = condition;
    this.size = size;
    this.brand = brand;
    this.color = color;
    this.isAvailable = isAvailable;
    this.isVippsPaymentEnabled = isVippsPaymentEnabled;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.images = images;
  }

  // Add getter and setter for images
  public List<ItemImageDTO> getImages() {
    return images;
  }

  public void setImages(List<ItemImageDTO> images) {
    this.images = images;
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public Long getSellerId() {
    return sellerId;
  }

  public void setSellerId(Long sellerId) {
    this.sellerId = sellerId;
  }

  public String getSellerName() {
    return sellerName;
  }

  public void setSellerName(String sellerName) {
    this.sellerName = sellerName;
  }

  public Long getLocationId() {
    return locationId;
  }

  public void setLocationId(Long locationId) {
    this.locationId = locationId;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public Long getShippingOptionId() {
    return shippingOptionId;
  }

  public void setShippingOptionId(Long shippingOptionId) {
    this.shippingOptionId = shippingOptionId;
  }

  public String getShippingOptionName() {
    return shippingOptionName;
  }

  public void setShippingOptionName(String shippingOptionName) {
    this.shippingOptionName = shippingOptionName;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  public boolean isVippsPaymentEnabled() {
    return isVippsPaymentEnabled;
  }

  public void setVippsPaymentEnabled(boolean vippsPaymentEnabled) {
    isVippsPaymentEnabled = vippsPaymentEnabled;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
