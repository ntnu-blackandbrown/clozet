package stud.ntnu.no.backend.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.exception.UserValidationException;
import stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException;
import stud.ntnu.no.backend.user.mapper.UserMapper;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserDTO createUser(RegisterUserDTO registerUserDTO) {
        if (registerUserDTO == null) {
            throw new UserValidationException("User registration data cannot be null");
        }
        if (userRepository.findByUsername(registerUserDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(registerUserDTO.getUsername());
        }
        if (userRepository.existsByEmail(registerUserDTO.getEmail())) {
            throw new EmailAlreadyInUseException(registerUserDTO.getEmail());
        }
        User user = userMapper.toEntity(registerUserDTO);
        user.setPasswordHash(passwordEncoder.encode(registerUserDTO.getPassword()));
        user.setRole("ROLE_USER");
        user.setActive(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    @Override
    public void createUserAndSendVerificationEmail(RegisterUserDTO registerUserDTO) {
        if (registerUserDTO == null) {
            throw new UserValidationException("User registration data cannot be null");
        }
        if (userRepository.findByUsername(registerUserDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(registerUserDTO.getUsername());
        }
        if (userRepository.existsByEmail(registerUserDTO.getEmail())) {
            throw new EmailAlreadyInUseException(registerUserDTO.getEmail());
        }
        User user = userMapper.toEntity(registerUserDTO);
        user.setPasswordHash(passwordEncoder.encode(registerUserDTO.getPassword()));
        user.setActive(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole("ROLE_USER");

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        User savedUser = userRepository.save(user);
        emailService.sendVerificationEmail(savedUser.getEmail(), token);
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (updateUserDTO.getUsername() != null &&
            !updateUserDTO.getUsername().equals(existingUser.getUsername()) &&
            userRepository.findByUsername(updateUserDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(updateUserDTO.getUsername());
        }
        if (updateUserDTO.getEmail() != null &&
            !updateUserDTO.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmail(updateUserDTO.getEmail())) {
            throw new EmailAlreadyInUseException(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getUsername() != null) {
            existingUser.setUsername(updateUserDTO.getUsername());
        }
        if (updateUserDTO.getEmail() != null) {
            existingUser.setEmail(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getFirstName() != null) {
            existingUser.setFirstName(updateUserDTO.getFirstName());
        }
        if (updateUserDTO.getLastName() != null) {
            existingUser.setLastName(updateUserDTO.getLastName());
        }
        if (updateUserDTO.getRole() != null) {
            existingUser.setRole(updateUserDTO.getRole());
        }
        existingUser.setActive(updateUserDTO.isActive());
        existingUser.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO login(LoginDTO loginDTO) {
        if (loginDTO == null || loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            throw new UserValidationException("Login data cannot be null");
        }
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new UserNotFoundException("Invalid username or password");
        }
        return userMapper.toDto(user);
    }
}
