package stud.ntnu.no.backend.item.dto;

import java.time.LocalDateTime;
import java.util.List;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;

/** DTO for displaying detailed product information. */
public class ProductDisplayDTO {

  private Long id;
  private String title;
  private String fullDescription;
  private double price;
  private Long categoryId;
  private String categoryName;
  private String locationName;
  private String sellerFullName;
  private String shippingOptionName;
  private boolean isAvailable;
  private LocalDateTime updatedAt;
  private LocalDateTime createdAt;
  private List<ItemImageDTO> images;

  // Default constructor
  public ProductDisplayDTO() {}

  // Full constructor
  public ProductDisplayDTO(
      Long id,
      String title,
      String fullDescription,
      double price,
      Long categoryId,
      String categoryName,
      String locationName,
      String sellerFullName,
      String shippingOptionName,
      boolean isAvailable,
      LocalDateTime updatedAt,
      LocalDateTime createdAt,
      List<ItemImageDTO> images) {
    this.id = id;
    this.title = title;
    this.fullDescription = fullDescription;
    this.price = price;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.locationName = locationName;
    this.sellerFullName = sellerFullName;
    this.shippingOptionName = shippingOptionName;
    this.isAvailable = isAvailable;
    this.updatedAt = updatedAt;
    this.createdAt = createdAt;
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

  public String getFullDescription() {
    return fullDescription;
  }

  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
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

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getSellerFullName() {
    return sellerFullName;
  }

  public void setSellerFullName(String sellerFullName) {
    this.sellerFullName = sellerFullName;
  }

  public String getShippingOptionName() {
    return shippingOptionName;
  }

  public void setShippingOptionName(String shippingOptionName) {
    this.shippingOptionName = shippingOptionName;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<ItemImageDTO> getImages() {
    return images;
  }

  public void setImages(List<ItemImageDTO> images) {
    this.images = images;
  }
}
