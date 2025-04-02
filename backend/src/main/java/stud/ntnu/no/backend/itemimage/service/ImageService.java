package stud.ntnu.no.backend.itemimage.service;

import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;

import java.util.List;

public interface ImageService {
    String uploadImage(MultipartFile file, Long itemId);
    List<ItemImage> getImagesByItemId(Long itemId);
    void deleteImage(Long imageId);
}