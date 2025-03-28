package stud.ntnu.no.backend.itemimage.service;

import stud.ntnu.no.backend.itemimage.dto.CreateItemImageDTO;
import stud.ntnu.no.backend.itemimage.dto.ItemImageDTO;

import java.util.List;

public interface ItemImageService {
    List<ItemImageDTO> getAllItemImages();
    List<ItemImageDTO> getItemImagesByItemId(Long itemId);
    ItemImageDTO getItemImage(Long id);
    ItemImageDTO createItemImage(CreateItemImageDTO itemImageDTO);
    ItemImageDTO updateItemImage(Long id, CreateItemImageDTO itemImageDTO);
    void deleteItemImage(Long id);
}