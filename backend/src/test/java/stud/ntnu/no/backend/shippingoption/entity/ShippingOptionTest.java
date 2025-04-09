package stud.ntnu.no.backend.shippingoption.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.item.entity.Item;

class ShippingOptionTest {

  @Test
  void testDefaultConstructor() {
    ShippingOption shippingOption = new ShippingOption();

    assertNull(shippingOption.getId());
    assertNull(shippingOption.getName());
    assertNull(shippingOption.getDescription());
    assertEquals(0, shippingOption.getEstimatedDays());
    assertEquals(0.0, shippingOption.getPrice());
    assertFalse(shippingOption.isTracked());
    assertNull(shippingOption.getItems());
  }

  @Test
  void testSettersAndGetters() {
    ShippingOption shippingOption = new ShippingOption();

    // Set up test data
    Long id = 1L;
    String name = "Express Shipping";
    String description = "Fast delivery within 2 days";
    int estimatedDays = 2;
    double price = 9.99;
    boolean isTracked = true;
    List<Item> items = new ArrayList<>();
    Item item = new Item();
    items.add(item);

    // Set the values
    shippingOption.setId(id);
    shippingOption.setName(name);
    shippingOption.setDescription(description);
    shippingOption.setEstimatedDays(estimatedDays);
    shippingOption.setPrice(price);
    shippingOption.setTracked(isTracked);
    shippingOption.setItems(items);

    // Assert the values
    assertEquals(id, shippingOption.getId());
    assertEquals(name, shippingOption.getName());
    assertEquals(description, shippingOption.getDescription());
    assertEquals(estimatedDays, shippingOption.getEstimatedDays());
    assertEquals(price, shippingOption.getPrice());
    assertTrue(shippingOption.isTracked());
    assertEquals(items, shippingOption.getItems());
    assertEquals(1, shippingOption.getItems().size());
  }

  @Test
  void testItemsRelationship() {
    ShippingOption shippingOption = new ShippingOption();

    // Create and add items
    List<Item> items = new ArrayList<>();
    Item item1 = new Item();
    Item item2 = new Item();
    items.add(item1);
    items.add(item2);

    shippingOption.setItems(items);

    // Assert the items
    assertEquals(2, shippingOption.getItems().size());
    assertTrue(shippingOption.getItems().contains(item1));
    assertTrue(shippingOption.getItems().contains(item2));

    // Test removing an item
    items.remove(item1);
    shippingOption.setItems(items);

    assertEquals(1, shippingOption.getItems().size());
    assertFalse(shippingOption.getItems().contains(item1));
    assertTrue(shippingOption.getItems().contains(item2));
  }
}
