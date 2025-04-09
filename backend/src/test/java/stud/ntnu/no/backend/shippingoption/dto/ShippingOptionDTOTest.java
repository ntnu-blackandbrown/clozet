package stud.ntnu.no.backend.shippingoption.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ShippingOptionDTOTest {

  @Test
  void testGettersAndSetters() {
    ShippingOptionDTO dto = new ShippingOptionDTO();

    Long id = 1L;
    String name = "Express Shipping";
    String description = "Next day delivery";
    int estimatedDays = 1;
    double price = 99.99;
    boolean isTracked = true;

    dto.setId(id);
    dto.setName(name);
    dto.setDescription(description);
    dto.setEstimatedDays(estimatedDays);
    dto.setPrice(price);
    dto.setTracked(isTracked);

    assertEquals(id, dto.getId());
    assertEquals(name, dto.getName());
    assertEquals(description, dto.getDescription());
    assertEquals(estimatedDays, dto.getEstimatedDays());
    assertEquals(price, dto.getPrice(), 0.001);
    assertEquals(isTracked, dto.isTracked());
  }

  @Test
  void testDefaultValues() {
    ShippingOptionDTO dto = new ShippingOptionDTO();

    assertNull(dto.getId());
    assertNull(dto.getName());
    assertNull(dto.getDescription());
    assertEquals(0, dto.getEstimatedDays());
    assertEquals(0.0, dto.getPrice(), 0.001);
    assertFalse(dto.isTracked());
  }
}
