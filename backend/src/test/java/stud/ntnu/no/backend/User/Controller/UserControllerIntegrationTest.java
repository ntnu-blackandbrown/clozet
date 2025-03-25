package stud.ntnu.no.backend.User.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.User.DTOs.RegisterUserDTO;
import stud.ntnu.no.backend.User.DTOs.UpdateUserDTO;
import stud.ntnu.no.backend.User.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for UserController using an actual database with test profile.
 * These tests rely on TestDatabaseInitializer to set up the test database state.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllUsers_returnsInitializedUsers() throws Exception {
        // Should return Admin and testuser from TestDatabaseInitializer
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("Admin"))
                .andExpect(jsonPath("$[1].username").value("testuser"));
    }
    
    @Test
    void getUserById_returnsCorrectUser() throws Exception {
        // Get Admin user (ID 1)
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
    
    @Test
    void createUser_addsUserToDatabase() throws Exception {
        // Create a new user
        RegisterUserDTO newUser = new RegisterUserDTO();
        newUser.setUsername("newuser");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");
        newUser.setRole("USER");
        
        // Perform create request
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.role").value("USER"));
        
        // Verify all users are now available
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].username").value("newuser"));
    }
    
    @Test
    void updateUser_modifiesExistingUser() throws Exception {
        // Update the testuser
        UpdateUserDTO updateUser = new UpdateUserDTO();
        updateUser.setUsername("updated_testuser");
        updateUser.setFirstName("Updated");
        updateUser.setLastName("User");
        updateUser.setEmail("updated@example.com");
        updateUser.setActive(true);
        updateUser.setRole("ADMIN");
        
        // ID 2 is the testuser
        mockMvc.perform(put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated_testuser"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
        
        // Verify the user was updated
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated_testuser"))
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }
    
    @Test
    void deleteUser_removesUserFromDatabase() throws Exception {
        // Create a user to delete
        RegisterUserDTO userToDelete = new RegisterUserDTO();
        userToDelete.setUsername("delete_me");
        userToDelete.setFirstName("Delete");
        userToDelete.setLastName("Me");
        userToDelete.setEmail("delete@example.com");
        userToDelete.setPassword("password");
        userToDelete.setRole("USER");
        
        // Create the user
        String responseContent = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userToDelete)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        // Extract ID of the created user from the response (assuming format includes "id":X)
        int idStartPos = responseContent.indexOf("\"id\":") + 5;
        int idEndPos = responseContent.indexOf(",", idStartPos);
        if (idEndPos == -1) {
            // In case id is the last field
            idEndPos = responseContent.indexOf("}", idStartPos);
        }
        String userId = responseContent.substring(idStartPos, idEndPos);
        
        // Delete the user
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());
        
        // Verify the user was deleted (should return 404)
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().is4xxClientError());
    }
} 