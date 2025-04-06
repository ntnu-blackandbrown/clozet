package stud.ntnu.no.backend.itemimage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Interface for file storage services.
 * Defines the contract for storing files associated with items.
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

