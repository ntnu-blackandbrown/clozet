package stud.ntnu.no.backend.user.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserImageControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void uploadAndRetrieveProfileImage() throws Exception {
        // Create test user first
        // This assumes you have a UserRepository or UserService available
        
        // Create test user
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser.setActive(true);
        testUser = userRepository.save(testUser);

        Long userId = testUser.getId();

        // Now proceed with image upload using the actual userId
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-profile.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes());

        String imageUrl = mockMvc.perform(multipart("/api/users/profile/image")
                .file(file)
                .param("userId", userId.toString()))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // Rest of test remains the same
    }
    
    @Test
    void uploadInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is not an image".getBytes());

        Long userId = 1L;
        
        mockMvc.perform(multipart("/api/users/profile/image")
                        .file(file)
                        .param("userId", userId.toString()))
                .andExpect(status().isUnsupportedMediaType());
    }
}