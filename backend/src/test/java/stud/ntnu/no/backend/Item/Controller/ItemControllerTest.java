package stud.ntnu.no.backend.Item.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.no.backend.Item.DTOs.CreateItemDTO;
import stud.ntnu.no.backend.Item.DTOs.ItemDTO;
import stud.ntnu.no.backend.Item.Service.ItemService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private ItemDTO testItemDTO;
    private CreateItemDTO testCreateItemDTO;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        fixedTime = LocalDateTime.now();

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
    void getAllItems_shouldReturnActiveItems() throws Exception {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        given(itemService.getActiveItems()).willReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Item"));
    }

    @Test
    void getAllItemsIncludingInactive_shouldReturnAllItems() throws Exception {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        given(itemService.getAllItems()).willReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/items/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void getItem_shouldReturnItemById() throws Exception {
        // Arrange
        given(itemService.getItem(1L)).willReturn(testItemDTO);

        // Act & Assert
        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Item"))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    void getItemsBySeller_shouldReturnSellerItems() throws Exception {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        given(itemService.getItemsBySeller(1L)).willReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/items/seller/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sellerId").value(1));
    }

    @Test
    void getItemsByCategory_shouldReturnCategoryItems() throws Exception {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        given(itemService.getItemsByCategory(1L)).willReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/items/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoryId").value(1));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() throws Exception {
        // Arrange
        List<ItemDTO> items = Arrays.asList(testItemDTO);
        String query = "test";
        given(itemService.searchItems(query)).willReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/items/search")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Item"));
    }

    @Test
    void createItem_shouldCreateAndReturnItem() throws Exception {
        // Arrange
        given(itemService.createItem(any(CreateItemDTO.class), any(Long.class))).willReturn(testItemDTO);

        // Act & Assert
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateItemDTO))
                .with(request -> {
                    request.setUserPrincipal(() -> "testUser");
                    return request;
                }))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Item"));
    }

    @Test
    void updateItem_shouldUpdateAndReturnItem() throws Exception {
        // Arrange
        given(itemService.updateItem(eq(1L), any(CreateItemDTO.class), any(Long.class))).willReturn(testItemDTO);

        // Act & Assert
        mockMvc.perform(put("/api/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateItemDTO))
                .with(request -> {
                    request.setUserPrincipal(() -> "testUser");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Item"));
    }

    @Test
    void deactivateItem_shouldDeactivateItem() throws Exception {
        // Arrange
        doNothing().when(itemService).deactivateItem(eq(1L), any(Long.class));

        // Act & Assert
        mockMvc.perform(put("/api/items/1/deactivate")
                .with(request -> {
                    request.setUserPrincipal(() -> "testUser");
                    return request;
                }))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateItem_shouldActivateItem() throws Exception {
        // Arrange
        doNothing().when(itemService).activateItem(eq(1L), any(Long.class));

        // Act & Assert
        mockMvc.perform(put("/api/items/1/activate")
                .with(request -> {
                    request.setUserPrincipal(() -> "testUser");
                    return request;
                }))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteItem_shouldDeleteItem() throws Exception {
        // Arrange
        doNothing().when(itemService).deleteItem(eq(1L), any(Long.class));

        // Act & Assert
        mockMvc.perform(delete("/api/items/1")
                .with(request -> {
                    request.setUserPrincipal(() -> "testUser");
                    return request;
                }))
                .andExpect(status().isNoContent());
    }
}