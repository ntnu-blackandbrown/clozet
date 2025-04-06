package stud.ntnu.no.backend.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.user.service.UserProfileService;

@RestController
@RequestMapping("/api/users/profile")
public class UserImageController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserImageController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

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

    @GetMapping("/{userId}/image")
    public ResponseEntity<?> getProfileImage(@PathVariable Long userId) {
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

    @DeleteMapping("/{userId}/image")
    public ResponseEntity<?> deleteProfileImage(@PathVariable Long userId) {
        try {
            userProfileService.deleteProfilePicture(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete profile picture: " + e.getMessage());
        }
    }
}