package stud.ntnu.no.backend.location.dto;

/**
 * Represents a Data Transfer Object for creating a location.
 * <p>
 * This class holds the necessary information to create a new location, including city, region,
 * latitude, and longitude.
 */
public class CreateLocationDTO {

  private String city;
  private String region;
  private double latitude;
  private double longitude;

  // Getters and setters

  /**
   * Returns the city of the location.
   *
   * @return the city
   */
  public String getCity() {
    return city;
  }

  /**
   * Sets the city of the location.
   *
   * @param city the city to set
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * Returns the region of the location.
   *
   * @return the region
   */
  public String getRegion() {
    return region;
  }

  /**
   * Sets the region of the location.
   *
   * @param region the region to set
   */
  public void setRegion(String region) {
    this.region = region;
  }

  /**
   * Returns the latitude of the location.
   *
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * Sets the latitude of the location.
   *
   * @param latitude the latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  /**
   * Returns the longitude of the location.
   *
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * Sets the longitude of the location.
   *
   * @param longitude the longitude to set
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}