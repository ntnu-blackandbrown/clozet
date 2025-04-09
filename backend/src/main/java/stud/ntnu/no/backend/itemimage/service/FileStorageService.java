package stud.ntnu.no.backend.itemimage.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for file storage services. Defines the contract for storing files associated with
 * items.
 */
public interface FileStorageService {

  /**
   * Stores a file associated with a specific item.
   *
   * @param file The file to store
   * @param itemId The ID of the item associated with the file
   * @return The URL or path of the stored file
   * @throws IOException If an error occurs during file storage
   */
  String storeFile(MultipartFile file, Long itemId) throws IOException;
}
