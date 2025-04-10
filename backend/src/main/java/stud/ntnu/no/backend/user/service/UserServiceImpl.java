package stud.ntnu.no.backend.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.category.repository.CategoryRepository;
import stud.ntnu.no.backend.common.security.model.CustomUserDetails;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.favorite.repository.FavoriteRepository;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.itemimage.repository.ItemImageRepository;
import stud.ntnu.no.backend.location.repository.LocationRepository;
import stud.ntnu.no.backend.message.repository.MessageRepository;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;
import stud.ntnu.no.backend.transaction.repository.TransactionRepository;
import stud.ntnu.no.backend.user.dto.ChangePasswordDTO;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.entity.VerificationToken;
import stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.exception.UserValidationException;
import stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException;
import stud.ntnu.no.backend.user.mapper.UserMapper;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;

/**
 * Implementation of the UserService abstract class for managing user-related operations.
 * <p>
 * This service provides a complete implementation of user management functionality including user
 * registration, authentication, profile management, and account verification. It handles
 * communication with the user repository for database operations, handles password encoding and
 * verification, and manages email communication for account-related actions.
 * </p>
 * <p>
 * Key responsibilities include:
 * <ul>
 *   <li>User registration with email verification</li>
 *   <li>User authentication</li>
 *   <li>User profile management (update, delete)</li>
 *   <li>Password change and reset functionality</li>
 *   <li>User retrieval by various criteria</li>
 * </ul>
 * </p>
 * <p>
 * This implementation uses Spring Security for authentication and authorization,
 * a password encoder for secure password storage, and an email service for
 * communicating with users.
 * </p>
 */
@Service
public class UserServiceImpl extends UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final VerificationTokenRepository verificationTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final FavoriteRepository favoriteRepository;
  private final MessageRepository messageRepository;
  private final TransactionRepository transactionRepository;
  private final ItemImageRepository itemImageRepository;
  private final ItemRepository itemRepository;
  private final LocationRepository locationRepository;
  private final ShippingOptionRepository shippingOptionRepository;
  private final CategoryRepository categoryRepository;
  @Value("${app.email.verification-expiry-hours:24}")
  private int verificationExpiryHours;

  @Autowired
  public UserServiceImpl(UserRepository userRepository,
      UserMapper userMapper,
      PasswordEncoder passwordEncoder,
      EmailService emailService,
      VerificationTokenRepository verificationTokenRepository,
      PasswordResetTokenRepository passwordResetTokenRepository,
      FavoriteRepository favoriteRepository,
      MessageRepository messageRepository,
      TransactionRepository transactionRepository,
      ItemImageRepository itemImageRepository,
      ItemRepository itemRepository,
      LocationRepository locationRepository,
      ShippingOptionRepository shippingOptionRepository,
      CategoryRepository categoryRepository) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.verificationTokenRepository = verificationTokenRepository;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.favoriteRepository = favoriteRepository;
    this.messageRepository = messageRepository;
    this.transactionRepository = transactionRepository;
    this.itemImageRepository = itemImageRepository;
    this.itemRepository = itemRepository;
    this.locationRepository = locationRepository;
    this.shippingOptionRepository = shippingOptionRepository;
    this.categoryRepository = categoryRepository;
  }

  /**
   * Retrieves the currently authenticated user from the security context.
   * <p>
   * This method checks the current security context for an authenticated user and returns the
   * corresponding User entity. It handles two authentication scenarios:
   * </p>
   * <ol>
   *   <li>When the principal is a CustomUserDetails instance (most common case)</li>
   *   <li>When only the username is available in the authentication</li>
   * </ol>
   *
   * @return the currently authenticated User entity
   * @throws RuntimeException      if no authenticated user is found in the security context
   * @throws UserNotFoundException if the authenticated username cannot be found in the database
   */
  @Override
  public User getCurrentUser() {
    logger.info("Getting current user");
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
   * Retrieves all users from the database.
   * <p>
   * This method fetches all user records from the database and converts them to DTOs for external
   * use.
   * </p>
   *
   * @return a list of user DTOs representing all users in the system
   */
  @Override
  public List<UserDTO> getAllUsers() {
    logger.info("Getting all users");
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves a user by their unique identifier.
   * <p>
   * This method attempts to find a user with the specified ID and converts the entity to a DTO if
   * found.
   * </p>
   *
   * @param id the unique identifier of the user to retrieve
   * @return the UserDTO representing the requested user
   * @throws UserNotFoundException if no user with the given ID exists
   */
  @Override
  public UserDTO getUserById(Long id) {
    logger.info("Getting user by ID: {}", id);
    return userRepository.findById(id)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  /**
   * Retrieves a user by their username.
   * <p>
   * This method attempts to find a user with the specified username and converts the entity to a
   * DTO if found.
   * </p>
   *
   * @param username the username of the user to retrieve
   * @return the UserDTO representing the requested user
   * @throws UserNotFoundException if no user with the given username exists
   */
  @Override
  public UserDTO getUserByUsername(String username) {
    logger.info("Getting user by username: {}", username);
    return userRepository.findByUsername(username)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException("Username " + username + " not found"));
  }

  @Transactional
  @Override
  public UserDTO createUser(RegisterUserDTO registerUserDTO) {
    logger.info("Creating user: {}", registerUserDTO);
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
    logger.info("Creating user and sending verification email: {}", registerUserDTO);
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
    logger.info("Updating user with ID: {}", id);
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername()
        .equals(existingUser.getUsername())) {
      if (userRepository.findByUsername(updateUserDTO.getUsername()).isPresent()) {
        throw new UsernameAlreadyExistsException(updateUserDTO.getUsername());
      }
      existingUser.setUsername(updateUserDTO.getUsername());
    }

    if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail()
        .equals(existingUser.getEmail())) {
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

  /**
   * Deletes a user from the system.
   * <p>
   * This method deletes a user and all associated data, following the proper deletion order to
   * maintain data integrity. It handles the following relationships:
   * <ol>
   *   <li>Verification tokens</li>
   *   <li>Password reset tokens</li>
   *   <li>Items and related data</li>
   * </ol>
   * </p>
   *
   * @param id the unique identifier of the user to delete
   * @throws UserNotFoundException if no user with the given ID exists
   * @throws RuntimeException      if a deletion error occurs
   */
  @Transactional
  @Override
  public void deleteUser(Long id) {
    logger.info("Deleting user with ID: {}", id);

    // Verify user exists
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    // Use a transaction to ensure all operations succeed or fail together
    try {
      // 1. Handle verification tokens - use existing repository method
      verificationTokenRepository.deleteByUser(user);

      // 2. Handle password reset tokens - use existing repository method
      passwordResetTokenRepository.deleteByUser(user);

      // 3. Delete the user, relying on JPA cascade for other entities
      // The database should have been configured with proper cascade deletion
      // for remaining entities like favorites, messages, items, etc.
      userRepository.delete(user);

      logger.info("Successfully deleted user with ID: {}", id);
    } catch (Exception e) {
      logger.error("Error deleting user with ID: {}", id, e);
      throw new RuntimeException("Failed to delete user and related entities", e);
    }
  }

  @Override
  public UserDTO login(LoginDTO loginDTO) {
    logger.info("Logging in user: {}", loginDTO.getUsernameOrEmail());
    String usernameOrEmail = loginDTO.getUsernameOrEmail();

    if (usernameOrEmail.contains("@")) {
      // If input looks like an email, find by email
      return userRepository.findByEmail(usernameOrEmail)
          .map(userMapper::toDto)
          .orElseThrow(
              () -> new UserNotFoundException("No user found with email: " + usernameOrEmail));
    } else {
      // Otherwise find by username
      return userRepository.findByUsername(usernameOrEmail)
          .map(userMapper::toDto)
          .orElseThrow(
              () -> new UserNotFoundException("Username " + usernameOrEmail + " not found"));
    }
  }

  @Transactional
  @Override
  public void changePassword(String username, ChangePasswordDTO changePasswordDTO) {
    logger.info("Changing password for user: {}", username);
    System.out.println("UserServiceImpl.changePassword - Username: " + username);
    System.out.println("UserServiceImpl.changePassword - Current Password: " +
        (changePasswordDTO.getCurrentPassword() != null ? "******" : "null"));
    System.out.println("UserServiceImpl.changePassword - New Password: " +
        (changePasswordDTO.getNewPassword() != null ? "******" : "null"));

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("Username " + username + " not found"));
    System.out.println("UserServiceImpl.changePassword - User found: " + user.getId());

    // Verify current password
    boolean passwordMatches = passwordEncoder.matches(changePasswordDTO.getCurrentPassword(),
        user.getPasswordHash());
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
    System.out.println(
        "UserServiceImpl.changePassword - Password updated, user ID: " + savedUser.getId());

    // Send confirmation email
    emailService.sendPasswordChangeConfirmationEmail(user.getEmail());
  }
}
