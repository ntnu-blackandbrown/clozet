package stud.ntnu.no.backend.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import stud.ntnu.no.backend.common.controller.MessageResponse;
import stud.ntnu.no.backend.user.dto.ChangePasswordDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private MeController meController;

    private static final String TEST_USERNAME = "testuser";
    private static final String CURRENT_PASSWORD = "current_password";
    private static final String NEW_PASSWORD = "new_password";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        
        // Default authentication setup for most tests
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        when(authentication.getPrincipal()).thenReturn(TEST_USERNAME);
    }

    @Test
    void getCurrentUser_whenUserAuthenticated_shouldReturnUserDTO() {
        // Arrange
        UserDTO expectedUserDTO = new UserDTO();
        expectedUserDTO.setUsername(TEST_USERNAME);
        
        when(userService.getUserByUsername(TEST_USERNAME)).thenReturn(expectedUserDTO);
        
        // Act
        ResponseEntity<?> response = meController.getCurrentUser();
        
        // Assert
        verify(userService).getUserByUsername(TEST_USERNAME);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDTO, response.getBody());
    }
    
    @Test
    void getCurrentUser_whenUserNotAuthenticated_shouldReturnUnauthorized() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);
        
        // Act
        ResponseEntity<?> response = meController.getCurrentUser();
        
        // Assert
        verify(userService, never()).getUserByUsername(anyString());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void getCurrentUser_whenUserIsAnonymous_shouldReturnUnauthorized() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        // Act
        ResponseEntity<?> response = meController.getCurrentUser();
        
        // Assert
        verify(userService, never()).getUserByUsername(anyString());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void getCurrentUser_whenUserNotFound_shouldReturnNotFound() {
        // Arrange
        when(userService.getUserByUsername(TEST_USERNAME)).thenThrow(
            new UserNotFoundException("Username " + TEST_USERNAME + " not found"));
        
        // Act
        ResponseEntity<?> response = meController.getCurrentUser();
        
        // Assert
        verify(userService).getUserByUsername(TEST_USERNAME);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void changePassword_whenUserAuthenticated_andPasswordValid_shouldReturnSuccess() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(CURRENT_PASSWORD);
        changePasswordDTO.setNewPassword(NEW_PASSWORD);
        
        // Act
        ResponseEntity<?> response = meController.changePassword(changePasswordDTO);
        
        // Assert
        verify(userService).changePassword(TEST_USERNAME, changePasswordDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Password changed successfully", messageResponse.getMessage());
    }

    @Test
    void changePassword_whenUserNotAuthenticated_shouldReturnUnauthorized() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(CURRENT_PASSWORD);
        changePasswordDTO.setNewPassword(NEW_PASSWORD);
        
        when(authentication.isAuthenticated()).thenReturn(false);
        
        // Act
        ResponseEntity<?> response = meController.changePassword(changePasswordDTO);
        
        // Assert
        verify(userService, never()).changePassword(anyString(), any(ChangePasswordDTO.class));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("Unauthorized", ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void changePassword_whenUserIsAnonymous_shouldReturnUnauthorized() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(CURRENT_PASSWORD);
        changePasswordDTO.setNewPassword(NEW_PASSWORD);
        
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        // Act
        ResponseEntity<?> response = meController.changePassword(changePasswordDTO);
        
        // Assert
        verify(userService, never()).changePassword(anyString(), any(ChangePasswordDTO.class));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("Unauthorized", ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void changePassword_whenUserNotFound_shouldReturnNotFound() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(CURRENT_PASSWORD);
        changePasswordDTO.setNewPassword(NEW_PASSWORD);
        
        doThrow(new UserNotFoundException("Username " + TEST_USERNAME + " not found"))
            .when(userService).changePassword(TEST_USERNAME, changePasswordDTO);
        
        // Act
        ResponseEntity<?> response = meController.changePassword(changePasswordDTO);
        
        // Assert
        verify(userService).changePassword(TEST_USERNAME, changePasswordDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("Username " + TEST_USERNAME + " not found", ((MessageResponse)response.getBody()).getMessage());
    }

    @Test
    void changePassword_whenCurrentPasswordIncorrect_shouldReturnBadRequest() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(CURRENT_PASSWORD);
        changePasswordDTO.setNewPassword(NEW_PASSWORD);
        
        doThrow(new BadCredentialsException("Current password is incorrect"))
            .when(userService).changePassword(TEST_USERNAME, changePasswordDTO);
        
        // Act
        ResponseEntity<?> response = meController.changePassword(changePasswordDTO);
        
        // Assert
        verify(userService).changePassword(TEST_USERNAME, changePasswordDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Current password is incorrect", messageResponse.getMessage());
    }

    @Test
    void changePassword_whenUnexpectedError_shouldReturnInternalServerError() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(CURRENT_PASSWORD);
        changePasswordDTO.setNewPassword(NEW_PASSWORD);
        
        doThrow(new RuntimeException("Unexpected error"))
            .when(userService).changePassword(TEST_USERNAME, changePasswordDTO);
        
        // Act
        ResponseEntity<?> response = meController.changePassword(changePasswordDTO);
        
        // Assert
        verify(userService).changePassword(TEST_USERNAME, changePasswordDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("An error occurred while changing the password"));
    }
    
    @Test
    void changePassword_withMissingCurrentPassword_shouldReturnBadRequest() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        // No current password set
        changePasswordDTO.setNewPassword(NEW_PASSWORD);
        
        // Act
        ResponseEntity<?> response = meController.changePassword(changePasswordDTO);
        
        // Assert
        verify(userService, never()).changePassword(anyString(), any(ChangePasswordDTO.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Current password is required", messageResponse.getMessage());
    }
    
    @Test
    void changePassword_withMissingNewPassword_shouldReturnBadRequest() {
        // Arrange
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(CURRENT_PASSWORD);
        // No new password set
        
        // Act
        ResponseEntity<?> response = meController.changePassword(changePasswordDTO);
        
        // Assert
        verify(userService, never()).changePassword(anyString(), any(ChangePasswordDTO.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("New password is required", messageResponse.getMessage());
    }
    
    @Test
    void changePassword_withNullRequestBody_shouldReturnBadRequest() {
        // Act
        ResponseEntity<?> response = meController.changePassword(null);
        
        // Assert
        verify(userService, never()).changePassword(anyString(), any(ChangePasswordDTO.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Password change request is invalid", messageResponse.getMessage());
    }
} 