package stud.ntnu.no.backend.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.itemimage.service.FileStorageService;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserProfileServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private User user;
    private final Long userId = 1L;
    private final String imageUrl = "https://example.com/images/profile.jpg";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void uploadProfilePicture_ValidFile_ReturnsUrl() throws Exception {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(fileStorageService.storeFile(eq(multipartFile), eq(userId))).thenReturn(imageUrl);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        String result = userProfileService.uploadProfilePicture(multipartFile, userId);

        // Assert
        assertEquals(imageUrl, result);
        assertEquals(imageUrl, user.getProfilePictureUrl());
        verify(userRepository, times(1)).findById(userId);
        verify(fileStorageService, times(1)).storeFile(eq(multipartFile), eq(userId));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void uploadProfilePicture_EmptyFile_ThrowsException() throws IOException {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(true);

        // Act & Assert
        assertThrows(EmptyFileException.class, () -> {
            userProfileService.uploadProfilePicture(multipartFile, userId);
        });
        verify(fileStorageService, never()).storeFile(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void uploadProfilePicture_InvalidFileType_ThrowsException() throws IOException {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        // Act & Assert
        assertThrows(InvalidFileTypeException.class, () -> {
            userProfileService.uploadProfilePicture(multipartFile, userId);
        });
        verify(fileStorageService, never()).storeFile(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void uploadProfilePicture_UserNotFound_ThrowsException() throws IOException {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userProfileService.uploadProfilePicture(multipartFile, userId);
        });
        verify(fileStorageService, never()).storeFile(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteProfilePicture_UserExists_DeletesPicture() {
        // Arrange
        user.setProfilePictureUrl(imageUrl);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userProfileService.deleteProfilePicture(userId);

        // Assert
        assertNull(user.getProfilePictureUrl());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteProfilePicture_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userProfileService.deleteProfilePicture(userId);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getProfilePictureUrl_UserWithPicture_ReturnsUrl() {
        // Arrange
        user.setProfilePictureUrl(imageUrl);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String result = userProfileService.getProfilePictureUrl(userId);

        // Assert
        assertEquals(imageUrl, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getProfilePictureUrl_UserWithoutPicture_ReturnsNull() {
        // Arrange
        user.setProfilePictureUrl(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        String result = userProfileService.getProfilePictureUrl(userId);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getProfilePictureUrl_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userProfileService.getProfilePictureUrl(userId);
        });
    }
} 