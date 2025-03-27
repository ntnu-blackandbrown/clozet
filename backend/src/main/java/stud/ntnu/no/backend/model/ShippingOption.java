package stud.ntnu.no.backend.model;

import jakarta.persistence.*;
import stud.ntnu.no.backend.Item.Entity.Item;

import java.util.List;

@Entity
@Table(name = "shipping_options")
public class ShippingOption {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private int estimatedDays;
  private double price;
  private boolean isTracked;

  @OneToMany(mappedBy = "shippingOption")
  private List<Item> items;

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

  public int getEstimatedDays() {
    return estimatedDays;
  }

  public void setEstimatedDays(int estimatedDays) {
    this.estimatedDays = estimatedDays;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public boolean isTracked() {
    return isTracked;
  }

  public void setTracked(boolean tracked) {
    isTracked = tracked;
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }
}