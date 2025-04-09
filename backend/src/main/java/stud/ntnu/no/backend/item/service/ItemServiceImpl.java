package stud.ntnu.no.backend.item.service;

import io.micrometer.common.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.exception.CategoryNotFoundException;
import stud.ntnu.no.backend.category.repository.CategoryRepository;
import stud.ntnu.no.backend.item.dto.CreateItemDTO;
import stud.ntnu.no.backend.item.dto.ItemDTO;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.exception.ItemNotFoundException;
import stud.ntnu.no.backend.item.exception.ItemValidationException;
import stud.ntnu.no.backend.item.mapper.ItemMapper;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.location.repository.LocationRepository;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.repository.UserRepository;

/**
 * Implementation of the {@link ItemService} interface that provides business logic for managing
 * marketplace items.
 *
 * <p>This service handles operations related to items such as creation, retrieval, modification,
 * and deletion. It enforces business rules, performs validation, and coordinates with various
 * repositories to maintain data integrity.
 *
 * <p>Key responsibilities include:
 *
 * <ul>
 *   <li>Managing item lifecycle (creation, update, deactivation, activation, deletion)
 *   <li>Enforcing ownership validation to ensure users can only modify their own items
 *   <li>Validating item data before persistence
 *   <li>Coordinating with related entities like categories, users, locations, and shipping options
 * </ul>
 */
@Service
public class ItemServiceImpl implements stud.ntnu.no.backend.item.service.ItemService {

  private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

  private final ItemRepository itemRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final LocationRepository locationRepository;
  private final ShippingOptionRepository shippingOptionRepository;
  private final ItemMapper itemMapper;

  /**
   * Constructs a new ItemServiceImpl with the required dependencies.
   *
   * @param itemRepository the repository for item data access
   * @param categoryRepository the repository for category data access
   * @param userRepository the repository for user data access
   * @param locationRepository the repository for location data access
   * @param shippingOptionRepository the repository for shipping option data access
   * @param itemMapper the mapper for converting between DTOs and entities
   */
  public ItemServiceImpl(
      ItemRepository itemRepository,
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
    logger.info("Fetching all items");
    return itemMapper.toDtoList(itemRepository.findAll());
  }

  @Override
  public List<ItemDTO> getActiveItems() {
    logger.info("Fetching all active items");
    return itemMapper.toDtoList(itemRepository.findByIsAvailableTrue());
  }

  @Override
  public ItemDTO getItem(Long id) {
    logger.info("Fetching item with id: {}", id);
    Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    return itemMapper.toDto(item);
  }

  @Override
  public List<ItemDTO> getItemsBySeller(Long sellerId) {
    logger.info("Fetching items for seller with id: {}", sellerId);
    if (!userRepository.existsById(sellerId)) {
      throw new UserNotFoundException(sellerId);
    }
    return itemMapper.toDtoList(itemRepository.findBySellerId(sellerId));
  }

  @Override
  public List<ItemDTO> getItemsByCategory(Long categoryId) {
    logger.info("Fetching items for category with id: {}", categoryId);
    if (!categoryRepository.existsById(categoryId)) {
      throw new CategoryNotFoundException(categoryId);
    }
    return itemMapper.toDtoList(itemRepository.findByCategoryId(categoryId));
  }

  @Override
  public List<ItemDTO> searchItems(String query) {
    logger.info("Searching items with query: {}", query);
    if (query == null || query.trim().isEmpty()) {
      throw new ItemValidationException("Search query cannot be empty");
    }
    return itemMapper.toDtoList(itemRepository.findByTitleContainingIgnoreCase(query.trim()));
  }

  @Override
  public ItemDTO createItem(CreateItemDTO itemDTO, Long userId) {
    logger.info("Creating item: {}", itemDTO);
    Category category =
        categoryRepository
            .findById(itemDTO.getCategoryId())
            .orElseThrow(() -> new ItemValidationException("Category not found"));

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    Location location =
        locationRepository
            .findById(itemDTO.getLocationId())
            .orElseThrow(() -> new ItemValidationException("Location not found"));

    ShippingOption shippingOption =
        shippingOptionRepository
            .findById(itemDTO.getShippingOptionId())
            .orElseThrow(() -> new ItemValidationException("Shipping option not found"));

    validateItem(itemDTO);

    Item item = itemMapper.toEntity(itemDTO, category, user, location, shippingOption);
    item = itemRepository.save(item);
    return itemMapper.toDto(item);
  }

  @Override
  public ItemDTO updateItem(Long itemId, CreateItemDTO itemDTO, Long userId) {
    logger.info("Updating item: {}", itemDTO);
    Item item = findItemAndVerifyOwnership(itemId, userId);

    Category category = null;
    if (itemDTO.getCategoryId() != null) {
      category =
          categoryRepository
              .findById(itemDTO.getCategoryId())
              .orElseThrow(() -> new ItemValidationException("Category not found"));
    }

    Location location = null;
    if (itemDTO.getLocationId() != null) {
      location =
          locationRepository
              .findById(itemDTO.getLocationId())
              .orElseThrow(() -> new ItemValidationException("Location not found"));
    }

    ShippingOption shippingOption = null;
    if (itemDTO.getShippingOptionId() != null) {
      shippingOption =
          shippingOptionRepository
              .findById(itemDTO.getShippingOptionId())
              .orElseThrow(() -> new ItemValidationException("Shipping option not found"));
    }

    validateItem(itemDTO);

    itemMapper.updateEntityFromDto(item, itemDTO, category, location, shippingOption);
    item = itemRepository.save(item);
    return itemMapper.toDto(item);
  }

  @Override
  public void deactivateItem(Long itemId, Long userId) {
    logger.info("Deactivating item: {}", itemId);
    Item item = findItemAndVerifyOwnership(itemId, userId);
    item.setAvailable(false);
    item.setUpdatedAt(LocalDateTime.now());
    itemRepository.save(item);
  }

  @Override
  public void activateItem(Long itemId, Long userId) {
    logger.info("Activating item: {}", itemId);
    Item item = findItemAndVerifyOwnership(itemId, userId);
    item.setAvailable(true);
    item.setUpdatedAt(LocalDateTime.now());
    itemRepository.save(item);
  }

  @Override
  @Transactional
  public void deleteItem(Long id, Long sellerId) {
    logger.info("Deleting item: {}", id);
    Item item = findItemAndVerifyOwnership(id, sellerId);
    itemRepository.delete(item);
  }

  /**
   * Retrieves an item and verifies that the specified user is the owner.
   *
   * <p>This helper method is used by operations that modify items to ensure that only the owner can
   * make changes to an item. It first fetches the item by ID and then checks if the provided user
   * ID matches the seller ID.
   *
   * @param itemId the unique identifier of the item to find
   * @param userId the unique identifier of the user attempting to access the item
   * @return the found item if the user is the owner
   * @throws ItemNotFoundException if no item exists with the given ID
   * @throws ItemValidationException if the specified user is not the owner of the item
   */
  private Item findItemAndVerifyOwnership(Long itemId, Long userId) {
    logger.info("Finding item with id: {}", itemId);
    Item item =
        itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));

    if (!item.getSeller().getId().equals(userId)) {
      throw new ItemValidationException("You don't have permission to modify this item");
    }

    return item;
  }

  /**
   * Validates an item's data before creation or update.
   *
   * <p>This method checks that the required fields are not empty and that business rules are
   * satisfied. For example, it ensures that titles and descriptions are not blank and that prices
   * are not negative.
   *
   * @param itemDTO the data transfer object containing the item details to validate
   * @throws ItemValidationException if any validation fails
   */
  private void validateItem(CreateItemDTO itemDTO) {
    logger.info("Validating item: {}", itemDTO);
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
