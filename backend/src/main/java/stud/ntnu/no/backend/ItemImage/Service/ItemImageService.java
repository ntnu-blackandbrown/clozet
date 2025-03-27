package stud.ntnu.no.backend.ItemImage.Service;

import stud.ntnu.no.backend.ItemImage.DTOs.CreateItemImageDTO;
import stud.ntnu.no.backend.ItemImage.DTOs.ItemImageDTO;

import java.util.List;

public interface ItemImageService {
    List<ItemImageDTO> getAllItemImages();
    List<ItemImageDTO> getItemImagesByItemId(Long itemId);
    ItemImageDTO getItemImage(Long id);
    ItemImageDTO createItemImage(CreateItemImageDTO itemImageDTO);
    ItemImageDTO updateItemImage(Long id, CreateItemImageDTO itemImageDTO);
    void deleteItemImage(Long id);
}