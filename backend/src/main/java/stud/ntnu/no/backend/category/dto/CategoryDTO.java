package stud.ntnu.no.backend.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Category entities. Contains validation constraints and represents the
 * data structure used for API communication with clients.
 */
public class CategoryDTO {

  /**
   * Unique identifier for the category
   */
  private Long id;

  /**
   * Name of the category with validation constraints
   */
  @NotBlank(message = "Name is required")
  @Size(min = 3, max = 100)
  private String name;

  /**
   * Description of the category with validation constraints
   */
  @NotBlank(message = "Description is required")
  @Size(max = 255)
  private String description;

  /**
   * ID of parent category, null if it's a top-level category
   */
  private Long parentId;

  /**
   * Timestamp when the category was created
   */
  private LocalDateTime createdAt;

  /**
   * Timestamp when the category was last updated
   */
  private LocalDateTime updatedAt;

  /**
   * List of IDs of subcategories belonging to this category
   */
  private List<Long> subcategoryIds;

  /**
   * Name of the parent category, null if it's a top-level category
   */
  private String parentName;

  // Getters and setters

  /**
   * Gets the list of subcategory IDs.
   *
   * @return List of subcategory IDs
   */
  public List<Long> getSubcategoryIds() {
    return subcategoryIds;
  }

  /**
   * Sets the list of subcategory IDs.
   *
   * @param subcategoryIds List of subcategory IDs
   */
  public void setSubcategoryIds(List<Long> subcategoryIds) {
    this.subcategoryIds = subcategoryIds;
  }

  /**
   * Gets the name of the parent category.
   *
   * @return Parent category name
   */
  public String getParentName() {
    return parentName;
  }

  /**
   * Sets the name of the parent category.
   *
   * @param parentName Parent category name
   */
  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  /**
   * Gets the unique identifier for the category.
   *
   * @return Category ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique identifier for the category.
   *
   * @param id Category ID
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the name of the category.
   *
   * @return Category name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the category.
   *
   * @param name Category name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description of the category.
   *
   * @return Category description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the category.
   *
   * @param description Category description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the ID of the parent category.
   *
   * @return Parent category ID
   */
  public Long getParentId() {
    return parentId;
  }

  /**
   * Sets the ID of the parent category.
   *
   * @param parentId Parent category ID
   */
  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  /**
   * Gets the timestamp when the category was created.
   *
   * @return Creation timestamp
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the timestamp when the category was created.
   *
   * @param createdAt Creation timestamp
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Gets the timestamp when the category was last updated.
   *
   * @return Update timestamp
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Sets the timestamp when the category was last updated.
   *
   * @param updatedAt Update timestamp
   */
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
