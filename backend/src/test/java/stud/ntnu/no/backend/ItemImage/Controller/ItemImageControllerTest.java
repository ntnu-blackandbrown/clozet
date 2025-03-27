package stud.ntnu.no.backend.ItemImage.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stud.ntnu.no.backend.ItemImage.DTOs.CreateItemImageDTO;
import stud.ntnu.no.backend.ItemImage.DTOs.ItemImageDTO;
import stud.ntnu.no.backend.ItemImage.Service.ItemImageService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemImageControllerTest {

    @Mock
    private ItemImageService itemImageService;

    @InjectMocks
    private ItemImageController itemImageController;

    private ItemImageDTO sampleItemImageDTO;
    private ItemImageDTO secondItemImageDTO;
    private CreateItemImageDTO createItemImageDTO;
    private List<ItemImageDTO> itemImageDTOList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        sampleItemImageDTO = new ItemImageDTO();
        sampleItemImageDTO.setId(1L);
        sampleItemImageDTO.setImageUrl("https://example.com/image1.jpg");
        sampleItemImageDTO.setAltText("Product front view");
        sampleItemImageDTO.setPosition(1);
        sampleItemImageDTO.setItemId(1L);

        secondItemImageDTO = new ItemImageDTO();
        secondItemImageDTO.setId(2L);
        secondItemImageDTO.setImageUrl("https://example.com/image2.jpg");
        secondItemImageDTO.setAltText("Product side view");
        secondItemImageDTO.setPosition(2);
        secondItemImageDTO.setItemId(1L);

        itemImageDTOList = Arrays.asList(sampleItemImageDTO, secondItemImageDTO);

        createItemImageDTO = new CreateItemImageDTO();
        createItemImageDTO.setImageUrl("https://example.com/image3.jpg");
        createItemImageDTO.setAltText("Product back view");
        createItemImageDTO.setPosition(3);
        createItemImageDTO.setItemId(1L);
    }

    @Test
    void getAllItemImages_ShouldReturnListOfItemImages() {
        when(itemImageService.getAllItemImages()).thenReturn(itemImageDTOList);

        ResponseEntity<List<ItemImageDTO>> response = itemImageController.getAllItemImages();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(itemImageService, times(1)).getAllItemImages();
    }

    @Test
    void getItemImagesByItemId_ShouldReturnListOfItemImages() {
        when(itemImageService.getItemImagesByItemId(1L)).thenReturn(itemImageDTOList);

        ResponseEntity<List<ItemImageDTO>> response = itemImageController.getItemImagesByItemId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(itemImageService, times(1)).getItemImagesByItemId(1L);
    }

    @Test
    void getItemImage_WithValidId_ShouldReturnItemImage() {
        when(itemImageService.getItemImage(1L)).thenReturn(sampleItemImageDTO);

        ResponseEntity<ItemImageDTO> response = itemImageController.getItemImage(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("https://example.com/image1.jpg", response.getBody().getImageUrl());
        verify(itemImageService, times(1)).getItemImage(1L);
    }

    @Test
    void createItemImage_WithValidData_ShouldReturnCreatedItemImage() {
        when(itemImageService.createItemImage(any(CreateItemImageDTO.class))).thenReturn(sampleItemImageDTO);

        ResponseEntity<ItemImageDTO> response = itemImageController.createItemImage(createItemImageDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(itemImageService, times(1)).createItemImage(any(CreateItemImageDTO.class));
    }

    @Test
    void updateItemImage_WithValidData_ShouldReturnUpdatedItemImage() {
        when(itemImageService.updateItemImage(eq(1L), any(CreateItemImageDTO.class))).thenReturn(sampleItemImageDTO);

        ResponseEntity<ItemImageDTO> response = itemImageController.updateItemImage(1L, createItemImageDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(itemImageService, times(1)).updateItemImage(eq(1L), any(CreateItemImageDTO.class));
    }

    @Test
    void deleteItemImage_ShouldReturnNoContent() {
        doNothing().when(itemImageService).deleteItemImage(1L);

        ResponseEntity<Void> response = itemImageController.deleteItemImage(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(itemImageService, times(1)).deleteItemImage(1L);
    }
}