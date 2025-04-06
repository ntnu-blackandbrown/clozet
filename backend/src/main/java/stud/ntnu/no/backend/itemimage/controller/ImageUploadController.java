package stud.ntnu.no.backend.itemimage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.service.FileStorageService;

import java.io.IOException;

/**
 * REST controller for testing image upload functionality.
 * Provides an endpoint for uploading images.
 */
@RestController
@RequestMapping("/api/test-image")
public class ImageUploadController {

    private final FileStorageService storageService;

    public ImageUploadController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Uploads an image and associates it with an optional item ID.
     *
     * @param file The image file to upload
     * @param itemId The optional ID of the item the image is associated with
     * @return The URL of the uploaded image or an error message
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "itemId", required = false) Long itemId) {

        try {
            String url = storageService.storeFile(file, itemId);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Feil ved opplasting: " + e.getMessage());
        }
    }
}
