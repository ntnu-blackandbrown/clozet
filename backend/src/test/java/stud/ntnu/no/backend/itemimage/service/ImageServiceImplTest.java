package stud.ntnu.no.backend.itemimage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.itemimage.repository.ItemImageRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    @Mock
    private ItemImageRepository itemImageRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ImageServiceImpl imageService;

    private Item item;
    private ItemImage image1;
    private ItemImage image2;
    private MultipartFile validImageFile;
    private MultipartFile emptyFile;
    private MultipartFile invalidTypeFile;

    @BeforeEach
    void setUp() {
        // Setup test data
        item = new Item();
        item.setId(1L);
        item.setTitle("Test Item");

        image1 = new ItemImage("/images/1/image1.jpg", true, 0);
        image1.setId(1L);
        image1.setItem(item);

        image2 = new ItemImage("/images/1/image2.jpg", false, 1);
        image2.setId(2L);
        image2.setItem(item);

        // Create test files
        validImageFile = new MockMultipartFile(
                "image.jpg",
                "image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        emptyFile = new MockMultipartFile(
                "empty.jpg",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        invalidTypeFile = new MockMultipartFile(
                "document.txt",
                "document.txt",
                "text/plain",
                "text content".getBytes()
        );
    }

    @Test
    void uploadImage_withValidFile_shouldReturnUrl() throws IOException {
        // Arrange
        String expectedUrl = "/images/1/uploaded-image.jpg";
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(fileStorageService.storeFile(any(MultipartFile.class), eq(1L))).thenReturn(expectedUrl);
        when(itemImageRepository.findByItemId(1L)).thenReturn(Arrays.asList());
        
        ArgumentCaptor<ItemImage> imageCaptor = ArgumentCaptor.forClass(ItemImage.class);
        when(itemImageRepository.save(imageCaptor.capture())).thenAnswer(i -> i.getArgument(0));

        // Act
        String result = imageService.uploadImage(validImageFile, 1L);

        // Assert
        assertEquals(expectedUrl, result);
        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class), eq(1L));
        verify(itemRepository, times(1)).findById(1L);
        verify(itemImageRepository, times(1)).findByItemId(1L);
        verify(itemImageRepository, times(1)).save(any(ItemImage.class));
        
        // Verify the saved image properties
        ItemImage savedImage = imageCaptor.getValue();
        assertEquals(expectedUrl, savedImage.getImageUrl());
        assertTrue(savedImage.isPrimary()); // Should be primary since it's the first image
        assertEquals(0, savedImage.getDisplayOrder());
        assertEquals(item, savedImage.getItem());
    }

    @Test
    void uploadImage_withExistingImages_shouldNotBePrimary() throws IOException {
        // Arrange
        String expectedUrl = "/images/1/uploaded-image.jpg";
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(fileStorageService.storeFile(any(MultipartFile.class), eq(1L))).thenReturn(expectedUrl);
        when(itemImageRepository.findByItemId(1L)).thenReturn(Arrays.asList(image1));
        
        ArgumentCaptor<ItemImage> imageCaptor = ArgumentCaptor.forClass(ItemImage.class);
        when(itemImageRepository.save(imageCaptor.capture())).thenAnswer(i -> i.getArgument(0));

        // Act
        String result = imageService.uploadImage(validImageFile, 1L);

        // Assert
        assertEquals(expectedUrl, result);
        
        // Verify the saved image properties
        ItemImage savedImage = imageCaptor.getValue();
        assertEquals(expectedUrl, savedImage.getImageUrl());
        assertFalse(savedImage.isPrimary()); // Should not be primary since there's an existing image
        assertEquals(1, savedImage.getDisplayOrder()); // Should be the 2nd image (index 1)
    }

    @Test
    void uploadImage_withEmptyFile_shouldThrowException() {
        // Since EmptyFileException is thrown during validation before any IOException can occur
        // we can simplify this test
        
        // Act & Assert
        assertThrows(EmptyFileException.class, () -> imageService.uploadImage(emptyFile, 1L));
        
        // No need to verify fileStorageService since the exception should be thrown before it's called
        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(itemImageRepository);
    }

    @Test
    void uploadImage_withInvalidFileType_shouldThrowException() {
        // Similar to the above test, InvalidFileTypeException is thrown during validation
        
        // Act & Assert
        assertThrows(InvalidFileTypeException.class, () -> imageService.uploadImage(invalidTypeFile, 1L));
        
        // No interactions should occur with these dependencies
        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(itemImageRepository);
    }

    @Test
    void uploadImage_withItemNotFound_shouldThrowException() throws IOException {
        // Arrange
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        // Make sure storeFile doesn't throw an exception
        when(fileStorageService.storeFile(any(MultipartFile.class), eq(99L))).thenReturn("/images/99/test.jpg");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> imageService.uploadImage(validImageFile, 99L));
        
        // Verify that storage was attempted but save was not
        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class), eq(99L));
        verify(itemRepository, times(1)).findById(99L);
        verify(itemImageRepository, never()).save(any());
    }

    @Test
    void uploadImage_withStorageFailure_shouldThrowException() throws IOException {
        // Arrange - we'll throw an IOException from the storage service
        when(fileStorageService.storeFile(any(MultipartFile.class), eq(1L)))
            .thenThrow(new IOException("Storage failure"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> imageService.uploadImage(validImageFile, 1L));
        
        // Verify storage was attempted but nothing else
        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class), eq(1L));
        verify(itemRepository, never()).findById(anyLong());
        verify(itemImageRepository, never()).save(any());
    }

    @Test
    void getImagesByItemId_shouldReturnImages() {
        // Arrange
        List<ItemImage> expectedImages = Arrays.asList(image1, image2);
        when(itemImageRepository.findByItemId(1L)).thenReturn(expectedImages);

        // Act
        List<ItemImage> result = imageService.getImagesByItemId(1L);

        // Assert
        assertEquals(expectedImages, result);
        assertEquals(2, result.size());
        verify(itemImageRepository, times(1)).findByItemId(1L);
    }

    @Test
    void getImagesByItemId_withNoImages_shouldReturnEmptyList() {
        // Arrange
        when(itemImageRepository.findByItemId(1L)).thenReturn(Arrays.asList());

        // Act
        List<ItemImage> result = imageService.getImagesByItemId(1L);

        // Assert
        assertTrue(result.isEmpty());
        verify(itemImageRepository, times(1)).findByItemId(1L);
    }

    @Test
    void deleteImage_shouldDeleteImage() {
        // Arrange
        doNothing().when(itemImageRepository).deleteById(1L);

        // Act
        imageService.deleteImage(1L);

        // Assert
        verify(itemImageRepository, times(1)).deleteById(1L);
    }
} 