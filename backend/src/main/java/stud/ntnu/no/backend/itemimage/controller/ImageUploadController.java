package stud.ntnu.no.backend.itemimage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.service.FileStorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/test-image")
public class ImageUploadController {

    private final FileStorageService storageService;

    public ImageUploadController(FileStorageService storageService) {
        this.storageService = storageService;
    }

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
