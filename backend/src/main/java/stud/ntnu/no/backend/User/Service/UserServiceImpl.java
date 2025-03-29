package stud.ntnu.no.backend.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.user.dto.*;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.exception.UserValidationException;
import stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException;
import stud.ntnu.no.backend.user.mapper.UserMapper;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, 
                           UserMapper userMapper, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<StatusUserDTO> getAllUsers() {
        return userMapper.toStatusDtoList(userRepository.findAll());
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

    @Override
    @Transactional
    public UserDTO createUser(RegisterUserDTO registerUserDTO) {
        if (registerUserDTO == null) {
            throw new UserValidationException("User registration data cannot be null");
        }

        // Sjekk om brukernavn eller e-post allerede finnes
        if (userRepository.findByUsername(registerUserDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(registerUserDTO.getUsername());
        }

        if (userRepository.existsByEmail(registerUserDTO.getEmail())) {
            throw new EmailAlreadyInUseException(registerUserDTO.getEmail());
        }

        // Konverter DTO til entitet
        User user = userMapper.toEntity(registerUserDTO);

        // Krypter passordet før lagring
        user.setPasswordHash(passwordEncoder.encode(registerUserDTO.getPassword()));

        // Lagre brukeren
        User savedUser = userRepository.save(user);

        // Returner den opprettede brukeren som DTO
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        // Sjekk om brukernavn oppdateres og allerede finnes
        if (updateUserDTO.getUsername() != null &&
            !updateUserDTO.getUsername().equals(existingUser.getUsername()) &&
            userRepository.findByUsername(updateUserDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(updateUserDTO.getUsername());
        }

        // Sjekk om e-post oppdateres og allerede finnes
        if (updateUserDTO.getEmail() != null &&
            !updateUserDTO.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmail(updateUserDTO.getEmail())) {
            throw new EmailAlreadyInUseException(updateUserDTO.getEmail());
        }

        // Oppdater feltene
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

    @Override
    @Transactional
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
