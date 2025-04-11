package stud.ntnu.no.backend.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.common.controller.MessageResponse;
import stud.ntnu.no.backend.user.dto.ChangePasswordDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.service.UserService;

/**
 * REST controller for managing the authenticated user's own account.
 * <p>
 * This controller provides secure endpoints for users to manage their own account
 * information, including retrieving profile data and changing passwords. Unlike
 * the UserController, which handles administrative operations, this controller
 * strictly enforces that users can only access and modify their own information.
 * </p>
 * <p>
 * All endpoints in this controller require authentication. Requests from
 * unauthenticated users will receive a 401 Unauthorized response.
 * </p>
 * <p>
 * Security considerations:
 * <ul>
 *   <li>All endpoints validate the current user's authentication status</li>
 *   <li>Sensitive operations like password changes require verification</li>
 *   <li>Error messages are carefully crafted to avoid information leakage</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api")
public class MeController {
    
    private static final Logger logger = LoggerFactory.getLogger(MeController.class);

    private final UserService userService;

    /**
     * Constructs a new MeController with the specified user service.
     * <p>
     * Uses constructor injection to maintain immutability and thread-safety.
     * </p>
     *
     * @param userService the service responsible for user-related business logic
     */
    public MeController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the profile information for the currently authenticated user.
     * <p>
     * This endpoint extracts the user identity from the security context and
     * returns comprehensive profile information. It checks authentication status
     * before attempting to retrieve user data.
     * </p>
     * <p>
     * The response excludes sensitive data like password hashes and
     * verification tokens to maintain security.
     * </p>
     *
     * @return ResponseEntity with:
     *         <ul>
     *           <li>HTTP status 200 (OK) and UserDTO if successful</li>
     *           <li>HTTP status 401 (Unauthorized) if user is not authenticated</li>
     *           <li>HTTP status 404 (Not Found) if the authenticated user no longer exists in the database</li>
     *           <li>HTTP status 500 (Internal Server Error) for unexpected errors</li>
     *         </ul>
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        logger.info("getCurrentUser() method called");
        
        // Hent autentiseringen fra SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Log detailed authentication information
        logger.info("Authentication object: {}", authentication);
        logger.info("Authentication details: isAuthenticated={}, principal={}, authorities={}", 
            authentication != null ? authentication.isAuthenticated() : "null",
            authentication != null ? authentication.getPrincipal() : "null",
            authentication != null ? authentication.getAuthorities() : "null");
        
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            logger.info("Unauthorized access attempt to /api/me");
            logger.info("Authentication state: null={}, isAuthenticated={}, principal={}", 
                authentication == null, 
                authentication != null ? authentication.isAuthenticated() : "N/A",
                authentication != null ? authentication.getPrincipal() : "N/A");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Unauthorized"));
        }

        // Hent brukernavnet fra den autentiserte brukeren
        String username = authentication.getName();
        logger.info("Getting user info for: {}", username);

        try {
            // Hent full brukerinfo
            logger.info("Calling userService.getUserByUsername({})", username);
            UserDTO userDTO = userService.getUserByUsername(username);
            logger.info("Successfully retrieved user info: {}", userDTO);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting user info: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("An error occurred while retrieving user information: " + e.getMessage()));
        }
    }

    /**
     * Changes the password for the currently authenticated user.
     * <p>
     * This endpoint allows users to update their password by providing their current
     * password (for verification) and a new password. It implements multiple validation
     * steps:
     * </p>
     * <ol>
     *   <li>Verifies that the request contains valid data</li>
     *   <li>Confirms that the user is properly authenticated</li>
     *   <li>Validates that the current password is correct before allowing the change</li>
     * </ol>
     * <p>
     * Data persistence: Upon successful validation, the password is securely hashed
     * and stored in the database. The previous password hash is immediately overwritten
     * to prevent any possibility of reverting to the old password.
     * </p>
     * <p>
     * Security notes: This method requires re-authentication with the current password
     * to prevent password changes by attackers who might have temporary access to a
     * user's session.
     * </p>
     *
     * @param changePasswordDTO data transfer object containing current password and new password
     * @return ResponseEntity with:
     *         <ul>
     *           <li>HTTP status 200 (OK) with success message if password was changed</li>
     *           <li>HTTP status 400 (Bad Request) if validation fails or current password is incorrect</li>
     *           <li>HTTP status 401 (Unauthorized) if user is not authenticated</li>
     *           <li>HTTP status 404 (Not Found) if the authenticated user no longer exists in the database</li>
     *           <li>HTTP status 500 (Internal Server Error) for unexpected errors</li>
     *         </ul>
     */
    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        System.out.println("MeController.changePassword - Request received");
        logger.debug("Password change request received");
        
        // Validate request body
        if (changePasswordDTO == null) {
            System.out.println("MeController.changePassword - DTO is null");
            logger.error("ChangePasswordDTO is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Password change request is invalid"));
        }
        
        if (changePasswordDTO.getCurrentPassword() == null || changePasswordDTO.getCurrentPassword().isEmpty()) {
            System.out.println("MeController.changePassword - Current password is missing");
            logger.error("Current password is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Current password is required"));
        }
        
        if (changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().isEmpty()) {
            System.out.println("MeController.changePassword - New password is missing");
            logger.error("New password is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("New password is required"));
        }
        
        // Hent autentiseringen fra SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("MeController.changePassword - Authentication: " + (authentication != null ? authentication.getName() : "null"));
        
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            System.out.println("MeController.changePassword - User not authenticated");
            logger.error("User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Unauthorized"));
        }

        // Hent brukernavnet fra den autentiserte brukeren
        String username = authentication.getName();
        System.out.println("MeController.changePassword - Username: " + username);
        logger.debug("Changing password for user: {}", username);

        try {
            System.out.println("MeController.changePassword - Calling service.changePassword");
            userService.changePassword(username, changePasswordDTO);
            System.out.println("MeController.changePassword - Service call completed successfully");
            logger.info("Password changed successfully for user: {}", username);
            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
        } catch (UserNotFoundException e) {
            System.out.println("MeController.changePassword - User not found: " + e.getMessage());
            logger.error("User not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(e.getMessage()));
        } catch (BadCredentialsException e) {
            System.out.println("MeController.changePassword - Bad credentials: " + e.getMessage());
            logger.error("Invalid current password for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println("MeController.changePassword - Exception: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            logger.error("Error changing password for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("An error occurred while changing the password: " + e.getMessage()));
        }
    }
}
