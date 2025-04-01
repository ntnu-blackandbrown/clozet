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

@RestController
@RequestMapping("/api")
public class MeController {
    
    private static final Logger logger = LoggerFactory.getLogger(MeController.class);

    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // Hent autentiseringen fra SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            logger.debug("Unauthorized access attempt to /api/me");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Unauthorized"));
        }

        // Hent brukernavnet fra den autentiserte brukeren
        String username = authentication.getName();
        logger.debug("Getting user info for: {}", username);

        try {
            // Hent full brukerinfo
            UserDTO userDTO = userService.getUserByUsername(username);
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
