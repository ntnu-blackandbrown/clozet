package stud.ntnu.no.backend.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import stud.ntnu.no.backend.item.entity.Item;

/**
 * Entity representing a category in the system. Categories can have hierarchical relationships with
 * parent-child associations. They are also related to items that belong to them.
 */
@Entity
@Table(name = "categories")
public class Category {

  /** Unique identifier for the category */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Unique name of the category */
  @Column(unique = true, nullable = false)
  private String name;

  /** Description of what the category contains */
  @Column(unique = true, nullable = false)
  private String description;

  /** Parent category, null if this is a top-level category */
  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Category parent;

  /** Timestamp when the category was created */
  private LocalDateTime createdAt;

  /** Timestamp when the category was last updated */
  private LocalDateTime updatedAt;

  /** Items belonging to this category */
  @OneToMany(mappedBy = "category")
  private List<Item> items;

  /** Direct subcategories of this category */
  @OneToMany(mappedBy = "parent")
  private List<Category> subcategories;

  // Getters and setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Category getParent() {
    return parent;
  }

  public void setParent(Category parent) {
    this.parent = parent;
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

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public List<Category> getSubcategories() {
    return subcategories;
  }

  public void setSubcategories(List<Category> subcategories) {
    this.subcategories = subcategories;
  }
}
