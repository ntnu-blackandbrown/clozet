package stud.ntnu.no.backend.itemimage.service;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.itemimage.repository.ItemImageRepository;

/**
 * Implementation of the ImageService interface. Provides methods for uploading, retrieving, and
 * deleting item images.
 */
@Service
public class ImageServiceImpl implements ImageService {

  private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
  private static final Set<String> ALLOWED_CONTENT_TYPES =
      Set.of("image/jpeg", "image/png", "image/jpg", "image/gif");

  private final ItemImageRepository itemImageRepository;
  private final ItemRepository itemRepository;
  private final FileStorageService fileStorageService;

  /**
   * Constructs an ImageServiceImpl with the specified repositories and storage service.
   *
   * @param itemImageRepository Repository for item images
   * @param itemRepository Repository for items
   * @param fileStorageService Service for storing files
   */
  @Autowired
  public ImageServiceImpl(
      ItemImageRepository itemImageRepository,
      ItemRepository itemRepository,
      FileStorageService fileStorageService) {
    this.itemImageRepository = itemImageRepository;
    this.itemRepository = itemRepository;
    this.fileStorageService = fileStorageService;
  }

  /**
   * Uploads an image and associates it with a specific item.
   *
   * @param file The image file to upload
   * @param itemId The ID of the item the image is associated with
   * @return The URL of the uploaded image
   * @throws RuntimeException if the item is not found or file storage fails
   */
  @Override
  public String uploadImage(MultipartFile file, Long itemId) {
    logger.info("Uploading image for item ID: {}", itemId);
    validateFile(file);

    try {
      String url = fileStorageService.storeFile(file, itemId);
      logger.info("Image stored successfully at: {}", url);

      // Get the item object
      Item item =
          itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));

      // Get the item's current images
      List<ItemImage> existingImages = itemImageRepository.findByItemId(itemId);

      // Determine if this is the first (primary) image and its display order
      boolean isPrimary = existingImages.isEmpty();
      int displayOrder = existingImages.size();

      // Create and save the ItemImage entity
      ItemImage image = new ItemImage(url, isPrimary, displayOrder);
      image.setItem(item);

      itemImageRepository.save(image);
      logger.info("Image associated with item ID: {}", itemId);

      return url;
    } catch (Exception e) {
      logger.error("Failed to store file for item ID: {}", itemId, e);
      throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
    }
  }

  /**
   * Retrieves all images associated with a specific item.
   *
   * @param itemId The ID of the item
   * @return A list of images associated with the item
   */
  @Override
  public List<ItemImage> getImagesByItemId(Long itemId) {
    logger.info("Retrieving images for item ID: {}", itemId);
    return itemImageRepository.findByItemId(itemId);
  }

  /**
   * Deletes an image by its ID.
   *
   * @param imageId The ID of the image to delete
   */
  @Override
  public void deleteImage(Long imageId) {
    logger.info("Deleting image with ID: {}", imageId);
    itemImageRepository.deleteById(imageId);
  }

  /**
   * Validates the uploaded file.
   *
   * @param file The file to validate
   * @throws EmptyFileException if the file is empty
   * @throws InvalidFileTypeException if the file type is not supported
   */
  private void validateFile(MultipartFile file) {
    if (file.isEmpty()) {
      logger.warn("Attempted to upload an empty file");
      throw new EmptyFileException("Failed to store empty file");
    }

    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      logger.warn("Unsupported file type: {}", contentType);
      throw new InvalidFileTypeException(
          "File type not supported. Only JPEG, PNG, and GIF are allowed");
    }
  }
}
