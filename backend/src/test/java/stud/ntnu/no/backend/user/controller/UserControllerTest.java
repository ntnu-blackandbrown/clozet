package stud.ntnu.no.backend.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.StatusUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.service.UserService;
import stud.ntnu.no.backend.config.SecurityConfig;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	private UserDTO testUser;
	private StatusUserDTO testStatusUser;

	@BeforeEach
	void setUp() {
		// Set up test user
		testUser = new UserDTO();
		testUser.setId(1L);
		testUser.setUsername("testuser");
		testUser.setFirstName("Test");
		testUser.setLastName("User");
		testUser.setEmail("test@example.com");
		testUser.setActive(true);
		testUser.setRole("USER");

		// Set up test status user
		testStatusUser = new StatusUserDTO();
		testStatusUser.setId(1L);
		testStatusUser.setUsername("testuser");
		testStatusUser.setEmail("test@example.com");
		testStatusUser.setActive(true);
	}

	@Test
	void getAllUsers_returnsList() throws Exception {
		// Arrange
		List<StatusUserDTO> users = Arrays.asList(testStatusUser);
		given(userService.getAllUsers()).willReturn(users);

		// Act & Assert
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].username").value("testuser"))
				.andExpect(jsonPath("$[0].email").value("test@example.com"))
				.andExpect(jsonPath("$[0].active").value(true));
	}

	@Test
	void getUserById_returnsUser() throws Exception {
		// Arrange
		given(userService.getUserById(1L)).willReturn(testUser);

		// Act & Assert
		mockMvc.perform(get("/api/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("testuser"))
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.firstName").value("Test"))
				.andExpect(jsonPath("$.lastName").value("User"))
				.andExpect(jsonPath("$.role").value("USER"))
				.andExpect(jsonPath("$.active").value(true));
	}

	@Test
	void createUser_returnsCreatedUser() throws Exception {
		// Arrange
		RegisterUserDTO registerDto = new RegisterUserDTO();
		registerDto.setUsername("testuser");
		registerDto.setFirstName("Test");
		registerDto.setLastName("User");
		registerDto.setEmail("test@example.com");
		registerDto.setPassword("password123");
		registerDto.setRole("USER");

		given(userService.createUser(any(RegisterUserDTO.class))).willReturn(testUser);

		// Act & Assert
		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("testuser"))
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.role").value("USER"));
	}

	@Test
	void updateUser_returnsUpdatedUser() throws Exception {
		// Arrange
		UpdateUserDTO updateDto = new UpdateUserDTO();
		updateDto.setUsername("updateduser");
		updateDto.setFirstName("Updated");
		updateDto.setLastName("User");
		updateDto.setEmail("updated@example.com");
		updateDto.setRole("ADMIN");
		updateDto.setActive(false);

		UserDTO updatedUser = new UserDTO();
		updatedUser.setId(1L);
		updatedUser.setUsername("updateduser");
		updatedUser.setFirstName("Updated");
		updatedUser.setLastName("User");
		updatedUser.setEmail("updated@example.com");
		updatedUser.setRole("ADMIN");
		updatedUser.setActive(false);

		given(userService.updateUser(eq(1L), any(UpdateUserDTO.class))).willReturn(updatedUser);

		// Act & Assert
		mockMvc.perform(put("/api/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("updateduser"))
				.andExpect(jsonPath("$.email").value("updated@example.com"))
				.andExpect(jsonPath("$.role").value("ADMIN"))
				.andExpect(jsonPath("$.active").value(false));
	}

	@Test
	void deleteUser_returnsNoContent() throws Exception {
		// Arrange
		doNothing().when(userService).deleteUser(1L);

		// Act & Assert
		mockMvc.perform(delete("/api/users/1"))
				.andExpect(status().isNoContent());
	}
}
