package stud.ntnu.no.backend.itemimage.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UpdateItemImageRequestTest {

  @Test
  void testEmptyConstructor() {
    UpdateItemImageRequest request = new UpdateItemImageRequest();

    assertNull(request.getImageUrl());
    assertNull(request.getIsPrimary());
    assertNull(request.getDisplayOrder());
  }

  @Test
  void testGettersAndSetters() {
    UpdateItemImageRequest request = new UpdateItemImageRequest();

    String imageUrl = "https://example.com/images/updated.jpg";
    Boolean isPrimary = true;
    Integer displayOrder = 1;

    request.setImageUrl(imageUrl);
    request.setIsPrimary(isPrimary);
    request.setDisplayOrder(displayOrder);

    assertEquals(imageUrl, request.getImageUrl());
    assertEquals(isPrimary, request.getIsPrimary());
    assertEquals(displayOrder, request.getDisplayOrder());
  }
}
