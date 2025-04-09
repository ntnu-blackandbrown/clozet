package stud.ntnu.no.backend.item.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.itemimage.dto.CreateItemImageDTO;

class CreateItemDTOTest {

  @Test
  void testEmptyConstructor() {
    CreateItemDTO dto = new CreateItemDTO();

    assertNull(dto.getTitle());
    assertNull(dto.getShortDescription());
    assertNull(dto.getLongDescription());
    assertEquals(0.0, dto.getPrice());
    assertNull(dto.getCategoryId());
    assertNull(dto.getLocationId());
    assertNull(dto.getShippingOptionId());
    assertEquals(0.0, dto.getLatitude());
    assertEquals(0.0, dto.getLongitude());
    assertNull(dto.getCondition());
    assertNull(dto.getSize());
    assertNull(dto.getBrand());
    assertNull(dto.getColor());
    assertFalse(dto.isVippsPaymentEnabled());
    assertNull(dto.getImages());
  }

  @Test
  void testParameterizedConstructor() {
    String title = "Test Item";
    String shortDescription = "Short description";
    String longDescription = "Long description";
    double price = 100.50;
    Long categoryId = 1L;
    Long locationId = 2L;
    Long shippingOptionId = 3L;
    double latitude = 63.4305;
    double longitude = 10.3951;
    String condition = "New";
    String size = "Medium";
    String brand = "Test Brand";
    String color = "Red";
    boolean isVippsPaymentEnabled = true;
    List<CreateItemImageDTO> images = new ArrayList<>();

    CreateItemDTO dto =
        new CreateItemDTO(
            title,
            shortDescription,
            longDescription,
            price,
            categoryId,
            locationId,
            shippingOptionId,
            latitude,
            longitude,
            condition,
            size,
            brand,
            color,
            isVippsPaymentEnabled,
            images);

    assertEquals(title, dto.getTitle());
    assertEquals(shortDescription, dto.getShortDescription());
    assertEquals(longDescription, dto.getLongDescription());
    assertEquals(price, dto.getPrice());
    assertEquals(categoryId, dto.getCategoryId());
    assertEquals(locationId, dto.getLocationId());
    assertEquals(shippingOptionId, dto.getShippingOptionId());
    assertEquals(latitude, dto.getLatitude());
    assertEquals(longitude, dto.getLongitude());
    assertEquals(condition, dto.getCondition());
    assertEquals(size, dto.getSize());
    assertEquals(brand, dto.getBrand());
    assertEquals(color, dto.getColor());
    assertEquals(isVippsPaymentEnabled, dto.isVippsPaymentEnabled());
    assertEquals(images, dto.getImages());
  }

  @Test
  void testGettersAndSetters() {
    CreateItemDTO dto = new CreateItemDTO();

    String title = "Test Item";
    String shortDescription = "Short description";
    String longDescription = "Long description";
    double price = 100.50;
    Long categoryId = 1L;
    Long locationId = 2L;
    Long shippingOptionId = 3L;
    double latitude = 63.4305;
    double longitude = 10.3951;
    String condition = "New";
    String size = "Medium";
    String brand = "Test Brand";
    String color = "Red";
    boolean isVippsPaymentEnabled = true;
    List<CreateItemImageDTO> images = new ArrayList<>();

    dto.setTitle(title);
    dto.setShortDescription(shortDescription);
    dto.setLongDescription(longDescription);
    dto.setPrice(price);
    dto.setCategoryId(categoryId);
    dto.setLocationId(locationId);
    dto.setShippingOptionId(shippingOptionId);
    dto.setLatitude(latitude);
    dto.setLongitude(longitude);
    dto.setCondition(condition);
    dto.setSize(size);
    dto.setBrand(brand);
    dto.setColor(color);
    dto.setVippsPaymentEnabled(isVippsPaymentEnabled);
    dto.setImages(images);

    assertEquals(title, dto.getTitle());
    assertEquals(shortDescription, dto.getShortDescription());
    assertEquals(longDescription, dto.getLongDescription());
    assertEquals(price, dto.getPrice());
    assertEquals(categoryId, dto.getCategoryId());
    assertEquals(locationId, dto.getLocationId());
    assertEquals(shippingOptionId, dto.getShippingOptionId());
    assertEquals(latitude, dto.getLatitude());
    assertEquals(longitude, dto.getLongitude());
    assertEquals(condition, dto.getCondition());
    assertEquals(size, dto.getSize());
    assertEquals(brand, dto.getBrand());
    assertEquals(color, dto.getColor());
    assertEquals(isVippsPaymentEnabled, dto.isVippsPaymentEnabled());
    assertEquals(images, dto.getImages());
  }
}
