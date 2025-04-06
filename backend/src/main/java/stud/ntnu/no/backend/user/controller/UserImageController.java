package stud.ntnu.no.backend.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.user.service.UserProfileService;

/**
 * REST controller for managing user profile images.
 * <p>
 * This controller provides endpoints for uploading, retrieving, and deleting user profile images.
 */
@RestController
@RequestMapping("/api/users/profile")
public class UserImageController {

    private static final Logger logger = LoggerFactory.getLogger(UserImageController.class);

    private final UserProfileService userProfileService;

    /**
     * Constructs a new UserImageController with the specified user profile service.
     *
     * @param userProfileService the UserProfileService
     */
    @Autowired
    public UserImageController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Uploads a profile image for a user.
     *
     * @param file the MultipartFile containing the image
     * @param userId the ID of the user
     * @return the URL of the uploaded image or an error message if the upload fails
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        logger.debug("Uploading profile image for user ID: {}", userId);
        try {
            String imageUrl = userProfileService.uploadProfilePicture(file, userId);
            return ResponseEntity.ok().body(imageUrl);
        } catch (EmptyFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File is empty. Please upload a valid image file.");
        } catch (InvalidFileTypeException e) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Invalid file type. Only JPEG, PNG, and GIF are supported.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    /**
     * Retrieves the profile image URL for a user.
     *
     * @param userId the ID of the user
     * @return the URL of the profile image or a no content response if not found
     */
    @GetMapping("/{userId}/image")
    public ResponseEntity<?> getProfileImage(@PathVariable Long userId) {
        logger.debug("Retrieving profile image for user ID: {}", userId);
        try {
            String imageUrl = userProfileService.getProfilePictureUrl(userId);
            if (imageUrl == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok().body(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve profile picture: " + e.getMessage());
        }
    }

    /**
     * Deletes the profile image for a user.
     *
     * @param userId the ID of the user
     * @return a success message or an error message if the deletion fails
     */
    @DeleteMapping("/{userId}/image")
    public ResponseEntity<?> deleteProfileImage(@PathVariable Long userId) {
        logger.debug("Deleting profile image for user ID: {}", userId);
        try {
            userProfileService.deleteProfilePicture(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete profile picture: " + e.getMessage());
        }
    }
}
