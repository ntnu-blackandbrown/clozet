package stud.ntnu.no.backend.User.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import stud.ntnu.no.backend.User.DTOs.*;
import stud.ntnu.no.backend.User.UserService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
// Bruker ContextConfiguration for å spesifisere at vi kun skal laste inn UserController og TestConfig,
// og dermed unngå å laste den reelle UserService (som avhenger av UserRepository)
@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class, UserControllerTest.TestConfig.class})
class UserControllerTest {

	@TestConfiguration
	static class TestConfig {
		@Bean
		@Primary  // Denne beanen skal brukes istedenfor den reelle UserService
		public UserService userService() {
			return Mockito.mock(UserService.class);
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	// Denne mocken injiseres nå fra vår TestConfig, og vi unngår dermed at
	// den reelle UserService med avhengighet til UserRepository forsøkes opprettet.
	@Autowired
	private UserService userService;

	private StatusUserDTO statusDto;
	private UserDTO userDto;
	private RegisterUserDTO registerDto;
	private UpdateUserDTO updateDto;

	@BeforeEach
	void setUp() {
		statusDto = new StatusUserDTO();
		statusDto.setId(1L);
		statusDto.setUsername("alice");
		statusDto.setEmail("alice@example.com");
		statusDto.setActive(true);

		userDto = new UserDTO();
		userDto.setId(1L);
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
	}

	@Test
	void getAllUsers_returnsList() throws Exception {
		given(userService.getAllUsers()).willReturn(List.of(statusDto));

		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].username").value("alice"))
				.andExpect(jsonPath("$[0].active").value(true));
	}

	@Test
	void getUserById_returnsUser() throws Exception {
		given(userService.getUserById(1L)).willReturn(userDto);

		mockMvc.perform(get("/api/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("alice"))
				.andExpect(jsonPath("$.email").value("alice@example.com"));
	}

	@Test
	void createUser_returnsCreatedUser() throws Exception {
		given(userService.createUser(any(RegisterUserDTO.class))).willReturn(userDto);

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("alice"));
	}

	@Test
	void updateUser_returnsUpdatedUser() throws Exception {
		given(userService.updateUser(eq(1L), any(UpdateUserDTO.class))).willReturn(userDto);

		mockMvc.perform(put("/api/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("alice"));
	}

	@Test
	void deleteUser_returnsNoContent() throws Exception {
		doNothing().when(userService).deleteUser(1L);

		mockMvc.perform(delete("/api/users/1"))
				.andExpect(status().isNoContent());
	}
}
