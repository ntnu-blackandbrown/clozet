package stud.ntnu.no.backend.item.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.user.entity.User;

/**
 * Entity representing an item that can be sold or traded on the platform.
 * <p>
 * An item represents a physical good that a user (seller) has listed for sale or trade. Each item
 * belongs to a specific category, has a location, shipping options, and various descriptive
 * attributes that help potential buyers understand what is being offered.
 * </p>
 * <p>
 * Items have relationships with several other entities including users (sellers), favorites (from
 * interested buyers), browsing history, images, transactions, and messages.
 * </p>
 */
@Entity
@Table(name = "items", indexes = {
    @Index(name = "idx_seller_id", columnList = "seller_id"),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_location_id", columnList = "location_id"),
    @Index(name = "idx_shipping_option_id", columnList = "shipping_option_id")
})
public class Item {

  /**
   * The unique identifier for the item.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user who is selling this item. Required field that cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  /**
   * The category to which this item belongs. Required field that cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  /**
   * The physical location associated with this item. Required field that cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  /**
   * The shipping option available for this item. Required field that cannot be null.
   */
  @ManyToOne
  @JoinColumn(name = "shipping_option_id", nullable = false)
  private ShippingOption shippingOption;

  /**
   * The title or name of the item. Required field that cannot be null.
   */
  @Column(nullable = false)
  private String title;

  /**
   * A brief description of the item. Required field that cannot be null.
   */
  @Column(nullable = false)
  private String shortDescription;

  /**
   * A detailed description of the item. Required field that cannot be null.
   */
  @Column(nullable = false)
  private String longDescription;

  /**
   * The price of the item in the default currency. Required field that cannot be null.
   */
  @Column(nullable = false)
  private double price;

  /**
   * The latitude coordinate of the item's location. Required field that cannot be null.
   */
  @Column(nullable = false)
  private double latitude;

  /**
   * The longitude coordinate of the item's location. Required field that cannot be null.
   */
  @Column(nullable = false)
  private double longitude;

  /**
   * The physical condition of the item (e.g., "New", "Used", "Like New"). Required field that
   * cannot be null.
   */
  @Column(nullable = false)
  private String condition;

  /**
   * The size of the item, applicable for clothing, furniture, etc. Required field that cannot be
   * null.
   */
  @Column(nullable = false)
  private String size;

  /**
   * The brand or manufacturer of the item. Required field that cannot be null.
   */
  @Column(nullable = false)
  private String brand;

  /**
   * The color of the item. Required field that cannot be null.
   */
  @Column(nullable = false)
  private String color;

  /**
   * Flag indicating whether the item is still available for purchase. Required field that cannot be
   * null.
   */
  @Column(nullable = false)
  private boolean isAvailable;

  /**
   * Flag indicating whether Vipps payment is enabled for this item. Required field that cannot be
   * null.
   */
  @Column(nullable = false)
  private boolean isVippsPaymentEnabled;

  /**
   * The timestamp when the item was created. Required field that cannot be null.
   */
  @Column(nullable = false)
  private LocalDateTime createdAt;

  /**
   * The timestamp when the item was last updated. Required field that cannot be null.
   */
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  /**
   * Collection of users who have favorited this item.
   */
  @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Set<Favorite> favorites = new HashSet<>();

  /**
   * Collection of images associated with this item. Uses cascade to ensure that when an item is
   * deleted, its images are also deleted.
   */
  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ItemImage> images;


  /**
   * Collection of transactions involving this item.
   */
  @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Transaction> transactions;

  /**
   * Collection of messages related to this item.
   */
  @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Message> messages;

  /**
   * Default constructor. Initializes the favorites collection.
   */
  public Item() {
    this.favorites = new HashSet<>();
  }

  // Getters and setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getSeller() {
    return seller;
  }

  public void setSeller(User seller) {
    this.seller = seller;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public ShippingOption getShippingOption() {
    return shippingOption;
  }

  public void setShippingOption(ShippingOption shippingOption) {
    this.shippingOption = shippingOption;
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

  public Set<Favorite> getFavorites() {
    return favorites;
  }

  public void setFavorites(Set<Favorite> favorites) {
    this.favorites = favorites;
  }

  public List<ItemImage> getImages() {
    return images;
  }

  public void setImages(List<ItemImage> images) {
    this.images = images;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}