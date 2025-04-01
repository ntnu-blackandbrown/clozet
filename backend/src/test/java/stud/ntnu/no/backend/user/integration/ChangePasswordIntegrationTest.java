package stud.ntnu.no.backend.user.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.common.controller.MessageResponse;
import stud.ntnu.no.backend.user.dto.ChangePasswordDTO;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Use test profile 
public class ChangePasswordIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    void testChangePassword_withValidCredentials_shouldUpdatePassword() {
        // Arrange - Create a test user
        String username = "testuser" + System.currentTimeMillis();
        String email = "test" + System.currentTimeMillis() + "@example.com";
        String initialPassword = "initial_password";
        String newPassword = "new_password";
        
        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setUsername(username);
        registerUserDTO.setEmail(email);
        registerUserDTO.setPassword(initialPassword);
        
        // Create user and make it active (skip email verification)
        HttpEntity<RegisterUserDTO> registerRequest = new HttpEntity<>(registerUserDTO, createJsonHeaders());
        ResponseEntity<MessageResponse> registerResponse = restTemplate.postForEntity(
            "/api/auth/register", 
            registerRequest,
            MessageResponse.class
        );
        assertEquals(200, registerResponse.getStatusCodeValue());
        
        // Activate user manually (bypass email verification)
        User user = userRepository.findByUsername(username).orElse(null);
        assertNotNull(user);
        user.setActive(true);
        userRepository.save(user);
        
        // Step 1: Login with initial password
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(initialPassword);
        
        HttpEntity<LoginDTO> loginRequest = new HttpEntity<>(loginDTO, createJsonHeaders());
        ResponseEntity<Object> loginResponse = restTemplate.postForEntity(
            "/api/auth/login", 
            loginRequest,
            Object.class
        );
        assertEquals(200, loginResponse.getStatusCodeValue());
        
        // Extract cookies from login response
        List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies, "Cookie headers should not be null");
        assertFalse(cookies.isEmpty(), "Cookie headers should not be empty");
        
        // Step 2: Change password with valid current password
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(initialPassword);
        changePasswordDTO.setNewPassword(newPassword);
        
        HttpHeaders headersWithCookie = createJsonHeaders();
        for (String cookie : cookies) {
            // Extract just the name=value part of the cookie
            String[] cookieParts = cookie.split(";")[0].trim().split("=", 2);
            if (cookieParts.length == 2) {
                headersWithCookie.add(HttpHeaders.COOKIE, cookieParts[0] + "=" + cookieParts[1]);
            }
        }
        
        HttpEntity<ChangePasswordDTO> changePasswordRequest = new HttpEntity<>(changePasswordDTO, headersWithCookie);
        ResponseEntity<Object> changePasswordResponse = restTemplate.postForEntity(
            "/api/me/change-password", 
            changePasswordRequest,
            Object.class
        );
        
        // Now the implementation should return 200 OK
        assertEquals(200, changePasswordResponse.getStatusCodeValue());
        
        // Also verify the password was updated in the database
        User updatedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(updatedUser);
        // Check if the password hash has changed
        assertNotEquals(user.getPasswordHash(), updatedUser.getPasswordHash(), 
            "Password hash should have changed");
        
        // Explicitly try to verify with password encoder if the new password works
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPasswordHash()),
            "Password encoder should verify new password against updated hash");
        
        assertFalse(passwordEncoder.matches(initialPassword, updatedUser.getPasswordHash()),
            "Password encoder should NOT verify old password against updated hash");
    }
    
    @Test
    void testChangePassword_withInvalidCurrentPassword_shouldReturnError() {
        // Arrange - Create a test user
        String username = "testuser" + System.currentTimeMillis();
        String email = "test" + System.currentTimeMillis() + "@example.com";
        String initialPassword = "initial_password";
        String wrongPassword = "wrong_password";
        String newPassword = "new_password";
        
        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setUsername(username);
        registerUserDTO.setEmail(email);
        registerUserDTO.setPassword(initialPassword);
        
        // Create user and make it active (skip email verification)
        HttpEntity<RegisterUserDTO> registerRequest = new HttpEntity<>(registerUserDTO, createJsonHeaders());
        ResponseEntity<MessageResponse> registerResponse = restTemplate.postForEntity(
            "/api/auth/register", 
            registerRequest,
            MessageResponse.class
        );
        assertEquals(200, registerResponse.getStatusCodeValue());
        
        // Activate user manually (bypass email verification)
        User user = userRepository.findByUsername(username).orElse(null);
        assertNotNull(user);
        user.setActive(true);
        userRepository.save(user);
        
        // Step 1: Login with initial password
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(initialPassword);
        
        HttpEntity<LoginDTO> loginRequest = new HttpEntity<>(loginDTO, createJsonHeaders());
        ResponseEntity<Object> loginResponse = restTemplate.postForEntity(
            "/api/auth/login", 
            loginRequest,
            Object.class
        );
        assertEquals(200, loginResponse.getStatusCodeValue());
        
        // Extract cookies from login response
        List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies, "Cookie headers should not be null");
        assertFalse(cookies.isEmpty(), "Cookie headers should not be empty");
        
        // Step 2: Attempt to change password with wrong current password
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword(wrongPassword);
        changePasswordDTO.setNewPassword(newPassword);
        
        HttpHeaders headersWithCookie = createJsonHeaders();
        for (String cookie : cookies) {
            // Extract just the name=value part of the cookie
            String[] cookieParts = cookie.split(";")[0].trim().split("=", 2);
            if (cookieParts.length == 2) {
                headersWithCookie.add(HttpHeaders.COOKIE, cookieParts[0] + "=" + cookieParts[1]);
            }
        }
        
        HttpEntity<ChangePasswordDTO> changePasswordRequest = new HttpEntity<>(changePasswordDTO, headersWithCookie);
        ResponseEntity<MessageResponse> changePasswordResponse = restTemplate.postForEntity(
            "/api/me/change-password", 
            changePasswordRequest,
            MessageResponse.class
        );
        
        // Now it should return 400 Bad Request
        assertEquals(400, changePasswordResponse.getStatusCodeValue());
        assertEquals("Current password is incorrect", changePasswordResponse.getBody().getMessage());
        
        // Verify the password was not changed - we can still login with initial password
        LoginDTO verifyLoginDTO = new LoginDTO();
        verifyLoginDTO.setUsername(username);
        verifyLoginDTO.setPassword(initialPassword);
        
        HttpEntity<LoginDTO> verifyLoginRequest = new HttpEntity<>(verifyLoginDTO, createJsonHeaders());
        ResponseEntity<Object> verifyLoginResponse = restTemplate.postForEntity(
            "/api/auth/login", 
            verifyLoginRequest,
            Object.class
        );
        assertEquals(200, verifyLoginResponse.getStatusCodeValue());
        
        // Verify with encoder that password hasn't changed
        User unchangedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(unchangedUser);
        assertTrue(passwordEncoder.matches(initialPassword, unchangedUser.getPasswordHash()),
            "Original password should still work with the hash in database");
    }
    
    @Test
    void testChangePassword_withoutAuthentication_shouldReturnUnauthorized() {
        // Attempt to change password without authentication
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("any_password");
        changePasswordDTO.setNewPassword("new_password");
        
        HttpEntity<ChangePasswordDTO> changePasswordRequest = new HttpEntity<>(changePasswordDTO, createJsonHeaders());
        ResponseEntity<String> changePasswordResponse = restTemplate.postForEntity(
            "/api/me/change-password", 
            changePasswordRequest,
            String.class
        );
        
        assertEquals(401, changePasswordResponse.getStatusCodeValue());
    }
    
    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
} 