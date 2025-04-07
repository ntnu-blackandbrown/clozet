package stud.ntnu.no.backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.common.security.model.CustomUserDetails;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.dto.ChangePasswordDTO;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.entity.VerificationToken;
import stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.exception.UserValidationException;
import stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException;
import stud.ntnu.no.backend.user.mapper.UserMapper;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of UserService for managing users.
 */
@Service
public class UserServiceImpl extends UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;
    
    @Value("${app.email.verification-expiry-hours:24}")
    private int verificationExpiryHours;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                          UserMapper userMapper,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService,
                          VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUser();
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Username " + username + " not found"));
    }

    /**
     * Retrieves all users.
     *
     * @return a list of user DTOs
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("Username " + username + " not found"));
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
        user.setActive(true);
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

        // Save the user first
        User savedUser = userRepository.save(user);
        
        // Generate a verification token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(verificationExpiryHours);
        
        // Create and save the verification token
        VerificationToken verificationToken = new VerificationToken(token, expiryDate, savedUser);
        verificationTokenRepository.save(verificationToken);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), token);
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.findByUsername(updateUserDTO.getUsername()).isPresent()) {
                throw new UsernameAlreadyExistsException(updateUserDTO.getUsername());
            }
            existingUser.setUsername(updateUserDTO.getUsername());
        }

        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(updateUserDTO.getEmail())) {
                throw new EmailAlreadyInUseException(updateUserDTO.getEmail());
            }
            existingUser.setEmail(updateUserDTO.getEmail());
        }

        if (updateUserDTO.getFirstName() != null) {
            existingUser.setFirstName(updateUserDTO.getFirstName());
        }

        if (updateUserDTO.getLastName() != null) {
            existingUser.setLastName(updateUserDTO.getLastName());
        }

        // Only ADMIN can change roles
        if (updateUserDTO.getRole() != null && existingUser.getRole().contains("ADMIN")) {
            existingUser.setRole(updateUserDTO.getRole());
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        return userMapper.toDto(userRepository.save(existingUser));
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
        String usernameOrEmail = loginDTO.getUsernameOrEmail();

        if (usernameOrEmail.contains("@")) {
            // If input looks like an email, find by email
            return userRepository.findByEmail(usernameOrEmail)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("No user found with email: " + usernameOrEmail));
        } else {
            // Otherwise find by username
            return userRepository.findByUsername(usernameOrEmail)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("Username " + usernameOrEmail + " not found"));
        }
    }

    @Transactional
    @Override
    public void changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        System.out.println("UserServiceImpl.changePassword - Username: " + username);
        System.out.println("UserServiceImpl.changePassword - Current Password: " +
            (changePasswordDTO.getCurrentPassword() != null ? "******" : "null"));
        System.out.println("UserServiceImpl.changePassword - New Password: " +
            (changePasswordDTO.getNewPassword() != null ? "******" : "null"));

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Username " + username + " not found"));
        System.out.println("UserServiceImpl.changePassword - User found: " + user.getId());

        // Verify current password
        boolean passwordMatches = passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPasswordHash());
        System.out.println("UserServiceImpl.changePassword - Password matches: " + passwordMatches);

        if (!passwordMatches) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        // Update password
        String newPasswordHash = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        System.out.println("UserServiceImpl.changePassword - New password hash generated");

        user.setPasswordHash(newPasswordHash);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        System.out.println("UserServiceImpl.changePassword - Password updated, user ID: " + savedUser.getId());

        // Send confirmation email
        emailService.sendPasswordChangeConfirmationEmail(user.getEmail());
    }
}
