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
 * REST controller for managing users.
 * <p>
 * This controller provides endpoints for CRUD operations on users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Retrieves all users.
     *
     * @return a list of UserDTOs
     */
    @GetMapping
    public List<UserDTO> getAllUsers() {
        logger.debug("Retrieving all users");
        return userService.getAllUsers();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the UserDTO
     */
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        logger.debug("Retrieving user with ID: {}", id);
        return userService.getUserById(id);
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param updateUserDTO the UpdateUserDTO with updated information
     * @return the updated UserDTO
     */
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        logger.debug("Updating user with ID: {}", id);
        return userService.updateUser(id, updateUserDTO);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return a ResponseEntity with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.debug("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
