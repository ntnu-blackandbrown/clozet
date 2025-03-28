package stud.ntnu.no.backend.Item.service;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.exception.CategoryNotFoundException;
import stud.ntnu.no.backend.category.repository.CategoryRepository;
import stud.ntnu.no.backend.Item.dto.CreateItemDTO;
import stud.ntnu.no.backend.Item.dto.ItemDTO;
import stud.ntnu.no.backend.Item.entity.Item;
import stud.ntnu.no.backend.Item.exception.ItemNotFoundException;
import stud.ntnu.no.backend.Item.exception.ItemValidationException;
import stud.ntnu.no.backend.Item.mapper.ItemMapper;
import stud.ntnu.no.backend.Item.repository.ItemRepository;
import stud.ntnu.no.backend.location.repository.LocationRepository;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ShippingOptionRepository shippingOptionRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository,
                           CategoryRepository categoryRepository,
                           UserRepository userRepository,
                           LocationRepository locationRepository,
                           ShippingOptionRepository shippingOptionRepository,
                           ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.shippingOptionRepository = shippingOptionRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<ItemDTO> getAllItems() {
        return itemMapper.toDtoList(itemRepository.findAll());
    }

    @Override
    public List<ItemDTO> getActiveItems() {
        return itemMapper.toDtoList(itemRepository.findByIsAvailableTrue());
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
    public ItemDTO createItem(CreateItemDTO itemDTO, Long userId) {
        Category category = categoryRepository.findById(itemDTO.getCategoryId())
            .orElseThrow(() -> new ItemValidationException("Category not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        Location location = locationRepository.findById(itemDTO.getLocationId())
            .orElseThrow(() -> new ItemValidationException("Location not found"));

        ShippingOption shippingOption = shippingOptionRepository.findById(itemDTO.getShippingOptionId())
            .orElseThrow(() -> new ItemValidationException("Shipping option not found"));

        validateItem(itemDTO);

        Item item = itemMapper.toEntity(itemDTO, category, user, location, shippingOption);
        item = itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDTO updateItem(Long itemId, CreateItemDTO itemDTO, Long userId) {
        Item item = findItemAndVerifyOwnership(itemId, userId);
        
        Category category = null;
        if (itemDTO.getCategoryId() != null) {
            category = categoryRepository.findById(itemDTO.getCategoryId())
                .orElseThrow(() -> new ItemValidationException("Category not found"));
        }

        Location location = null;
        if (itemDTO.getLocationId() != null) {
            location = locationRepository.findById(itemDTO.getLocationId())
                .orElseThrow(() -> new ItemValidationException("Location not found"));
        }

        ShippingOption shippingOption = null;
        if (itemDTO.getShippingOptionId() != null) {
            shippingOption = shippingOptionRepository.findById(itemDTO.getShippingOptionId())
                .orElseThrow(() -> new ItemValidationException("Shipping option not found"));
        }

        validateItem(itemDTO);
        
        itemMapper.updateEntityFromDto(item, itemDTO, category, location, shippingOption);
        item = itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    @Override
    public void deactivateItem(Long itemId, Long userId) {
        Item item = findItemAndVerifyOwnership(itemId, userId);
        item.setAvailable(false);
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
    }

    @Override
    public void activateItem(Long itemId, Long userId) {
        Item item = findItemAndVerifyOwnership(itemId, userId);
        item.setAvailable(true);
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id, Long sellerId) {
        Item item = findItemAndVerifyOwnership(id, sellerId);
        itemRepository.delete(item);
    }

    private Item findItemAndVerifyOwnership(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException(itemId));
        
        if (!item.getSeller().getId().equals(userId)) {
            throw new ItemValidationException("You don't have permission to modify this item");
        }
        
        return item;
    }

    private void validateItem(CreateItemDTO itemDTO) {
        if (StringUtils.isBlank(itemDTO.getTitle())) {
            throw new ItemValidationException("Title cannot be empty");
        }

        if (StringUtils.isBlank(itemDTO.getLongDescription())) {
            throw new ItemValidationException("Description cannot be empty");
        }

        if (itemDTO.getPrice() < 0) {
            throw new ItemValidationException("Item price cannot be negative");
        }
    }
}