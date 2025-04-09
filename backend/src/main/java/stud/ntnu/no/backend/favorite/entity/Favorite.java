package stud.ntnu.no.backend.favorite.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

/**
 * Entity class for favorites. Represents a database record for a favorite with associated
 * attributes.
 */
@Entity
@Table(name = "favorites")
public class Favorite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  private Item item;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "active", nullable = false, columnDefinition = "boolean default false")
  private boolean active;

  /**
   * Empty constructor for Favorite. Required by JPA.
   */
  public Favorite() {
    this.createdAt = LocalDateTime.now();
    this.active = false;
  }

  /**
   * Constructor to create a new Favorite object with all fields.
   *
   * @param id        The ID of the favorite
   * @param user      The user who owns the favorite
   * @param item      The item that is favorited
   * @param createdAt The creation timestamp
   * @param active    The status of the favorite (active/inactive)
   */
  public Favorite(Long id, User user, Item item, LocalDateTime createdAt, boolean active) {
    this.id = id;
    this.user = user;
    this.item = item;
    this.createdAt = createdAt;
    this.active = active;
  }

  /**
   * Retrieves the ID of the favorite.
   *
   * @return The ID of the favorite
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the favorite.
   *
   * @param id The ID of the favorite
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Retrieves the user's ID as a string.
   *
   * @return The user's ID as a string
   */
  public String getUserId() {
    return user != null ? user.getId().toString() : null;
  }

  /**
   * Retrieves the user object.
   *
   * @return The user who owns the favorite
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets the user for the favorite.
   *
   * @param user The user who will own the favorite
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Retrieves the item's ID.
   *
   * @return The item's ID
   */
  public Long getItemId() {
    return item != null ? item.getId() : null;
  }

  /**
   * Retrieves the item object.
   *
   * @return The item that is favorited
   */
  public Item getItem() {
    return item;
  }

  /**
   * Sets the item for the favorite.
   *
   * @param item The item to be favorited
   */
  public void setItem(Item item) {
    this.item = item;
  }

  /**
   * Retrieves the creation timestamp.
   *
   * @return The creation timestamp
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the creation timestamp.
   *
   * @param createdAt The creation timestamp
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Checks if the favorite is active.
   *
   * @return true if the favorite is active, otherwise false
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the active status of the favorite.
   *
   * @param active The active status of the favorite
   */
  public void setActive(boolean active) {
    this.active = active;
  }
}
