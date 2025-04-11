package stud.ntnu.no.backend.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.service.UserService;

import java.util.List;

/**
 * REST controller for managing user-related operations.
 * <p>
 * This controller provides API endpoints for managing user accounts,
 * including retrieving user information, updating user profiles, and
 * deleting user accounts. It handles administrative operations on users
 * and is separate from the authentication and registration processes.
 * </p>
 * <p>
 * All endpoints return appropriate HTTP status codes and structured response data.
 * Many operations may require administrative privileges or ownership of the resource.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Retrieves all users in the system.
     * <p>
     * This endpoint returns a collection of all user profiles with sensitive
     * information removed. It is typically restricted to administrators.
     * </p>
     *
     * @return a list of UserDTOs with HTTP status 200 (OK)
     */
    @GetMapping
    public List<UserDTO> getAllUsers() {
        logger.debug("Retrieving all users");
        return userService.getAllUsers();
    }

    /**
     * Retrieves a specific user by their unique identifier.
     * <p>
     * This endpoint attempts to find and return a user that matches the provided ID.
     * </p>
     *
     * @param id the unique identifier of the user to retrieve
     * @return the UserDTO with HTTP status 200 (OK)
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID exists
     */
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        logger.debug("Retrieving user with ID: {}", id);
        return userService.getUserById(id);
    }

    /**
     * Updates an existing user's information.
     * <p>
     * This endpoint updates a user record with the provided data.
     * Only specific fields can be updated, and some fields (like email or username)
     * may require validation to ensure uniqueness.
     * </p>
     *
     * @param id the unique identifier of the user to update
     * @param updateUserDTO the data transfer object containing updated user information
     * @return the updated UserDTO with HTTP status 200 (OK)
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID exists
     * @throws stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException if the new username is already taken
     * @throws stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException if the new email is already in use
     */
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        logger.debug("Updating user with ID: {}", id);
        return userService.updateUser(id, updateUserDTO);
    }

    /**
     * Deletes a user from the system.
     * <p>
     * This endpoint removes a user account and potentially all associated data.
     * This operation is typically irreversible and should be used with caution.
     * </p>
     *
     * @param id the unique identifier of the user to delete
     * @return a ResponseEntity with HTTP status 204 (No Content) indicating successful deletion
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.debug("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}