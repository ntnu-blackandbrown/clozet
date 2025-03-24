package stud.ntnu.no.backend.User.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.User.DTOs.RegisterUserDTO;
import stud.ntnu.no.backend.User.DTOs.UserDTO;
import stud.ntnu.no.backend.User.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  private UserDTO userDTO;
  private RegisterUserDTO registerUserDTO;

  @BeforeEach
  public void setUp() {
    userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setEmail("test@example.com");
    userDTO.setFirstName("John");
    userDTO.setLastName("Doe");
    userDTO.setUsername("johndoe");
    userDTO.setActive(true);
    userDTO.setRole("USER");

    registerUserDTO = new RegisterUserDTO();
    registerUserDTO.setEmail("test@example.com");
    registerUserDTO.setFirstName("John");
    registerUserDTO.setLastName("Doe");
    registerUserDTO.setUsername("johndoe");
    registerUserDTO.setPassword("password");
    registerUserDTO.setRole("USER");
  }

  @Test
  @WithMockUser(username = "testuser", roles = {"USER"})
  public void testCreateUser() throws Exception {
    given(userService.createUser(any(RegisterUserDTO.class))).willReturn(userDTO);

    mockMvc.perform(post("/api/users")
            .contentType("application/json")
            .content("{\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"username\":\"johndoe\",\"password\":\"password\",\"role\":\"USER\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userDTO.getId()))
        .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
        .andExpect(jsonPath("$.firstName").value(userDTO.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(userDTO.getLastName()))
        .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
        .andExpect(jsonPath("$.active").value(userDTO.isActive()))
        .andExpect(jsonPath("$.role").value(userDTO.getRole()));
  }
}