package stud.ntnu.no.backend.itemimage.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service for storing files using Cloudinary.
 * Activated when the application property 'app.storage.use-cloudinary' is set to true.
 */
@Service("cloudinaryStorageService")
@ConditionalOnProperty(name = "app.storage.use-cloudinary", havingValue = "true")
@Primary
public class CloudinaryStorageService implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryStorageService.class);
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Stores a file in Cloudinary under a specific item folder.
     *
     * @param file The file to store
     * @param itemId The ID of the item associated with the file
     * @return The URL of the stored file
     * @throws IOException If an error occurs during file upload
     */
    @Override
    public String storeFile(MultipartFile file, Long itemId) throws IOException {
        logger.info("Storing file in Cloudinary for item ID: {}", itemId);
        Map<String, Object> params = ObjectUtils.asMap(
                "folder", "items/" + itemId,
                "resource_type", "auto"
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        String url = (String) uploadResult.get("secure_url");
        logger.info("File stored successfully in Cloudinary at: {}", url);
        return url;
    }
}
