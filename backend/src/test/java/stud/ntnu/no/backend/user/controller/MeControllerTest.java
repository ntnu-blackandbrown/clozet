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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.no.backend.user.dto.ChangePasswordDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.service.UserService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
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
class MeControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MeController meController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        // Setup authentication with mock authentication
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        mockMvc = MockMvcBuilders.standaloneSetup(meController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void getCurrentUser_WithAuthenticatedUser_ShouldReturnUserDTO() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        
        when(userService.getUserByUsername("testuser")).thenReturn(userDTO);

        // When/Then
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andDo(document("me-get-current-user",
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
                
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    void getCurrentUser_WithUnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When/Then
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andDo(document("me-get-unauthorized",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("Error message")
                        )
                ));
                
        verify(userService, never()).getUserByUsername(anyString());
    }

    @Test
    void getCurrentUser_WithUserNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.getUserByUsername("testuser")).thenThrow(new UserNotFoundException("User not found"));

        // When/Then
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andDo(document("me-get-not-found",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("Error message")
                        )
                ));
                
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    void changePassword_WithValidCredentials_ShouldReturnSuccess() throws Exception {
        // Given
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("oldPassword");
        changePasswordDTO.setNewPassword("newPassword");
        
        doNothing().when(userService).changePassword(eq("testuser"), any(ChangePasswordDTO.class));

        // When/Then
        mockMvc.perform(post("/api/me/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Password changed successfully")))
                .andDo(document("me-change-password",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("currentPassword").description("Current password"),
                                fieldWithPath("newPassword").description("New password")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Success message")
                        )
                ));
                
        verify(userService).changePassword(eq("testuser"), any(ChangePasswordDTO.class));
    }

    @Test
    void changePassword_WithInvalidCredentials_ShouldReturnBadRequest() throws Exception {
        // Given
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("wrongPassword");
        changePasswordDTO.setNewPassword("newPassword");
        
        doThrow(new BadCredentialsException("Invalid current password"))
                .when(userService).changePassword(eq("testuser"), any(ChangePasswordDTO.class));

        // When/Then
        mockMvc.perform(post("/api/me/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid current password"))
                .andDo(document("me-change-password-invalid",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("currentPassword").description("Incorrect current password"),
                                fieldWithPath("newPassword").description("New password")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Error message")
                        )
                ));
                
        verify(userService).changePassword(eq("testuser"), any(ChangePasswordDTO.class));
    }

    @Test
    void changePassword_WithMissingFields_ShouldReturnBadRequest() throws Exception {
        // Given
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        // Missing currentPassword field

        // When/Then
        mockMvc.perform(post("/api/me/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Current password is required")))
                .andDo(document("me-change-password-missing-fields",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("Error message")
                        )
                ));
                
        verify(userService, never()).changePassword(anyString(), any(ChangePasswordDTO.class));
    }
} 