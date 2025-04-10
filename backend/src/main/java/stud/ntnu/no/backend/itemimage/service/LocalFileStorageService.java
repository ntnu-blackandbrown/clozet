package stud.ntnu.no.backend.itemimage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for storing files locally on the server. Activated when the application property
 * 'app.storage.use-cloudinary' is set to false or not set.
 */
@Service("localFileStorageService")
@ConditionalOnProperty(name = "app.storage.use-cloudinary", havingValue = "false", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

  private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);
  private final Path fileStorageLocation;

  /**
   * Constructs a LocalFileStorageService with the specified upload directory.
   *
   * @param uploadDir The directory where files will be stored
   * @throws RuntimeException if the upload directory cannot be created
   */
  public LocalFileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
    this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.fileStorageLocation);
      logger.info("Upload directory created at: {}", this.fileStorageLocation);
    } catch (IOException ex) {
      logger.error("Could not create upload directory", ex);
      throw new RuntimeException("Could not create upload directory", ex);
    }
  }

  /**
   * Stores a file locally under a specific item directory.
   *
   * @param file   The file to store
   * @param itemId The ID of the item associated with the file
   * @return The path of the stored file
   * @throws IOException If an error occurs during file storage
   */
  @Override
  public String storeFile(MultipartFile file, Long itemId) throws IOException {
    logger.info("Storing file for item ID: {}", itemId);
    Path itemDirectory = fileStorageLocation.resolve(itemId.toString());
    if (!Files.exists(itemDirectory)) {
      Files.createDirectories(itemDirectory);
      logger.info("Created directory for item ID: {}", itemId);
    }

    String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
    String extension = originalFilename.contains(".") ?
        originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
    String filename = UUID.randomUUID() + extension;

    Path targetLocation = itemDirectory.resolve(filename);
    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    logger.info("File stored successfully at: {}", targetLocation);

    return "/images/" + itemId + "/" + filename;
  }
}
