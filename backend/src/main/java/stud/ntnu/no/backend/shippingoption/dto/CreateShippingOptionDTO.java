package stud.ntnu.no.backend.shippingoption.dto;

/**
 * Data Transfer Object for creating a shipping option.
 * <p>
 * This class holds information required to create a new shipping option, including its name,
 * description, estimated delivery days, price, and tracking status.
 */
public class CreateShippingOptionDTO {

  private String name;
  private String description;
  private int estimatedDays;
  private double price;
  private boolean isTracked;

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
}