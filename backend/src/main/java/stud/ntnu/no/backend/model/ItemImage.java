package stud.ntnu.no.backend.model;

import jakarta.persistence.*;
import stud.ntnu.no.backend.Item.Entity.Item;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_images", indexes = {
    @Index(name = "idx_item_id", columnList = "item_id")
})
public class ItemImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  @Column(nullable = false)
  private String imageUrl;

  @Column(nullable = false)
  private boolean isPrimary;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  // Getters and setters


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public boolean isPrimary() {
    return isPrimary;
  }

  public void setPrimary(boolean primary) {
    isPrimary = primary;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}