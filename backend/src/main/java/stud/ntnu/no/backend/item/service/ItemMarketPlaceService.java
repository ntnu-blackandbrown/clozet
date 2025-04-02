package stud.ntnu.no.backend.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.item.dto.ItemMarketPlaceDTO;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.mapper.ItemMarketPlaceMapper;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemMarketPlaceService {

    private final ItemRepository itemRepository;
    private final ItemMarketPlaceMapper itemMarketPlaceMapper;
    private final UserService userService;

    @Autowired
    public ItemMarketPlaceService(ItemRepository itemRepository, 
                                 ItemMarketPlaceMapper itemMarketPlaceMapper,
                                 UserService userService) {
        this.itemRepository = itemRepository;
        this.itemMarketPlaceMapper = itemMarketPlaceMapper;
        this.userService = userService;
    }

    public List<ItemMarketPlaceDTO> getAllMarketPlaceItems() {
        // Get current user, if authenticated
        User currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception e) {
            // User not authenticated, continue with null user
        }

        final User finalUser = currentUser;
        
        // Get all available items and map to DTOs
        List<Item> items = itemRepository.findByIsAvailableTrue();
        return items.stream()
                .map(item -> itemMarketPlaceMapper.toItemMarketPlaceDTO(item, finalUser))
                .collect(Collectors.toList());
    }

    public List<ItemMarketPlaceDTO> getMarketPlaceItemsByCategory(Long categoryId) {
        User currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception e) {
            // Continue with null user
        }

        final User finalUser = currentUser;
        
        List<Item> items = itemRepository.findByCategoryIdAndIsAvailableTrue(categoryId);
        return items.stream()
                .map(item -> itemMarketPlaceMapper.toItemMarketPlaceDTO(item, finalUser))
                .collect(Collectors.toList());
    }
}