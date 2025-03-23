package stud.ntnu.no.backend.model;

import jakarta.persistence.*;
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
  private String description;

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
  private boolean isVipPaymentEnabled;

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
}