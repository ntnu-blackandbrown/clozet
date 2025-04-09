package stud.ntnu.no.backend.itemimage.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;

/**
 * Service interface for managing item images. Defines operations for uploading, retrieving, and
 * deleting images.
 */
public interface ImageService {

  /**
   * Uploads an image and associates it with a specific item.
   *
   * @param file The image file to upload
   * @param itemId The ID of the item the image is associated with
   * @return The URL of the uploaded image
   */
  String uploadImage(MultipartFile file, Long itemId);

  /**
   * Retrieves all images associated with a specific item.
   *
   * @param itemId The ID of the item
   * @return A list of images associated with the item
   */
  List<ItemImage> getImagesByItemId(Long itemId);

  /**
   * Deletes an image by its ID.
   *
   * @param imageId The ID of the image to delete
   */
  void deleteImage(Long imageId);
}
