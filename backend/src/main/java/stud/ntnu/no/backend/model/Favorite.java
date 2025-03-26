package stud.ntnu.no.backend.model;

import jakarta.persistence.*;
import stud.ntnu.no.backend.User.Entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_item_id", columnList = "item_id")
})
public class Favorite {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  // Getters and setters


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}