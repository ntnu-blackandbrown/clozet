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

@Service("localFileStorageService")
public class LocalFileStorageService implements FileStorageService {
    private final Path fileStorageLocation;

    public LocalFileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Long itemId) throws IOException {
        // Create itemId directory
        Path itemDirectory = fileStorageLocation.resolve(itemId.toString());
        if (!Files.exists(itemDirectory)) {
            Files.createDirectories(itemDirectory);
        }

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFilename.contains(".") ?
            originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID().toString() + extension;

        // Copy file
        Path targetLocation = itemDirectory.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + itemId + "/" + filename;
    }
}
