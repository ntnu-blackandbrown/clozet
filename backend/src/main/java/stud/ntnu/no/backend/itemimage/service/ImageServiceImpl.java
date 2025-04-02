package stud.ntnu.no.backend.itemimage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.itemimage.repository.ItemImageRepository;

import java.util.List;
import java.util.Set;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/jpg", "image/gif");

    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public ImageServiceImpl(ItemImageRepository itemImageRepository,
                            ItemRepository itemRepository,
                            FileStorageService fileStorageService) {
        this.itemImageRepository = itemImageRepository;
        this.itemRepository = itemRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public String uploadImage(MultipartFile file, Long itemId) {
        validateFile(file);

        try {
            String url = fileStorageService.storeFile(file, itemId);

            // Get the item object
            Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

            // Get the item's current images
            List<ItemImage> existingImages = itemImageRepository.findByItemId(itemId);

            // Determine if this is the first (primary) image and its display order
            boolean isPrimary = existingImages.isEmpty();
            int displayOrder = existingImages.size();

            // Create and save the ItemImage entity
            ItemImage image = new ItemImage(url, isPrimary, displayOrder);
            image.setItem(item);

            itemImageRepository.save(image);

            return url;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ItemImage> getImagesByItemId(Long itemId) {
        return itemImageRepository.findByItemId(itemId);
    }

    @Override
    public void deleteImage(Long imageId) {
        itemImageRepository.deleteById(imageId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException("Failed to store empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException("File type not supported. Only JPEG, PNG, and GIF are allowed");
        }
    }
}