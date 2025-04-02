package stud.ntnu.no.backend.itemimage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.itemimage.service.ImageService;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
        @RequestParam("file") MultipartFile file,
        @RequestParam("itemId") Long itemId) {

        try {
            String imageUrl = imageService.uploadImage(file, itemId);
            return ResponseEntity.ok().body(imageUrl);

        } catch (EmptyFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Filen er tom. Last opp en gyldig bildefil.");

        } catch (InvalidFileTypeException e) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body("Ugyldig filtype. Bare JPEG, PNG og GIF støttes.");

        } catch (RuntimeException e) {
            // Dette fanger f.eks. "Item not found" eller "Failed to store file"
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Opplasting feilet: " + e.getMessage());
        }
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ItemImage>> getImagesByItemId(@PathVariable Long itemId) {
        List<ItemImage> images = imageService.getImagesByItemId(itemId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }
}
