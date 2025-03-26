package stud.ntnu.no.backend.Item.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.Category.Entity.Category;
import stud.ntnu.no.backend.Category.Exceptions.CategoryNotFoundException;
import stud.ntnu.no.backend.Category.Repository.CategoryRepository;
import stud.ntnu.no.backend.Item.DTOs.CreateItemDTO;
import stud.ntnu.no.backend.Item.DTOs.ItemDTO;
import stud.ntnu.no.backend.Item.Entity.Item;
import stud.ntnu.no.backend.Item.Exceptions.ItemNotFoundException;
import stud.ntnu.no.backend.Item.Exceptions.ItemValidationException;
import stud.ntnu.no.backend.Item.Mapper.ItemMapper;
import stud.ntnu.no.backend.Item.Repository.ItemRepository;
import stud.ntnu.no.backend.User.Entity.User;
import stud.ntnu.no.backend.User.Exceptions.UserNotFoundException;
import stud.ntnu.no.backend.User.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, 
                          CategoryRepository categoryRepository,
                          UserRepository userRepository,
                          ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<ItemDTO> getAllItems() {
        return itemMapper.toDtoList(itemRepository.findAll());
    }

    @Override
    public List<ItemDTO> getActiveItems() {
        return itemMapper.toDtoList(itemRepository.findByActiveTrue());
    }

    @Override
    public ItemDTO getItem(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException(id));
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDTO> getItemsBySeller(Long sellerId) {
        if (!userRepository.existsById(sellerId)) {
            throw new UserNotFoundException(sellerId);
        }
        return itemMapper.toDtoList(itemRepository.findBySellerId(sellerId));
    }

    @Override
    public List<ItemDTO> getItemsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        return itemMapper.toDtoList(itemRepository.findByCategoryId(categoryId));
    }

    @Override
    public List<ItemDTO> searchItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ItemValidationException("Search query cannot be empty");
        }
        return itemMapper.toDtoList(itemRepository.findByTitleContainingIgnoreCase(query.trim()));
    }

    @Override
    @Transactional
    public ItemDTO createItem(CreateItemDTO itemDTO, Long sellerId) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new UserNotFoundException(sellerId));
            
        Category category = categoryRepository.findById(itemDTO.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException(itemDTO.getCategoryId()));
            
        validateItem(itemDTO);
            
        Item item = itemMapper.toEntity(itemDTO, category, seller);
        Item savedItem = itemRepository.save(item);
        
        return itemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDTO updateItem(Long id, CreateItemDTO itemDTO, Long sellerId) {
        Item existingItem = itemRepository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException(id));
            
        if (!existingItem.getSeller().getId().equals(sellerId)) {
            throw new ItemValidationException("You can only update your own items");
        }
            
        Category category = categoryRepository.findById(itemDTO.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException(itemDTO.getCategoryId()));
            
        validateItem(itemDTO);
            
        existingItem.setTitle(itemDTO.getTitle());
        existingItem.setDescription(itemDTO.getDescription());
        existingItem.setPrice(itemDTO.getPrice());
        existingItem.setCategory(category);
        existingItem.setImageUrl(itemDTO.getImageUrl());
        existingItem.setUpdatedAt(LocalDateTime.now());
        
        Item updatedItem = itemRepository.save(existingItem);
        
        return itemMapper.toDto(updatedItem);
    }

    @Override
    @Transactional
    public void deactivateItem(Long id, Long sellerId) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException(id));
            
        if (!item.getSeller().getId().equals(sellerId)) {
            throw new ItemValidationException("You can only deactivate your own items");
        }
            
        item.setActive(false);
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void activateItem(Long id, Long sellerId) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException(id));
            
        if (!item.getSeller().getId().equals(sellerId)) {
            throw new ItemValidationException("You can only activate your own items");
        }
            
        item.setActive(true);
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id, Long sellerId) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ItemNotFoundException(id));
            
        if (!item.getSeller().getId().equals(sellerId)) {
            throw new ItemValidationException("You can only delete your own items");
        }
            
        itemRepository.delete(item);
    }

    private void validateItem(CreateItemDTO itemDTO) {
        if (itemDTO.getTitle() == null || itemDTO.getTitle().trim().isEmpty()) {
            throw new ItemValidationException("Item title cannot be empty");
        }
        
        if (itemDTO.getPrice() == null || itemDTO.getPrice().doubleValue() <= 0) {
            throw new ItemValidationException("Item price must be positive");
        }
    }
}