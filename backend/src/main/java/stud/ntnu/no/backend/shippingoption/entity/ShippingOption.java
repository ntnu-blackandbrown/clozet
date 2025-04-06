package stud.ntnu.no.backend.shippingoption.entity;

import jakarta.persistence.*;
import stud.ntnu.no.backend.item.entity.Item;

import java.util.List;

/**
 * Entity representing a shipping option.
 * <p>
 * This class is mapped to the "shipping_options" table in the database and holds
 * information about a shipping option, including its ID, name, description,
 * estimated delivery days, price, tracking status, and associated items.
 */
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

  /**
   * Returns the ID of the shipping option.
   *
   * @return the ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the shipping option.
   *
   * @param id the ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the name of the shipping option.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the shipping option.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the description of the shipping option.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the shipping option.
   *
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the estimated delivery days for the shipping option.
   *
   * @return the estimated delivery days
   */
  public int getEstimatedDays() {
    return estimatedDays;
  }

  /**
   * Sets the estimated delivery days for the shipping option.
   *
   * @param estimatedDays the estimated delivery days to set
   */
  public void setEstimatedDays(int estimatedDays) {
    this.estimatedDays = estimatedDays;
  }

  /**
   * Returns the price of the shipping option.
   *
   * @return the price
   */
  public double getPrice() {
    return price;
  }

  /**
   * Sets the price of the shipping option.
   *
   * @param price the price to set
   */
  public void setPrice(double price) {
    this.price = price;
  }

  /**
   * Returns whether the shipping option is tracked.
   *
   * @return true if tracked, false otherwise
   */
  public boolean isTracked() {
    return isTracked;
  }

  /**
   * Sets whether the shipping option is tracked.
   *
   * @param tracked true if tracked, false otherwise
   */
  public void setTracked(boolean tracked) {
    isTracked = tracked;
  }

  /**
   * Returns the list of items associated with the shipping option.
   *
   * @return the list of items
   */
  public List<Item> getItems() {
    return items;
  }

  /**
   * Sets the list of items associated with the shipping option.
   *
   * @param items the list of items to set
   */
  public void setItems(List<Item> items) {
    this.items = items;
  }
}