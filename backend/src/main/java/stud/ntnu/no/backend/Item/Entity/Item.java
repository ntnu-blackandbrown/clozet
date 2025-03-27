package stud.ntnu.no.backend.Item.Entity;

import jakarta.persistence.*;
import stud.ntnu.no.backend.Category.Entity.Category;
import stud.ntnu.no.backend.Location.Entity.Location;
import stud.ntnu.no.backend.User.Entity.User;
import stud.ntnu.no.backend.model.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items", indexes = {
    @Index(name = "idx_seller_id", columnList = "seller_id"),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_location_id", columnList = "location_id"),
    @Index(name = "idx_shipping_option_id", columnList = "shipping_option_id")
})
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @ManyToOne
  @JoinColumn(name = "shipping_option_id", nullable = false)
  private ShippingOption shippingOption;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String shortDescription;

  @Column(nullable = false)
  private String longDescription;

  @Column(nullable = false)
  private double price;

  @Column(nullable = false)
  private double latitude;

  @Column(nullable = false)
  private double longitude;

  @Column(nullable = false)
  private String condition;

  @Column(nullable = false)
  private String size;

  @Column(nullable = false)
  private String brand;

  @Column(nullable = false)
  private String color;

  @Column(nullable = false)
  private boolean isAvailable;

  @Column(nullable = false)
  private boolean isVippsPaymentEnabled;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "item")
  private List<Favorite> favorites;

  @OneToMany(mappedBy = "item")
  private List<ItemImage> images;

  @OneToMany(mappedBy = "item")
  private List<Transaction> transactions;

  @OneToMany(mappedBy = "item")
  private List<Message> messages;

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

  public List<Favorite> getFavorites() {
    return favorites;
  }

  public void setFavorites(List<Favorite> favorites) {
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