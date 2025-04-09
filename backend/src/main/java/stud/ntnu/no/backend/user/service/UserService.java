package stud.ntnu.no.backend.user.service;

import java.util.List;
import stud.ntnu.no.backend.user.dto.ChangePasswordDTO;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.entity.User;

/**
 * Abstract service class for managing user-related operations.
 *
 * <p>This service provides methods for user management including creation, retrieval,
 * authentication, verification, and profile management. Implementations of this class should handle
 * business logic related to users while communicating with the appropriate repositories and
 * external services.
 */
public abstract class UserService {

  /**
   * Retrieves all users in the system.
   *
   * @return a list of UserDTO objects representing all users
   */
  public abstract List<UserDTO> getAllUsers();

  /**
   * Retrieves a user by their unique identifier.
   *
   * @param id the unique identifier of the user
   * @return the UserDTO representing the requested user
   * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID
   *     exists
   */
  public abstract UserDTO getUserById(Long id);

  /**
   * Retrieves a user by their username.
   *
   * @param username the username of the user to retrieve
   * @return the UserDTO representing the requested user
   * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given
   *     username exists
   */
  public abstract UserDTO getUserByUsername(String username);

  /**
   * Creates a new user with the given information.
   *
   * @param registerUserDTO the data transfer object containing user registration information
   * @return the UserDTO representing the newly created user
   * @throws stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException if the username is
   *     already taken
   * @throws stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException if the email is already
   *     in use
   * @throws stud.ntnu.no.backend.user.exception.UserValidationException if the user data is invalid
   */
  public abstract UserDTO createUser(RegisterUserDTO registerUserDTO);

  /**
   * Creates a new user and sends a verification email to the user's email address. The user account
   * will be inactive until verified.
   *
   * @param registerUserDTO the data transfer object containing user registration information
   * @throws stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException if the username is
   *     already taken
   * @throws stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException if the email is already
   *     in use
   * @throws stud.ntnu.no.backend.user.exception.UserValidationException if the user data is invalid
   */
  public abstract void createUserAndSendVerificationEmail(RegisterUserDTO registerUserDTO);

  /**
   * Updates an existing user with the given information.
   *
   * @param id the unique identifier of the user to update
   * @param updateUserDTO the data transfer object containing updated user information
   * @return the UserDTO representing the updated user
   * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID
   *     exists
   * @throws stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException if the new username
   *     is already taken
   * @throws stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException if the new email is
   *     already in use
   */
  public abstract UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);

  /**
   * Deletes a user from the system.
   *
   * @param id the unique identifier of the user to delete
   * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID
   *     exists
   */
  public abstract void deleteUser(Long id);

  /**
   * Authenticates a user with the given credentials.
   *
   * @param loginDTO the data transfer object containing login credentials
   * @return the UserDTO representing the authenticated user
   * @throws org.springframework.security.authentication.BadCredentialsException if the credentials
   *     are invalid
   */
  public abstract UserDTO login(LoginDTO loginDTO);

  /**
   * Changes the password of a user.
   *
   * @param username the username of the user whose password should be changed
   * @param changePasswordDTO the data transfer object containing old and new password information
   * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given
   *     username exists
   * @throws org.springframework.security.authentication.BadCredentialsException if the old password
   *     is incorrect
   */
  public abstract void changePassword(String username, ChangePasswordDTO changePasswordDTO);

  /**
   * Retrieves the currently authenticated user.
   *
   * @return the User entity of the currently authenticated user
   * @throws java.lang.RuntimeException if no user is authenticated
   * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if the authenticated user
   *     cannot be found
   */
  public abstract User getCurrentUser();
}
