package stud.ntnu.no.backend.itemimage.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CreateItemImageDTOTest {

  @Test
  void testEmptyConstructor() {
    CreateItemImageDTO dto = new CreateItemImageDTO();

    assertNull(dto.getItemId());
    assertNull(dto.getImageUrl());
    assertFalse(dto.isPrimary());
    assertEquals(0, dto.getDisplayOrder());
  }

  @Test
  void testGettersAndSetters() {
    CreateItemImageDTO dto = new CreateItemImageDTO();

    Long itemId = 1L;
    String imageUrl = "https://example.com/images/new.jpg";
    boolean isPrimary = true;
    int displayOrder = 0;

    dto.setItemId(itemId);
    dto.setImageUrl(imageUrl);
    dto.setIsPrimary(isPrimary);
    dto.setDisplayOrder(displayOrder);

    assertEquals(itemId, dto.getItemId());
    assertEquals(imageUrl, dto.getImageUrl());
    assertEquals(isPrimary, dto.isPrimary());
    assertEquals(displayOrder, dto.getDisplayOrder());
  }
}
