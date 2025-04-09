package stud.ntnu.no.backend.item.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.category.entity.Category;
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

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

  @Mock private ItemRepository itemRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private UserRepository userRepository;

  @Mock private LocationRepository locationRepository;

  @Mock private ShippingOptionRepository shippingOptionRepository;

  @Mock private ItemMapper itemMapper;

  @InjectMocks private ItemServiceImpl itemService;

  private Item item1;
  private Item item2;
  private ItemDTO itemDTO1;
  private ItemDTO itemDTO2;
  private User seller;
  private Category category;
  private Location location;
  private ShippingOption shippingOption;
  private CreateItemDTO createItemDTO;

  @BeforeEach
  void setUp() {
    // Opprett test data
    seller = new User();
    seller.setId(1L);
    seller.setUsername("seller");
    seller.setEmail("seller@example.com");

    category = new Category();
    category.setId(1L);
    category.setName("Electronics");

    location = new Location();
    location.setId(1L);
    location.setCity("Oslo");
    location.setRegion("Oslo Region");

    shippingOption = new ShippingOption();
    shippingOption.setId(1L);
    shippingOption.setName("Standard");
    shippingOption.setPrice(99.0);

    item1 = new Item();
    item1.setId(1L);
    item1.setTitle("Smartphone");
    item1.setSeller(seller);
    item1.setCategory(category);
    item1.setLocation(location);
    item1.setShippingOption(shippingOption);
    item1.setShortDescription("New smartphone");
    item1.setLongDescription("A brand new smartphone with great features");
    item1.setPrice(5000.0);
    item1.setAvailable(true);
    item1.setCreatedAt(LocalDateTime.now());
    item1.setUpdatedAt(LocalDateTime.now());

    item2 = new Item();
    item2.setId(2L);
    item2.setTitle("Laptop");
    item2.setSeller(seller);
    item2.setCategory(category);
    item2.setLocation(location);
    item2.setShippingOption(shippingOption);
    item2.setShortDescription("New laptop");
    item2.setLongDescription("A brand new laptop with great specs");
    item2.setPrice(10000.0);
    item2.setAvailable(true);
    item2.setCreatedAt(LocalDateTime.now());
    item2.setUpdatedAt(LocalDateTime.now());

    itemDTO1 = new ItemDTO();
    itemDTO1.setId(1L);
    itemDTO1.setTitle("Smartphone");
    itemDTO1.setSellerId(1L);
    itemDTO1.setCategoryId(1L);
    itemDTO1.setPrice(5000.0);

    itemDTO2 = new ItemDTO();
    itemDTO2.setId(2L);
    itemDTO2.setTitle("Laptop");
    itemDTO2.setSellerId(1L);
    itemDTO2.setCategoryId(1L);
    itemDTO2.setPrice(10000.0);

    createItemDTO = new CreateItemDTO();
    createItemDTO.setTitle("New Item");
    createItemDTO.setCategoryId(1L);
    createItemDTO.setLocationId(1L);
    createItemDTO.setShippingOptionId(1L);
    createItemDTO.setShortDescription("Short description");
    createItemDTO.setLongDescription("Long detailed description");
    createItemDTO.setPrice(3000.0);
    createItemDTO.setCondition("New");
    createItemDTO.setSize("M");
    createItemDTO.setBrand("Brand");
    createItemDTO.setColor("Black");
    createItemDTO.setVippsPaymentEnabled(true);
  }

  @Test
  void getAllItems_shouldReturnAllItems() {
    // Arrange
    when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));
    when(itemMapper.toDtoList(anyList())).thenReturn(Arrays.asList(itemDTO1, itemDTO2));

    // Act
    List<ItemDTO> result = itemService.getAllItems();

    // Assert
    assertEquals(2, result.size());
    assertEquals("Smartphone", result.get(0).getTitle());
    assertEquals("Laptop", result.get(1).getTitle());
    verify(itemRepository, times(1)).findAll();
    verify(itemMapper, times(1)).toDtoList(anyList());
  }

  @Test
  void getActiveItems_shouldReturnActiveItems() {
    // Arrange
    when(itemRepository.findByIsAvailableTrue()).thenReturn(Arrays.asList(item1, item2));
    when(itemMapper.toDtoList(anyList())).thenReturn(Arrays.asList(itemDTO1, itemDTO2));

    // Act
    List<ItemDTO> result = itemService.getActiveItems();

    // Assert
    assertEquals(2, result.size());
    verify(itemRepository, times(1)).findByIsAvailableTrue();
    verify(itemMapper, times(1)).toDtoList(anyList());
  }

  @Test
  void getItem_withValidId_shouldReturnItem() {
    // Arrange
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
    when(itemMapper.toDto(item1)).thenReturn(itemDTO1);

    // Act
    ItemDTO result = itemService.getItem(1L);

    // Assert
    assertNotNull(result);
    assertEquals("Smartphone", result.getTitle());
    verify(itemRepository, times(1)).findById(1L);
    verify(itemMapper, times(1)).toDto(item1);
  }

  @Test
  void getItem_withInvalidId_shouldThrowException() {
    // Arrange
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ItemNotFoundException.class,
        () -> {
          itemService.getItem(99L);
        });
    verify(itemRepository, times(1)).findById(99L);
  }

  @Test
  void getItemsBySeller_withValidId_shouldReturnItems() {
    // Arrange
    when(userRepository.existsById(1L)).thenReturn(true);
    when(itemRepository.findBySellerId(1L)).thenReturn(Arrays.asList(item1, item2));
    when(itemMapper.toDtoList(anyList())).thenReturn(Arrays.asList(itemDTO1, itemDTO2));

    // Act
    List<ItemDTO> result = itemService.getItemsBySeller(1L);

    // Assert
    assertEquals(2, result.size());
    verify(userRepository, times(1)).existsById(1L);
    verify(itemRepository, times(1)).findBySellerId(1L);
    verify(itemMapper, times(1)).toDtoList(anyList());
  }

  @Test
  void getItemsBySeller_withInvalidId_shouldThrowException() {
    // Arrange
    when(userRepository.existsById(99L)).thenReturn(false);

    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          itemService.getItemsBySeller(99L);
        });
    verify(userRepository, times(1)).existsById(99L);
    verify(itemRepository, never()).findBySellerId(anyLong());
  }

  @Test
  void createItem_withValidData_shouldReturnCreatedItem() {
    // Arrange
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
    when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
    when(shippingOptionRepository.findById(1L)).thenReturn(Optional.of(shippingOption));

    Item newItem = new Item();
    newItem.setTitle("New Item");
    newItem.setPrice(3000.0);

    when(itemMapper.toEntity(
            eq(createItemDTO), eq(category), eq(seller), eq(location), eq(shippingOption)))
        .thenReturn(newItem);
    when(itemRepository.save(newItem)).thenReturn(newItem);
    when(itemMapper.toDto(newItem)).thenReturn(itemDTO1);

    // Act
    ItemDTO result = itemService.createItem(createItemDTO, 1L);

    // Assert
    assertNotNull(result);
    verify(categoryRepository, times(1)).findById(1L);
    verify(userRepository, times(1)).findById(1L);
    verify(locationRepository, times(1)).findById(1L);
    verify(shippingOptionRepository, times(1)).findById(1L);
    verify(itemMapper, times(1))
        .toEntity(eq(createItemDTO), eq(category), eq(seller), eq(location), eq(shippingOption));
    verify(itemRepository, times(1)).save(newItem);
    verify(itemMapper, times(1)).toDto(newItem);
  }

  @Test
  void createItem_withInvalidData_shouldThrowException() {
    // Arrange
    CreateItemDTO invalidDTO = new CreateItemDTO();
    invalidDTO.setTitle(""); // Invalid title
    invalidDTO.setCategoryId(1L);
    invalidDTO.setLocationId(1L);
    invalidDTO.setShippingOptionId(1L);
    invalidDTO.setPrice(3000.0);

    // Act & Assert
    assertThrows(
        ItemValidationException.class,
        () -> {
          itemService.createItem(invalidDTO, 1L);
        });
  }

  @Test
  void deactivateItem_withValidId_shouldDeactivateItem() {
    // Arrange
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
    when(itemRepository.save(item1)).thenReturn(item1);

    // Act
    itemService.deactivateItem(1L, 1L);

    // Assert
    assertFalse(item1.isAvailable());
    verify(itemRepository, times(1)).findById(1L);
    verify(itemRepository, times(1)).save(item1);
  }

  @Test
  void activateItem_withValidId_shouldActivateItem() {
    // Arrange
    item1.setAvailable(false);
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
    when(itemRepository.save(item1)).thenReturn(item1);

    // Act
    itemService.activateItem(1L, 1L);

    // Assert
    assertTrue(item1.isAvailable());
    verify(itemRepository, times(1)).findById(1L);
    verify(itemRepository, times(1)).save(item1);
  }

  @Test
  void deleteItem_withValidId_shouldDeleteItem() {
    // Arrange
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
    doNothing().when(itemRepository).delete(item1);

    // Act
    itemService.deleteItem(1L, 1L);

    // Assert
    verify(itemRepository, times(1)).findById(1L);
    verify(itemRepository, times(1)).delete(item1);
  }

  @Test
  void deleteItem_withInvalidId_shouldThrowException() {
    // Arrange
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ItemNotFoundException.class,
        () -> {
          itemService.deleteItem(99L, 1L);
        });
    verify(itemRepository, times(1)).findById(99L);
    verify(itemRepository, never()).delete(any(Item.class));
  }

  @Test
  void deleteItem_withWrongSellerId_shouldThrowException() {
    // Arrange
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

    // Act & Assert
    assertThrows(
        ItemValidationException.class,
        () -> {
          itemService.deleteItem(1L, 2L); // Wrong seller ID
        });
    verify(itemRepository, times(1)).findById(1L);
    verify(itemRepository, never()).delete(any(Item.class));
  }
}
