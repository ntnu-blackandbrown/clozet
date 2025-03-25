package stud.ntnu.no.backend.User.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.User.DTOs.*;
import stud.ntnu.no.backend.User.UserService;
import stud.ntnu.no.backend.security.config.SecurityConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class, UserControllerTest.TestConfig.class})
@ActiveProfiles("test") // Use test profile to isolate test environment
@Import(SecurityConfig.class) // Import security configuration for proper request handling
class UserControllerTest {

	@TestConfiguration
	static class TestConfig {
		@Bean
		@Primary
		public UserService userService() {
			return Mockito.mock(UserService.class);
		}
		
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;
	
	// Test data objects
	private StatusUserDTO statusDto;
	private UserDTO userDto;
	private RegisterUserDTO registerDto;
	private UpdateUserDTO updateDto;
	
	// Admin user for initialization simulation
	private UserDTO adminUserDto;

	@BeforeEach
	void setUp() {
		// Reset mock before each test
		Mockito.reset(userService);
		
		// Set up admin user similar to DatabaseInitializer
		adminUserDto = new UserDTO();
		adminUserDto.setId(1L);
		adminUserDto.setUsername("Admin");
		adminUserDto.setFirstName("Admin");
		adminUserDto.setLastName("Administrator");
		adminUserDto.setEmail("admin@example.com");
		adminUserDto.setActive(true);
		adminUserDto.setRole("ADMIN");
		
		// Standard test user
		statusDto = new StatusUserDTO();
		statusDto.setId(2L);
		statusDto.setUsername("alice");
		statusDto.setEmail("alice@example.com");
		statusDto.setActive(true);

		userDto = new UserDTO();
		userDto.setId(2L);
		userDto.setUsername("alice");
		userDto.setFirstName("Alice");
		userDto.setLastName("Wonderland");
		userDto.setEmail("alice@example.com");
		userDto.setActive(true);
		userDto.setRole("USER");

		registerDto = new RegisterUserDTO();
		registerDto.setUsername("bob");
		registerDto.setFirstName("Bob");
		registerDto.setLastName("Builder");
		registerDto.setEmail("bob@example.com");
		registerDto.setPassword("password");
		registerDto.setRole("USER");

		updateDto = new UpdateUserDTO();
		updateDto.setUsername("bobby");
		updateDto.setFirstName("Bobby");
		updateDto.setLastName("Builder");
		updateDto.setEmail("bobby@example.com");
		updateDto.setActive(false);
		updateDto.setRole("ADMIN");
		
		// Mock that getAllUsers would return both admin and regular user
		given(userService.getAllUsers()).willReturn(List.of(
			convertToStatusDTO(adminUserDto),
			statusDto
		));
	}
	
	// Helper method to convert UserDTO to StatusUserDTO for consistency
	private StatusUserDTO convertToStatusDTO(UserDTO userDTO) {
		StatusUserDTO statusUserDTO = new StatusUserDTO();
		statusUserDTO.setId(userDTO.getId());
		statusUserDTO.setEmail(userDTO.getEmail());
		statusUserDTO.setUsername(userDTO.getUsername());
		statusUserDTO.setActive(userDTO.isActive());
		return statusUserDTO;
	}

	@Test
	void getAllUsers_returnsList() throws Exception {
		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].username").value("Admin"))
				.andExpect(jsonPath("$[0].active").value(true))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].username").value("alice"));
	}

	@Test
	void getUserById_returnsUser() throws Exception {
		// Test getting admin user
		given(userService.getUserById(1L)).willReturn(adminUserDto);
		mockMvc.perform(get("/api/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("Admin"))
				.andExpect(jsonPath("$.role").value("ADMIN"));
		
		// Test getting regular user
		given(userService.getUserById(2L)).willReturn(userDto);
		mockMvc.perform(get("/api/users/2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.username").value("alice"))
				.andExpect(jsonPath("$.email").value("alice@example.com"));
	}

	@Test
	void createUser_returnsCreatedUser() throws Exception {
		// Set ID to 3 since we already have admin(1) and alice(2)
		UserDTO createdUserDto = new UserDTO();
		createdUserDto.setId(3L);
		createdUserDto.setUsername("bob");
		createdUserDto.setFirstName("Bob");
		createdUserDto.setLastName("Builder");
		createdUserDto.setEmail("bob@example.com");
		createdUserDto.setActive(true);
		createdUserDto.setRole("USER");
		
		given(userService.createUser(any(RegisterUserDTO.class))).willReturn(createdUserDto);

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(3))
				.andExpect(jsonPath("$.username").value("bob"))
				.andExpect(jsonPath("$.role").value("USER"));
	}

	@Test
	void updateUser_returnsUpdatedUser() throws Exception {
		// Create updated version of alice
		UserDTO updatedUserDto = new UserDTO();
		updatedUserDto.setId(2L);
		updatedUserDto.setUsername("alice_updated");
		updatedUserDto.setFirstName("Alice");
		updatedUserDto.setLastName("Updated");
		updatedUserDto.setEmail("alice_updated@example.com");
		updatedUserDto.setActive(true);
		updatedUserDto.setRole("ADMIN");
		
		given(userService.updateUser(eq(2L), any(UpdateUserDTO.class))).willReturn(updatedUserDto);

		mockMvc.perform(put("/api/users/2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.username").value("alice_updated"))
				.andExpect(jsonPath("$.role").value("ADMIN"));
	}

	@Test
	void deleteUser_returnsNoContent() throws Exception {
		doNothing().when(userService).deleteUser(2L);

		mockMvc.perform(delete("/api/users/2"))
				.andExpect(status().isNoContent());
	}
}
