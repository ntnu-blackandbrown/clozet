package stud.ntnu.no.backend.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a location entity.
 * <p>
 * This class is mapped to the "locations" table in the database and holds information about a
 * location, including its ID, city, region, latitude, and longitude.
 */
@Entity
@Table(name = "locations")
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String city;

  private double latitude;

  private double longitude;

  @Column(nullable = false)
  private String region;

  // Getters and setters

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
   * Returns the ID of the location.
   *
   * @return the ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the location.
   *
   * @param id the ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }


}