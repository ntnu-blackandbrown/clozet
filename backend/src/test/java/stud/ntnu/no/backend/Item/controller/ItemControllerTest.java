package stud.ntnu.no.backend.Item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import stud.ntnu.no.backend.Item.dto.CreateItemDTO;
import stud.ntnu.no.backend.Item.dto.ItemDTO;
import stud.ntnu.no.backend.Item.service.ItemService;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ItemService itemService;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private User mockUser;

    @InjectMocks
    private ItemController itemController;

    private ItemDTO testItemDTO;
    private CreateItemDTO testCreateItemDTO;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fixedTime = LocalDateTime.now();
        
        // Setup security context mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(1L);

        // Setup test item DTO
        testItemDTO = new ItemDTO();
        testItemDTO.setId(1L);
        testItemDTO.setTitle("Test Item");
        testItemDTO.setShortDescription("Short test description");
        testItemDTO.setLongDescription("Long test description");
        testItemDTO.setPrice(100.0);
        testItemDTO.setCategoryId(1L);
        testItemDTO.setCategoryName("Test Category");
        testItemDTO.setSellerId(1L);
        testItemDTO.setSellerName("testUser");
        testItemDTO.setLocationId(1L);
        testItemDTO.setLocationName("Test Location");
        testItemDTO.setShippingOptionId(1L);
        testItemDTO.setShippingOptionName("Standard Shipping");
        testItemDTO.setLatitude(63.4305);
        testItemDTO.setLongitude(10.3951);
        testItemDTO.setCondition("New");
        testItemDTO.setSize("M");
        testItemDTO.setBrand("Test Brand");
        testItemDTO.setColor("Red");
        testItemDTO.setAvailable(true);
        testItemDTO.setVippsPaymentEnabled(true);
        testItemDTO.setCreatedAt(fixedTime);
        testItemDTO.setUpdatedAt(fixedTime);

        // Setup create item DTO
        testCreateItemDTO = new CreateItemDTO();
        testCreateItemDTO.setTitle("New Item");
        testCreateItemDTO.setShortDescription("Short description");
        testCreateItemDTO.setLongDescription("Long description");
        testCreateItemDTO.setPrice(200.0);
        testCreateItemDTO.setCategoryId(1L);
        testCreateItemDTO.setLocationId(1L);
        testCreateItemDTO.setShippingOptionId(1L);
        testCreateItemDTO.setLatitude(63.4305);
        testCreateItemDTO.setLongitude(10.3951);
        testCreateItemDTO.setCondition("New");
        testCreateItemDTO.setSize("M");
        testCreateItemDTO.setBrand("Test Brand");
        testCreateItemDTO.setColor("Red");
        testCreateItemDTO.setVippsPaymentEnabled(true);
    }

    @Test
    void getAllItems_shouldReturnActiveItems() {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        when(itemService.getActiveItems()).thenReturn(items);

        // Act
        ResponseEntity<List<ItemDTO>> response = itemController.getAllItems();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Item", response.getBody().get(0).getTitle());
        verify(itemService, times(1)).getActiveItems();
    }

    @Test
    void getAllItemsIncludingInactive_shouldReturnAllItems() {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        when(itemService.getAllItems()).thenReturn(items);

        // Act
        ResponseEntity<List<ItemDTO>> response = itemController.getAllItemsIncludingInactive();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(true, response.getBody().get(0).isAvailable());
        verify(itemService, times(1)).getAllItems();
    }

    @Test
    void getItem_shouldReturnItemById() {
        // Arrange
        when(itemService.getItem(1L)).thenReturn(testItemDTO);

        // Act
        ResponseEntity<ItemDTO> response = itemController.getItem(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Item", response.getBody().getTitle());
        assertEquals(100.0, response.getBody().getPrice());
        verify(itemService, times(1)).getItem(1L);
    }

    @Test
    void getItemsBySeller_shouldReturnSellerItems() {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        when(itemService.getItemsBySeller(1L)).thenReturn(items);

        // Act
        ResponseEntity<List<ItemDTO>> response = itemController.getItemsBySeller(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getSellerId());
        verify(itemService, times(1)).getItemsBySeller(1L);
    }

    @Test
    void getItemsByCategory_shouldReturnCategoryItems() {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        when(itemService.getItemsByCategory(1L)).thenReturn(items);

        // Act
        ResponseEntity<List<ItemDTO>> response = itemController.getItemsByCategory(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getCategoryId());
        verify(itemService, times(1)).getItemsByCategory(1L);
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        String query = "test";
        when(itemService.searchItems(query)).thenReturn(items);

        // Act
        ResponseEntity<List<ItemDTO>> response = itemController.searchItems(query);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Item", response.getBody().get(0).getTitle());
        verify(itemService, times(1)).searchItems(query);
    }

    @Test
    void createItem_shouldCreateAndReturnItem() {
        // Arrange
        when(itemService.createItem(any(CreateItemDTO.class), eq(1L))).thenReturn(testItemDTO);

        // Act
        ResponseEntity<ItemDTO> response = itemController.createItem(testCreateItemDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Item", response.getBody().getTitle());
        verify(itemService, times(1)).createItem(any(CreateItemDTO.class), eq(1L));
    }

    @Test
    void updateItem_shouldUpdateAndReturnItem() {
        // Arrange
        when(itemService.updateItem(eq(1L), any(CreateItemDTO.class), eq(1L))).thenReturn(testItemDTO);

        // Act
        ResponseEntity<ItemDTO> response = itemController.updateItem(1L, testCreateItemDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Item", response.getBody().getTitle());
        verify(itemService, times(1)).updateItem(eq(1L), any(CreateItemDTO.class), eq(1L));
    }

    @Test
    void deactivateItem_shouldDeactivateItem() {
        // Arrange
        doNothing().when(itemService).deactivateItem(eq(1L), eq(1L));

        // Act
        ResponseEntity<Void> response = itemController.deactivateItem(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemService, times(1)).deactivateItem(eq(1L), eq(1L));
    }

    @Test
    void activateItem_shouldActivateItem() {
        // Arrange
        doNothing().when(itemService).activateItem(eq(1L), eq(1L));

        // Act
        ResponseEntity<Void> response = itemController.activateItem(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemService, times(1)).activateItem(eq(1L), eq(1L));
    }

    @Test
    void deleteItem_shouldDeleteItem() {
        // Arrange
        doNothing().when(itemService).deleteItem(eq(1L), eq(1L));

        // Act
        ResponseEntity<Void> response = itemController.deleteItem(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemService, times(1)).deleteItem(eq(1L), eq(1L));
    }
}