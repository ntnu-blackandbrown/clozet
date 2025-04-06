package stud.ntnu.no.backend.location.dto;

/**
 * Represents a Data Transfer Object for a location.
 * <p>
 * This class holds information about a location, including its ID, city,
 * region, latitude, longitude, and a combined name for display purposes.
 */
public class LocationDTO {
    private Long id;
    private String city;
    private String region;
    private double latitude;
    private double longitude;
    private String name; // Combined city and region for display

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

    /**
     * Returns the combined name of the location for display.
     *
     * @return the combined name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the combined name of the location for display.
     *
     * @param name the combined name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}