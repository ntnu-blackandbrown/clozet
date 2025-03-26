package stud.ntnu.no.backend.Category.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class CategoryDTO {
  private Long id;
  @NotBlank(message = "Name is required")
  @Size(min = 3, max = 100)
  private String name;

  @NotBlank(message = "Description is required")
  @Size(max = 255)
  private String description;
  private Long parentId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<Long> subcategoryIds;
  private String parentName;

  // Getters and setters


  public List<Long> getSubcategoryIds() {
    return subcategoryIds;
  }

  public void setSubcategoryIds(List<Long> subcategoryIds) {
    this.subcategoryIds = subcategoryIds;
  }



  // Add getter and setter
  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

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

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
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
}