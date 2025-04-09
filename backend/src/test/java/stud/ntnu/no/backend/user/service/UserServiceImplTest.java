package stud.ntnu.no.backend.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException;
import stud.ntnu.no.backend.user.mapper.UserMapper;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;

class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private EmailService emailService;

  @Mock private VerificationTokenRepository verificationTokenRepository;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl userService;

  private User user;
  private UserDTO userDTO;
  private RegisterUserDTO registerUserDTO;
  private UpdateUserDTO updateUserDTO;
  private List<User> userList;
  private List<UserDTO> userDTOList;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Setup test data
    user = new User();
    user.setId(1L);
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setPasswordHash("encodedPassword");
    user.setFirstName("Test");
    user.setLastName("User");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    user.setActive(true);
    user.setRole("ROLE_USER");

    userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setUsername("testuser");
    userDTO.setEmail("test@example.com");
    userDTO.setFirstName("Test");
    userDTO.setLastName("User");

    registerUserDTO = new RegisterUserDTO();
    registerUserDTO.setUsername("newuser");
    registerUserDTO.setEmail("new@example.com");
    registerUserDTO.setPassword("password123");
    registerUserDTO.setFirstName("New");
    registerUserDTO.setLastName("User");

    updateUserDTO = new UpdateUserDTO();
    updateUserDTO.setFirstName("Updated");
    updateUserDTO.setLastName("Name");
    updateUserDTO.setEmail("updated@example.com");

    User user2 = new User();
    user2.setId(2L);
    user2.setUsername("anotheruser");
    user2.setEmail("another@example.com");
    user2.setPasswordHash("encodedPassword2");
    user2.setFirstName("Another");
    user2.setLastName("User");
    user2.setCreatedAt(LocalDateTime.now());
    user2.setUpdatedAt(LocalDateTime.now());
    user2.setActive(true);
    user2.setRole("ROLE_USER");

    UserDTO userDTO2 = new UserDTO();
    userDTO2.setId(2L);
    userDTO2.setUsername("anotheruser");
    userDTO2.setEmail("another@example.com");
    userDTO2.setFirstName("Another");
    userDTO2.setLastName("User");

    userList = Arrays.asList(user, user2);
    userDTOList = Arrays.asList(userDTO, userDTO2);
  }

  @Test
  void getAllUsers() {
    // Arrange
    when(userRepository.findAll()).thenReturn(userList);
    when(userMapper.toDto(user)).thenReturn(userDTO);
    when(userMapper.toDto(userList.get(1))).thenReturn(userDTOList.get(1));

    // Act
    List<UserDTO> result = userService.getAllUsers();

    // Assert
    assertEquals(2, result.size());
    assertEquals(userDTO.getId(), result.get(0).getId());
    assertEquals(userDTO.getUsername(), result.get(0).getUsername());
    verify(userRepository, times(1)).findAll();
    verify(userMapper, times(2)).toDto(any(User.class));
  }

  @Test
  void getUserById_ExistingId_ReturnsUser() {
    // Arrange
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDTO);

    // Act
    UserDTO result = userService.getUserById(userId);

    // Assert
    assertNotNull(result);
    assertEquals(userDTO, result);
    verify(userRepository, times(1)).findById(userId);
    verify(userMapper, times(1)).toDto(user);
  }

  @Test
  void getUserById_NonExistingId_ThrowsException() {
    // Arrange
    Long userId = 999L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          userService.getUserById(userId);
        });
    verify(userRepository, times(1)).findById(userId);
    verify(userMapper, never()).toDto(any(User.class));
  }

  @Test
  void createUser_ValidData_ReturnsUserDTO() {
    // Arrange
    when(userRepository.findByUsername(registerUserDTO.getUsername())).thenReturn(Optional.empty());
    when(userRepository.existsByEmail(registerUserDTO.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(registerUserDTO.getPassword())).thenReturn("encodedPassword");
    when(userMapper.toEntity(registerUserDTO)).thenReturn(user);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

    // Act
    UserDTO result = userService.createUser(registerUserDTO);

    // Assert
    assertNotNull(result);
    assertEquals(userDTO, result);
    verify(userRepository, times(1)).findByUsername(registerUserDTO.getUsername());
    verify(userRepository, times(1)).existsByEmail(registerUserDTO.getEmail());
    verify(passwordEncoder, times(1)).encode(registerUserDTO.getPassword());
    verify(userRepository, times(1)).save(any(User.class));
    verify(userMapper, times(1)).toDto(any(User.class));
  }

  @Test
  void createUser_ExistingUsername_ThrowsException() {
    // Arrange
    when(userRepository.findByUsername(registerUserDTO.getUsername()))
        .thenReturn(Optional.of(user));

    // Act & Assert
    assertThrows(
        UsernameAlreadyExistsException.class,
        () -> {
          userService.createUser(registerUserDTO);
        });
    verify(userRepository, times(1)).findByUsername(registerUserDTO.getUsername());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void createUser_ExistingEmail_ThrowsException() {
    // Arrange
    when(userRepository.findByUsername(registerUserDTO.getUsername())).thenReturn(Optional.empty());
    when(userRepository.existsByEmail(registerUserDTO.getEmail())).thenReturn(true);

    // Act & Assert
    assertThrows(
        EmailAlreadyInUseException.class,
        () -> {
          userService.createUser(registerUserDTO);
        });
    verify(userRepository, times(1)).findByUsername(registerUserDTO.getUsername());
    verify(userRepository, times(1)).existsByEmail(registerUserDTO.getEmail());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void updateUser_ExistingId_ReturnsUpdatedUser() {
    // Arrange
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDTO);

    // Act
    UserDTO result = userService.updateUser(userId, updateUserDTO);

    // Assert
    assertNotNull(result);
    assertEquals(userDTO, result);
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).save(user);
    verify(userMapper, times(1)).toDto(user);
  }

  @Test
  void updateUser_NonExistingId_ThrowsException() {
    // Arrange
    Long userId = 999L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          userService.updateUser(userId, updateUserDTO);
        });
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void deleteUser_ExistingId_DeletesUser() {
    // Arrange
    Long userId = 1L;
    when(userRepository.existsById(userId)).thenReturn(true);
    doNothing().when(userRepository).deleteById(userId);

    // Act
    userService.deleteUser(userId);

    // Assert
    verify(userRepository, times(1)).existsById(userId);
    verify(userRepository, times(1)).deleteById(userId);
  }

  @Test
  void deleteUser_NonExistingId_ThrowsException() {
    // Arrange
    Long userId = 999L;
    when(userRepository.existsById(userId)).thenReturn(false);

    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          userService.deleteUser(userId);
        });
    verify(userRepository, times(1)).existsById(userId);
    verify(userRepository, never()).deleteById(any());
  }

  @Test
  void login_ValidCredentials_ReturnsUserDTO() {
    // Arrange
    String username = "testuser";
    LoginDTO loginDTO = new LoginDTO();
    loginDTO.setUsernameOrEmail(username);
    loginDTO.setPassword("password123");

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDTO);

    // Act
    UserDTO result = userService.login(loginDTO);

    // Assert
    assertEquals(userDTO, result);
    verify(userRepository, times(1)).findByUsername(username);
    verify(userMapper, times(1)).toDto(user);
  }

  @Test
  void login_InvalidUsername_ThrowsException() {
    // Arrange
    String username = "wronguser";
    LoginDTO loginDTO = new LoginDTO();
    loginDTO.setUsernameOrEmail(username);
    loginDTO.setPassword("password123");

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          userService.login(loginDTO);
        });
    verify(userRepository, times(1)).findByUsername(username);
    verify(userMapper, never()).toDto(any(User.class));
  }
}
