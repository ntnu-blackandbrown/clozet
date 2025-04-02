package stud.ntnu.no.backend.image.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.service.CloudinaryStorageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

class CloudinaryStorageServiceTest {

    @Mock
    private Cloudinary cloudinary;
    
    @Mock
    private Uploader uploader;
    
    private CloudinaryStorageService service;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
        service = new CloudinaryStorageService(cloudinary);
    }
    
    @Test
    void storeFile_shouldUploadToCloudinary() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
            "test.jpg", "test.jpg", "image/jpeg", "test content".getBytes()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("secure_url", "https://res.cloudinary.com/demo/image/upload/items/1/sample.jpg");
        
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(result);
        
        // Act
        String url = service.storeFile(file, 1L);
        
        // Assert
        assertEquals("https://res.cloudinary.com/demo/image/upload/items/1/sample.jpg", url);
    }
}