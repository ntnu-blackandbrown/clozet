package stud.ntnu.no.backend.itemimage.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.itemimage.mapper.ItemImageMapper;
import stud.ntnu.no.backend.itemimage.service.ImageService;

/**
 * REST controller for handling image operations. Provides endpoints for uploading, retrieving, and
 * deleting images.
 */
@RestController
@RequestMapping("/api/images")
public class ImageController {

  private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
  private final ImageService imageService;
  private final ItemImageMapper itemImageMapper;

  public ImageController(ImageService imageService, ItemImageMapper itemImageMapper) {
    this.imageService = imageService;
    this.itemImageMapper = itemImageMapper;
  }

  /**
   * Uploads an image for a specific item.
   *
   * @param file   The image file to upload
   * @param itemId The ID of the item the image is associated with
   * @return The URL of the uploaded image or an error message
   */
  @PostMapping("/upload")
  public ResponseEntity<?> uploadImage(
      @RequestParam("file") MultipartFile file,
      @RequestParam("itemId") Long itemId) {

    logger.info("Uploading image for item ID: {}", itemId);
    try {
      String imageUrl = imageService.uploadImage(file, itemId);
      logger.info("Image uploaded successfully: {}", imageUrl);
      return ResponseEntity.ok().body(imageUrl);

    } catch (EmptyFileException e) {
      logger.error("Empty file upload attempt for item ID: {}", itemId, e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Filen er tom. Last opp en gyldig bildefil.");

    } catch (InvalidFileTypeException e) {
      logger.error("Invalid file type upload attempt for item ID: {}", itemId, e);
      return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
          .body("Ugyldig filtype. Bare JPEG, PNG og GIF støttes.");

    } catch (RuntimeException e) {
      logger.error("Image upload failed for item ID: {}", itemId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Opplasting feilet: " + e.getMessage());
    }
  }

  /**
   * Retrieves images by item ID.
   *
   * @param itemId The ID of the item
   * @return A list of images associated with the item
   */
  @GetMapping("/item/{itemId}")
  public ResponseEntity<List<ItemImageDTO>> getImagesByItemId(@PathVariable Long itemId) {
    List<ItemImage> images = imageService.getImagesByItemId(itemId);
    List<ItemImageDTO> imageDTOs = itemImageMapper.toDTOList(images);
    return ResponseEntity.ok(imageDTOs);
  }

  /**
   * Deletes an image by ID.
   *
   * @param id The ID of the image to delete
   * @return A response indicating the result of the delete operation
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteImage(@PathVariable Long id) {
    logger.info("Deleting image with ID: {}", id);
    imageService.deleteImage(id);
    return ResponseEntity.ok().build();
  }
}
