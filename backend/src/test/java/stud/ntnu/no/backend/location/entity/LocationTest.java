package stud.ntnu.no.backend.location.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LocationTest {

  @Test
  void testDefaultConstructor() {
    Location location = new Location();

    assertNull(location.getId());
    assertNull(location.getCity());
    assertNull(location.getRegion());
    assertEquals(0.0, location.getLatitude());
    assertEquals(0.0, location.getLongitude());
  }

  @Test
  void testGettersAndSetters() {
    Location location = new Location();

    Long id = 1L;
    String city = "Oslo";
    String region = "Oslo";
    double latitude = 59.9139;
    double longitude = 10.7522;

    location.setId(id);
    location.setCity(city);
    location.setRegion(region);
    location.setLatitude(latitude);
    location.setLongitude(longitude);

    assertEquals(id, location.getId());
    assertEquals(city, location.getCity());
    assertEquals(region, location.getRegion());
    assertEquals(latitude, location.getLatitude());
    assertEquals(longitude, location.getLongitude());
  }

  @Test
  void testCoordinatesRange() {
    Location location = new Location();

    // Test valid coordinates
    location.setLatitude(90.0);
    location.setLongitude(180.0);
    assertEquals(90.0, location.getLatitude());
    assertEquals(180.0, location.getLongitude());

    location.setLatitude(-90.0);
    location.setLongitude(-180.0);
    assertEquals(-90.0, location.getLatitude());
    assertEquals(-180.0, location.getLongitude());

    // Test typical coordinates for a real location
    location.setLatitude(59.9139);
    location.setLongitude(10.7522);
    assertEquals(59.9139, location.getLatitude());
    assertEquals(10.7522, location.getLongitude());
  }
}
