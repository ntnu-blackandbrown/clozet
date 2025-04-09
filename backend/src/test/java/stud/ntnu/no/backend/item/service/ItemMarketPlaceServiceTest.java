package stud.ntnu.no.backend.item.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.item.dto.ItemMarketPlaceDTO;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.mapper.ItemMarketPlaceMapper;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ItemMarketPlaceServiceTest {

  @Mock private ItemRepository itemRepository;

  @Mock private ItemMarketPlaceMapper itemMarketPlaceMapper;

  @Mock private UserService userService;

  @InjectMocks private ItemMarketPlaceService itemMarketPlaceService;

  private Item item1;
  private Item item2;
  private User currentUser;
  private ItemMarketPlaceDTO itemDTO1;
  private ItemMarketPlaceDTO itemDTO2;

  @BeforeEach
  void setUp() {
    // Opprett test data
    currentUser = new User();
    currentUser.setId(1L);
    currentUser.setUsername("testuser");

    item1 = new Item();
    item1.setId(1L);
    item1.setTitle("Item 1");
    item1.setAvailable(true);

    item2 = new Item();
    item2.setId(2L);
    item2.setTitle("Item 2");
    item2.setAvailable(true);

    itemDTO1 = new ItemMarketPlaceDTO();
    itemDTO1.setId(1L);
    itemDTO1.setTitle("Item 1");

    itemDTO2 = new ItemMarketPlaceDTO();
    itemDTO2.setId(2L);
    itemDTO2.setTitle("Item 2");
  }

  @Test
  void getAllMarketPlaceItems_withAuthenticatedUser_shouldReturnAllItems() {
    // Arrange
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(itemRepository.findByIsAvailableTrue()).thenReturn(Arrays.asList(item1, item2));
    when(itemMarketPlaceMapper.toItemMarketPlaceDTO(item1, currentUser)).thenReturn(itemDTO1);
    when(itemMarketPlaceMapper.toItemMarketPlaceDTO(item2, currentUser)).thenReturn(itemDTO2);

    // Act
    List<ItemMarketPlaceDTO> result = itemMarketPlaceService.getAllMarketPlaceItems();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Item 1", result.get(0).getTitle());
    assertEquals("Item 2", result.get(1).getTitle());
    verify(userService, times(1)).getCurrentUser();
    verify(itemRepository, times(1)).findByIsAvailableTrue();
    verify(itemMarketPlaceMapper, times(1)).toItemMarketPlaceDTO(item1, currentUser);
    verify(itemMarketPlaceMapper, times(1)).toItemMarketPlaceDTO(item2, currentUser);
  }

  @Test
  void getAllMarketPlaceItems_withoutAuthenticatedUser_shouldReturnAllItems() {
    // Arrange
    when(userService.getCurrentUser()).thenThrow(new RuntimeException("User not authenticated"));
    when(itemRepository.findByIsAvailableTrue()).thenReturn(Arrays.asList(item1, item2));
    when(itemMarketPlaceMapper.toItemMarketPlaceDTO(item1, null)).thenReturn(itemDTO1);
    when(itemMarketPlaceMapper.toItemMarketPlaceDTO(item2, null)).thenReturn(itemDTO2);

    // Act
    List<ItemMarketPlaceDTO> result = itemMarketPlaceService.getAllMarketPlaceItems();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Item 1", result.get(0).getTitle());
    assertEquals("Item 2", result.get(1).getTitle());
    verify(userService, times(1)).getCurrentUser();
    verify(itemRepository, times(1)).findByIsAvailableTrue();
    verify(itemMarketPlaceMapper, times(1)).toItemMarketPlaceDTO(item1, null);
    verify(itemMarketPlaceMapper, times(1)).toItemMarketPlaceDTO(item2, null);
  }

  @Test
  void getMarketPlaceItemsByCategory_withAuthenticatedUser_shouldReturnCategoryItems() {
    // Arrange
    Long categoryId = 1L;
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(itemRepository.findByCategoryIdAndIsAvailableTrue(categoryId))
        .thenReturn(Arrays.asList(item1));
    when(itemMarketPlaceMapper.toItemMarketPlaceDTO(item1, currentUser)).thenReturn(itemDTO1);

    // Act
    List<ItemMarketPlaceDTO> result =
        itemMarketPlaceService.getMarketPlaceItemsByCategory(categoryId);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Item 1", result.get(0).getTitle());
    verify(userService, times(1)).getCurrentUser();
    verify(itemRepository, times(1)).findByCategoryIdAndIsAvailableTrue(categoryId);
    verify(itemMarketPlaceMapper, times(1)).toItemMarketPlaceDTO(item1, currentUser);
  }

  @Test
  void getMarketPlaceItemsByCategory_withoutAuthenticatedUser_shouldReturnCategoryItems() {
    // Arrange
    Long categoryId = 1L;
    when(userService.getCurrentUser()).thenThrow(new RuntimeException("User not authenticated"));
    when(itemRepository.findByCategoryIdAndIsAvailableTrue(categoryId))
        .thenReturn(Arrays.asList(item1));
    when(itemMarketPlaceMapper.toItemMarketPlaceDTO(item1, null)).thenReturn(itemDTO1);

    // Act
    List<ItemMarketPlaceDTO> result =
        itemMarketPlaceService.getMarketPlaceItemsByCategory(categoryId);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Item 1", result.get(0).getTitle());
    verify(userService, times(1)).getCurrentUser();
    verify(itemRepository, times(1)).findByCategoryIdAndIsAvailableTrue(categoryId);
    verify(itemMarketPlaceMapper, times(1)).toItemMarketPlaceDTO(item1, null);
  }
}
