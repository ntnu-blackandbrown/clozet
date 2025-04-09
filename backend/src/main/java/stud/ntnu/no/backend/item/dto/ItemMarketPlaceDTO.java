package stud.ntnu.no.backend.item.dto;

/**
 * DTO for displaying marketplace item information.
 */
public class ItemMarketPlaceDTO {

  private Long id;
  private String title;
  private double price;
  private String category;
  private String image;
  private String location;
  private boolean isVippsPaymentEnabled;
  private boolean isWishlisted;

  // Default constructor
  public ItemMarketPlaceDTO() {
  }

  // Full constructor
  public ItemMarketPlaceDTO(Long id, String title, double price, String category,
      String image, String location, boolean isVippsPaymentEnabled,
      boolean isWishlisted) {
    this.id = id;
    this.title = title;
    this.price = price;
    this.category = category;
    this.image = image;
    this.location = location;
    this.isVippsPaymentEnabled = isVippsPaymentEnabled;
    this.isWishlisted = isWishlisted;
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

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public boolean isVippsPaymentEnabled() {
    return isVippsPaymentEnabled;
  }

  public void setVippsPaymentEnabled(boolean vippsPaymentEnabled) {
    this.isVippsPaymentEnabled = vippsPaymentEnabled;
  }

  public boolean isWishlisted() {
    return isWishlisted;
  }

  public void setWishlisted(boolean wishlisted) {
    isWishlisted = wishlisted;
  }
}
