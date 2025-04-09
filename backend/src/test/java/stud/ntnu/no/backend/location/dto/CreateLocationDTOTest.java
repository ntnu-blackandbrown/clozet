package stud.ntnu.no.backend.location.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CreateLocationDTOTest {

  @Test
  void testGettersAndSetters() {
    CreateLocationDTO dto = new CreateLocationDTO();

    String city = "Oslo";
    String region = "Oslo";
    double latitude = 59.9139;
    double longitude = 10.7522;

    dto.setCity(city);
    dto.setRegion(region);
    dto.setLatitude(latitude);
    dto.setLongitude(longitude);

    assertEquals(city, dto.getCity());
    assertEquals(region, dto.getRegion());
    assertEquals(latitude, dto.getLatitude());
    assertEquals(longitude, dto.getLongitude());
  }

  @Test
  void testDefaultValues() {
    CreateLocationDTO dto = new CreateLocationDTO();

    assertNull(dto.getCity());
    assertNull(dto.getRegion());
    assertEquals(0.0, dto.getLatitude());
    assertEquals(0.0, dto.getLongitude());
  }
}
