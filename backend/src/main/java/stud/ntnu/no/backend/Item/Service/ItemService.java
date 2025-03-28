package stud.ntnu.no.backend.Item.service;

import stud.ntnu.no.backend.Item.dto.ItemDTO;
import stud.ntnu.no.backend.Item.dto.CreateItemDTO;

import java.util.List;

public interface ItemService {
    List<ItemDTO> getAllItems();
    List<ItemDTO> getActiveItems();
    ItemDTO getItem(Long id);
    List<ItemDTO> getItemsBySeller(Long sellerId);
    List<ItemDTO> getItemsByCategory(Long categoryId);
    List<ItemDTO> searchItems(String query);
    ItemDTO createItem(CreateItemDTO itemDTO, Long sellerId);
    ItemDTO updateItem(Long id, CreateItemDTO itemDTO, Long sellerId);
    void deactivateItem(Long id, Long sellerId);
    void activateItem(Long id, Long sellerId);
    void deleteItem(Long id, Long sellerId);
}