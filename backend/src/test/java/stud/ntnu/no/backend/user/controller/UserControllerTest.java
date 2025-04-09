package stud.ntnu.no.backend.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.service.UserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;
    
    @Mock
    private EntityManager entityManager;
    
    @Mock
    private Query query;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Given
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        
        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        
        List<UserDTO> users = Arrays.asList(user1, user2);
        
        when(userService.getAllUsers()).thenReturn(users);

        // When/Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andDo(document("users-get-all",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("User ID"),
                                fieldWithPath("[].username").description("Username"),
                                fieldWithPath("[].email").description("User email"),
                                fieldWithPath("[].firstName").description("User's first name"),
                                fieldWithPath("[].lastName").description("User's last name"),
                                fieldWithPath("[].active").description("Whether the user account is active"),
                                fieldWithPath("[].role").description("User's role in the system")
                        )
                ));
                
        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        Long userId = 1L;
        
        UserDTO user = new UserDTO();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        when(userService.getUserById(userId)).thenReturn(user);

        // When/Then
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andDo(document("user-get-by-id",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("User ID"),
                                fieldWithPath("username").description("Username"),
                                fieldWithPath("email").description("User email"),
                                fieldWithPath("firstName").description("User's first name"),
                                fieldWithPath("lastName").description("User's last name"),
                                fieldWithPath("active").description("Whether the user account is active"),
                                fieldWithPath("role").description("User's role in the system")
                        )
                ));
                
        verify(userService).getUserById(userId);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        Long userId = 1L;
        
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFirstName("John");
        updateUserDTO.setLastName("Doe");
        
        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(userId);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Doe");
        
        when(userService.updateUser(eq(userId), any(UpdateUserDTO.class))).thenReturn(updatedUser);

        // When/Then
        mockMvc.perform(put("/api/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andDo(document("user-update",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("firstName").description("User's first name"),
                                fieldWithPath("lastName").description("User's last name"),
                                fieldWithPath("username").description("Username").optional(),
                                fieldWithPath("email").description("User email").optional(),
                                fieldWithPath("active").description("Whether the user account is active").optional(),
                                fieldWithPath("role").description("User's role in the system").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").description("User ID"),
                                fieldWithPath("username").description("Username"),
                                fieldWithPath("email").description("User email"),
                                fieldWithPath("firstName").description("User's first name"),
                                fieldWithPath("lastName").description("User's last name"),
                                fieldWithPath("active").description("Whether the user account is active"),
                                fieldWithPath("role").description("User's role in the system")
                        )
                ));
                
        verify(userService).updateUser(eq(userId), any(UpdateUserDTO.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);
        
        // Mock EntityManager and Query
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // When/Then
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent())
                .andDo(document("user-delete",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint())
                ));
                
        verify(userService).deleteUser(userId);
        // Verify that createQuery was called at least once
        verify(entityManager, atLeastOnce()).createQuery(anyString());
    }
} 