package stud.ntnu.no.backend.itemimage.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ItemImageDTOTest {

  @Test
  void testConstructor() {
    Long id = 1L;
    Long itemId = 2L;
    String imageUrl = "https://example.com/images/test.jpg";
    boolean isPrimary = true;
    int displayOrder = 0;

    ItemImageDTO dto = new ItemImageDTO(id, itemId, imageUrl, isPrimary, displayOrder);

    assertEquals(id, dto.getId());
    assertEquals(itemId, dto.getItemId());
    assertEquals(imageUrl, dto.getImageUrl());
    assertEquals(isPrimary, dto.isPrimary());
    assertEquals(displayOrder, dto.getDisplayOrder());
  }

  @Test
  void testGettersAndSetters() {
    ItemImageDTO dto = new ItemImageDTO(null, null, null, false, 0);

    Long id = 1L;
    Long itemId = 2L;
    String imageUrl = "https://example.com/images/test.jpg";
    boolean isPrimary = true;
    int displayOrder = 0;

    dto.setId(id);
    dto.setItemId(itemId);
    dto.setImageUrl(imageUrl);
    dto.setPrimary(isPrimary);
    dto.setDisplayOrder(displayOrder);

    assertEquals(id, dto.getId());
    assertEquals(itemId, dto.getItemId());
    assertEquals(imageUrl, dto.getImageUrl());
    assertEquals(isPrimary, dto.isPrimary());
    assertEquals(displayOrder, dto.getDisplayOrder());
  }
}
