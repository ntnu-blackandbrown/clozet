package stud.ntnu.no.backend.itemimage.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.item.entity.Item;

class ItemImageTest {

  @Test
  void testDefaultConstructor() {
    ItemImage itemImage = new ItemImage();

    assertNull(itemImage.getId());
    assertNull(itemImage.getItem());
    assertNull(itemImage.getImageUrl());
    assertFalse(itemImage.isPrimary());
    assertEquals(0, itemImage.getDisplayOrder());
  }

  @Test
  void testParameterizedConstructor() {
    String imageUrl = "http://example.com/image.jpg";
    boolean isPrimary = true;
    int displayOrder = 1;

    ItemImage itemImage = new ItemImage(imageUrl, isPrimary, displayOrder);

    assertNull(itemImage.getId());
    assertNull(itemImage.getItem());
    assertEquals(imageUrl, itemImage.getImageUrl());
    assertTrue(itemImage.isPrimary());
    assertEquals(displayOrder, itemImage.getDisplayOrder());
  }

  @Test
  void testGettersAndSetters() {
    ItemImage itemImage = new ItemImage();

    Long id = 1L;
    Item item = new Item();
    String imageUrl = "http://example.com/image.jpg";
    boolean isPrimary = true;
    int displayOrder = 1;

    itemImage.setId(id);
    itemImage.setItem(item);
    itemImage.setImageUrl(imageUrl);
    itemImage.setPrimary(isPrimary);
    itemImage.setDisplayOrder(displayOrder);

    assertEquals(id, itemImage.getId());
    assertEquals(item, itemImage.getItem());
    assertEquals(imageUrl, itemImage.getImageUrl());
    assertTrue(itemImage.isPrimary());
    assertEquals(displayOrder, itemImage.getDisplayOrder());
  }

  @Test
  void testItemRelationship() {
    ItemImage itemImage = new ItemImage();
    Item item = new Item();
    item.setId(1L);
    item.setTitle("Test Item");

    itemImage.setItem(item);

    assertEquals(item, itemImage.getItem());
    assertEquals(1L, itemImage.getItem().getId());
    assertEquals("Test Item", itemImage.getItem().getTitle());
  }

  @Test
  void testTogglePrimary() {
    ItemImage itemImage = new ItemImage();
    assertFalse(itemImage.isPrimary());

    itemImage.setPrimary(true);
    assertTrue(itemImage.isPrimary());

    itemImage.setPrimary(false);
    assertFalse(itemImage.isPrimary());
  }
}
