package stud.ntnu.no.backend.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.user.service.UserProfileService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserImageControllerTest {

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private UserImageController userImageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadProfileImage_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        String expectedUrl = "http://example.com/user/profile.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file", "profile.jpg", "image/jpeg", "test image content".getBytes());
        
        when(userProfileService.uploadProfilePicture(any(), eq(userId))).thenReturn(expectedUrl);
        
        // Act
        ResponseEntity<?> response = userImageController.uploadProfileImage(file, userId);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
        verify(userProfileService).uploadProfilePicture(file, userId);
    }

    @Test
    void uploadProfileImage_EmptyFile() throws Exception {
        // Arrange
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);
        
        when(userProfileService.uploadProfilePicture(any(), eq(userId)))
                .thenThrow(new EmptyFileException("Failed to store empty file"));
        
        // Act
        ResponseEntity<?> response = userImageController.uploadProfileImage(file, userId);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userProfileService).uploadProfilePicture(file, userId);
    }

    @Test
    void uploadProfileImage_InvalidFileType() throws Exception {
        // Arrange
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file", "doc.txt", "text/plain", "text content".getBytes());
        
        when(userProfileService.uploadProfilePicture(any(), eq(userId)))
                .thenThrow(new InvalidFileTypeException("File type not supported"));
        
        // Act
        ResponseEntity<?> response = userImageController.uploadProfileImage(file, userId);
        
        // Assert
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userProfileService).uploadProfilePicture(file, userId);
    }

    @Test
    void getProfileImage_Success() {
        // Arrange
        Long userId = 1L;
        String expectedUrl = "http://example.com/user/profile.jpg";
        when(userProfileService.getProfilePictureUrl(userId)).thenReturn(expectedUrl);
        
        // Act
        ResponseEntity<?> response = userImageController.getProfileImage(userId);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
    }

    @Test
    void getProfileImage_NotFound() {
        // Arrange
        Long userId = 1L;
        when(userProfileService.getProfilePictureUrl(userId)).thenReturn(null);
        
        // Act
        ResponseEntity<?> response = userImageController.getProfileImage(userId);
        
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteProfileImage_Success() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userProfileService).deleteProfilePicture(userId);
        
        // Act
        ResponseEntity<?> response = userImageController.deleteProfileImage(userId);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userProfileService).deleteProfilePicture(userId);
    }
}