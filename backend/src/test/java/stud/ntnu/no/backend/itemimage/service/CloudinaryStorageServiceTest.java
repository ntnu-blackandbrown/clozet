package stud.ntnu.no.backend.itemimage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CloudinaryStorageServiceTest {

  @Mock private Cloudinary cloudinary;

  @Mock private Uploader uploader;

  @InjectMocks private CloudinaryStorageService cloudinaryStorageService;

  private MultipartFile testFile;

  @BeforeEach
  void setUp() {
    // Setup mock uploader for cloudinary
    when(cloudinary.uploader()).thenReturn(uploader);

    // Create test file
    testFile =
        new MockMultipartFile(
            "test-file.jpg", "test-file.jpg", "image/jpeg", "test image content".getBytes());
  }

  @Test
  void storeFile_shouldUploadToCloudinaryAndReturnUrl() throws IOException {
    // Arrange
    Long itemId = 1L;
    String expectedUrl = "https://res.cloudinary.com/demo/image/upload/items/1/sample.jpg";

    Map<String, Object> uploadResult = new HashMap<>();
    uploadResult.put("secure_url", expectedUrl);
    uploadResult.put("public_id", "items/1/sample");

    when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

    // Act
    String result = cloudinaryStorageService.storeFile(testFile, itemId);

    // Assert
    assertEquals(expectedUrl, result);

    // Verify correct parameters were passed to Cloudinary
    verify(uploader, times(1))
        .upload(
            any(byte[].class),
            argThat(
                params -> {
                  boolean correctFolder = "items/1".equals(params.get("folder"));
                  boolean correctResourceType = "auto".equals(params.get("resource_type"));
                  return correctFolder && correctResourceType;
                }));
  }

  @Test
  void storeFile_withDifferentItemIds_shouldUseCorrectFolders() throws IOException {
    // Arrange
    Long itemId1 = 1L;
    Long itemId2 = 2L;

    String expectedUrl1 = "https://res.cloudinary.com/demo/image/upload/items/1/sample1.jpg";
    String expectedUrl2 = "https://res.cloudinary.com/demo/image/upload/items/2/sample2.jpg";

    Map<String, Object> uploadResult1 = new HashMap<>();
    uploadResult1.put("secure_url", expectedUrl1);

    Map<String, Object> uploadResult2 = new HashMap<>();
    uploadResult2.put("secure_url", expectedUrl2);

    when(uploader.upload(
            any(byte[].class), argThat(params -> "items/1".equals(params.get("folder")))))
        .thenReturn(uploadResult1);
    when(uploader.upload(
            any(byte[].class), argThat(params -> "items/2".equals(params.get("folder")))))
        .thenReturn(uploadResult2);

    // Act
    String result1 = cloudinaryStorageService.storeFile(testFile, itemId1);
    String result2 = cloudinaryStorageService.storeFile(testFile, itemId2);

    // Assert
    assertEquals(expectedUrl1, result1);
    assertEquals(expectedUrl2, result2);

    // Verify correct folder parameters were used
    verify(uploader, times(1))
        .upload(any(byte[].class), argThat(params -> "items/1".equals(params.get("folder"))));
    verify(uploader, times(1))
        .upload(any(byte[].class), argThat(params -> "items/2".equals(params.get("folder"))));
  }

  @Test
  void storeFile_whenUploadFails_shouldThrowException() throws IOException {
    // Arrange
    Long itemId = 1L;
    IOException expectedException = new IOException("Cloudinary upload failed");

    when(uploader.upload(any(byte[].class), anyMap())).thenThrow(expectedException);

    // Act & Assert
    IOException thrownException =
        assertThrows(
            IOException.class,
            () -> {
              cloudinaryStorageService.storeFile(testFile, itemId);
            });

    assertEquals(expectedException.getMessage(), thrownException.getMessage());
    verify(uploader, times(1)).upload(any(byte[].class), anyMap());
  }

  @Test
  void storeFile_whenResultMissingUrl_shouldReturnNull() throws IOException {
    // Arrange
    Long itemId = 1L;

    // Return a result without the secure_url field
    Map<String, Object> incompleteResult = new HashMap<>();
    incompleteResult.put("public_id", "items/1/sample");
    // No secure_url included

    when(uploader.upload(any(byte[].class), anyMap())).thenReturn(incompleteResult);

    // Act
    String result = cloudinaryStorageService.storeFile(testFile, itemId);

    // Assert
    assertNull(result, "Result should be null when secure_url is missing from the uploadResult");
    verify(uploader, times(1)).upload(any(byte[].class), anyMap());
  }
}
