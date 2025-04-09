package stud.ntnu.no.backend.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.no.backend.common.security.model.CustomUserDetails;
import stud.ntnu.no.backend.item.dto.CreateItemDTO;
import stud.ntnu.no.backend.item.dto.ItemDTO;
import stud.ntnu.no.backend.item.service.ItemService;
import stud.ntnu.no.backend.user.entity.User;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemControllerTest {

  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock private ItemService itemService;

  @Mock private Authentication authentication;

  @Mock private CustomUserDetails userDetails;

  @Mock private User user;

  @InjectMocks private ItemController itemController;

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {
    // Setup authentication with mock user
    when(user.getId()).thenReturn(123L);
    when(userDetails.getUser()).thenReturn(user);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mockMvc =
        MockMvcBuilders.standaloneSetup(itemController)
            .apply(
                documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint()))
            .build();
  }

  @Test
  void getAllItems_ShouldReturnActiveItems() throws Exception {
    // Given
    ItemDTO item1 = new ItemDTO();
    item1.setId(1L);
    item1.setTitle("Item 1");

    ItemDTO item2 = new ItemDTO();
    item2.setId(2L);
    item2.setTitle("Item 2");

    List<ItemDTO> items = Arrays.asList(item1, item2);

    when(itemService.getActiveItems()).thenReturn(items);

    // When/Then
    mockMvc
        .perform(get("/api/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Item 1"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].title").value("Item 2"))
        .andDo(
            document(
                "items-get-all-active",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("[].id").description("Item ID"),
                    fieldWithPath("[].title").description("Item title"),
                    fieldWithPath("[].shortDescription")
                        .description("Short description of the item"),
                    fieldWithPath("[].longDescription")
                        .description("Detailed description of the item"),
                    fieldWithPath("[].price").description("Item price"),
                    fieldWithPath("[].categoryId").description("Category ID"),
                    fieldWithPath("[].categoryName").description("Category name"),
                    fieldWithPath("[].sellerId").description("Seller ID"),
                    fieldWithPath("[].sellerName").description("Seller name"),
                    fieldWithPath("[].locationId").description("Location ID"),
                    fieldWithPath("[].locationName").description("Location name"),
                    fieldWithPath("[].shippingOptionId").description("Shipping option ID"),
                    fieldWithPath("[].shippingOptionName").description("Shipping option name"),
                    fieldWithPath("[].latitude").description("Latitude coordinate"),
                    fieldWithPath("[].longitude").description("Longitude coordinate"),
                    fieldWithPath("[].condition").description("Item condition"),
                    fieldWithPath("[].size").description("Item size"),
                    fieldWithPath("[].brand").description("Item brand"),
                    fieldWithPath("[].color").description("Item color"),
                    fieldWithPath("[].createdAt").description("Creation date and time"),
                    fieldWithPath("[].updatedAt").description("Last update date and time"),
                    fieldWithPath("[].images").description("Item images"),
                    fieldWithPath("[].available").description("Item availability status"),
                    fieldWithPath("[].vippsPaymentEnabled")
                        .description("Whether Vipps payment is enabled"))));

    verify(itemService).getActiveItems();
  }

  @Test
  void getAllItemsIncludingInactive_ShouldReturnAllItems() throws Exception {
    // Given
    ItemDTO activeItem = new ItemDTO();
    activeItem.setId(1L);
    activeItem.setTitle("Active Item");

    ItemDTO inactiveItem = new ItemDTO();
    inactiveItem.setId(2L);
    inactiveItem.setTitle("Inactive Item");

    List<ItemDTO> items = Arrays.asList(activeItem, inactiveItem);

    when(itemService.getAllItems()).thenReturn(items);

    // When/Then
    mockMvc
        .perform(get("/api/items/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Active Item"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].title").value("Inactive Item"))
        .andDo(
            document(
                "items-get-all-including-inactive",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("[].id").description("Item ID"),
                    fieldWithPath("[].title").description("Item title"),
                    fieldWithPath("[].shortDescription")
                        .description("Short description of the item"),
                    fieldWithPath("[].longDescription")
                        .description("Detailed description of the item"),
                    fieldWithPath("[].price").description("Item price"),
                    fieldWithPath("[].categoryId").description("Category ID"),
                    fieldWithPath("[].categoryName").description("Category name"),
                    fieldWithPath("[].sellerId").description("Seller ID"),
                    fieldWithPath("[].sellerName").description("Seller name"),
                    fieldWithPath("[].locationId").description("Location ID"),
                    fieldWithPath("[].locationName").description("Location name"),
                    fieldWithPath("[].shippingOptionId").description("Shipping option ID"),
                    fieldWithPath("[].shippingOptionName").description("Shipping option name"),
                    fieldWithPath("[].latitude").description("Latitude coordinate"),
                    fieldWithPath("[].longitude").description("Longitude coordinate"),
                    fieldWithPath("[].condition").description("Item condition"),
                    fieldWithPath("[].size").description("Item size"),
                    fieldWithPath("[].brand").description("Item brand"),
                    fieldWithPath("[].color").description("Item color"),
                    fieldWithPath("[].createdAt").description("Creation date and time"),
                    fieldWithPath("[].updatedAt").description("Last update date and time"),
                    fieldWithPath("[].images").description("Item images"),
                    fieldWithPath("[].available").description("Item availability status"),
                    fieldWithPath("[].vippsPaymentEnabled")
                        .description("Whether Vipps payment is enabled"))));

    verify(itemService).getAllItems();
  }

  @Test
  void getItem_ShouldReturnItemById() throws Exception {
    // Given
    Long itemId = 1L;

    ItemDTO item = new ItemDTO();
    item.setId(itemId);
    item.setTitle("Test Item");

    when(itemService.getItem(itemId)).thenReturn(item);

    // When/Then
    mockMvc
        .perform(get("/api/items/" + itemId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Test Item"))
        .andDo(
            document(
                "items-get-by-id",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("id").description("Item ID"),
                    fieldWithPath("title").description("Item title"),
                    fieldWithPath("shortDescription").description("Short description of the item"),
                    fieldWithPath("longDescription")
                        .description("Detailed description of the item"),
                    fieldWithPath("price").description("Item price"),
                    fieldWithPath("categoryId").description("Category ID"),
                    fieldWithPath("categoryName").description("Category name"),
                    fieldWithPath("sellerId").description("Seller ID"),
                    fieldWithPath("sellerName").description("Seller name"),
                    fieldWithPath("locationId").description("Location ID"),
                    fieldWithPath("locationName").description("Location name"),
                    fieldWithPath("shippingOptionId").description("Shipping option ID"),
                    fieldWithPath("shippingOptionName").description("Shipping option name"),
                    fieldWithPath("latitude").description("Latitude coordinate"),
                    fieldWithPath("longitude").description("Longitude coordinate"),
                    fieldWithPath("condition").description("Item condition"),
                    fieldWithPath("size").description("Item size"),
                    fieldWithPath("brand").description("Item brand"),
                    fieldWithPath("color").description("Item color"),
                    fieldWithPath("createdAt").description("Creation date and time"),
                    fieldWithPath("updatedAt").description("Last update date and time"),
                    fieldWithPath("images").description("Item images"),
                    fieldWithPath("available").description("Item availability status"),
                    fieldWithPath("vippsPaymentEnabled")
                        .description("Whether Vipps payment is enabled"))));

    verify(itemService).getItem(itemId);
  }

  @Test
  void getItemsBySeller_ShouldReturnSellerItems() throws Exception {
    // Given
    Long sellerId = 123L;

    ItemDTO item1 = new ItemDTO();
    item1.setId(1L);
    item1.setTitle("Seller Item 1");

    ItemDTO item2 = new ItemDTO();
    item2.setId(2L);
    item2.setTitle("Seller Item 2");

    List<ItemDTO> items = Arrays.asList(item1, item2);

    when(itemService.getItemsBySeller(sellerId)).thenReturn(items);

    // When/Then
    mockMvc
        .perform(get("/api/items/seller/" + sellerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Seller Item 1"))
        .andDo(
            document(
                "items-get-by-seller",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("[].id").description("Item ID"),
                    fieldWithPath("[].title").description("Item title"),
                    fieldWithPath("[].shortDescription")
                        .description("Short description of the item"),
                    fieldWithPath("[].longDescription")
                        .description("Detailed description of the item"),
                    fieldWithPath("[].price").description("Item price"),
                    fieldWithPath("[].categoryId").description("Category ID"),
                    fieldWithPath("[].categoryName").description("Category name"),
                    fieldWithPath("[].sellerId").description("Seller ID"),
                    fieldWithPath("[].sellerName").description("Seller name"),
                    fieldWithPath("[].locationId").description("Location ID"),
                    fieldWithPath("[].locationName").description("Location name"),
                    fieldWithPath("[].shippingOptionId").description("Shipping option ID"),
                    fieldWithPath("[].shippingOptionName").description("Shipping option name"),
                    fieldWithPath("[].latitude").description("Latitude coordinate"),
                    fieldWithPath("[].longitude").description("Longitude coordinate"),
                    fieldWithPath("[].condition").description("Item condition"),
                    fieldWithPath("[].size").description("Item size"),
                    fieldWithPath("[].brand").description("Item brand"),
                    fieldWithPath("[].color").description("Item color"),
                    fieldWithPath("[].createdAt").description("Creation date and time"),
                    fieldWithPath("[].updatedAt").description("Last update date and time"),
                    fieldWithPath("[].images").description("Item images"),
                    fieldWithPath("[].available").description("Item availability status"),
                    fieldWithPath("[].vippsPaymentEnabled")
                        .description("Whether Vipps payment is enabled"))));

    verify(itemService).getItemsBySeller(sellerId);
  }

  @Test
  void createItem_ShouldReturnCreatedItem() throws Exception {
    // Given
    CreateItemDTO createItemDTO = new CreateItemDTO();
    createItemDTO.setTitle("New Item");
    createItemDTO.setCategoryId(1L);
    createItemDTO.setShortDescription("Short description");
    createItemDTO.setLongDescription("Long description for the item");
    createItemDTO.setPrice(99.99);
    createItemDTO.setLocationId(1L);
    createItemDTO.setCondition("NEW");
    createItemDTO.setShippingOptionId(1L);
    createItemDTO.setLatitude(59.9171);
    createItemDTO.setLongitude(10.7274);
    createItemDTO.setSize("M");
    createItemDTO.setBrand("Test Brand");
    createItemDTO.setColor("Blue");

    ItemDTO createdItem = new ItemDTO();
    createdItem.setId(1L);
    createdItem.setTitle("New Item");
    createdItem.setShortDescription("Short description");
    createdItem.setPrice(99.99);

    when(itemService.createItem(any(CreateItemDTO.class), anyLong())).thenReturn(createdItem);

    // When/Then
    mockMvc
        .perform(
            post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createItemDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("New Item"))
        .andDo(
            document(
                "items-create",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").description("Item title"),
                    fieldWithPath("categoryId").description("Category ID"),
                    fieldWithPath("shortDescription").description("Short description of the item"),
                    fieldWithPath("longDescription")
                        .description("Detailed description of the item"),
                    fieldWithPath("price").description("Item price"),
                    fieldWithPath("locationId").description("Location ID"),
                    fieldWithPath("condition").description("Item condition"),
                    fieldWithPath("shippingOptionId").description("Shipping option ID"),
                    fieldWithPath("latitude").description("Latitude coordinate"),
                    fieldWithPath("longitude").description("Longitude coordinate"),
                    fieldWithPath("size").description("Item size"),
                    fieldWithPath("brand").description("Item brand"),
                    fieldWithPath("color").description("Item color"),
                    fieldWithPath("vippsPaymentEnabled")
                        .description("Whether Vipps payment is enabled")
                        .optional(),
                    fieldWithPath("images").description("Item images").optional()),
                responseFields(
                    fieldWithPath("id").description("Item ID"),
                    fieldWithPath("title").description("Item title"),
                    fieldWithPath("shortDescription").description("Short description of the item"),
                    fieldWithPath("longDescription")
                        .description("Detailed description of the item"),
                    fieldWithPath("price").description("Item price"),
                    fieldWithPath("categoryId").description("Category ID"),
                    fieldWithPath("categoryName").description("Category name"),
                    fieldWithPath("sellerId").description("Seller ID"),
                    fieldWithPath("sellerName").description("Seller name"),
                    fieldWithPath("locationId").description("Location ID"),
                    fieldWithPath("locationName").description("Location name"),
                    fieldWithPath("shippingOptionId").description("Shipping option ID"),
                    fieldWithPath("shippingOptionName").description("Shipping option name"),
                    fieldWithPath("latitude").description("Latitude coordinate"),
                    fieldWithPath("longitude").description("Longitude coordinate"),
                    fieldWithPath("condition").description("Item condition"),
                    fieldWithPath("size").description("Item size"),
                    fieldWithPath("brand").description("Item brand"),
                    fieldWithPath("color").description("Item color"),
                    fieldWithPath("createdAt").description("Creation date and time"),
                    fieldWithPath("updatedAt").description("Last update date and time"),
                    fieldWithPath("images").description("Item images"),
                    fieldWithPath("available").description("Item availability status"),
                    fieldWithPath("vippsPaymentEnabled")
                        .description("Whether Vipps payment is enabled"))));

    verify(itemService).createItem(any(CreateItemDTO.class), eq(123L));
  }

  @Test
  void updateItem_ShouldReturnUpdatedItem() throws Exception {
    // Given
    Long itemId = 1L;

    CreateItemDTO updateItemDTO = new CreateItemDTO();
    updateItemDTO.setTitle("Updated Item");
    updateItemDTO.setCategoryId(2L);
    updateItemDTO.setShortDescription("Updated short description");
    updateItemDTO.setLongDescription("Updated long description for the item");
    updateItemDTO.setPrice(129.99);
    updateItemDTO.setLocationId(1L);
    updateItemDTO.setCondition("USED");
    updateItemDTO.setShippingOptionId(1L);
    updateItemDTO.setLatitude(59.9171);
    updateItemDTO.setLongitude(10.7274);
    updateItemDTO.setSize("L");
    updateItemDTO.setBrand("Updated Brand");
    updateItemDTO.setColor("Red");

    ItemDTO updatedItem = new ItemDTO();
    updatedItem.setId(itemId);
    updatedItem.setTitle("Updated Item");
    updatedItem.setShortDescription("Updated short description");
    updatedItem.setPrice(129.99);

    when(itemService.updateItem(eq(itemId), any(CreateItemDTO.class), anyLong()))
        .thenReturn(updatedItem);

    // When/Then
    mockMvc
        .perform(
            put("/api/items/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateItemDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Updated Item"))
        .andDo(
            document(
                "items-update",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").description("Item title"),
                    fieldWithPath("categoryId").description("Category ID"),
                    fieldWithPath("shortDescription").description("Short description of the item"),
                    fieldWithPath("longDescription")
                        .description("Detailed description of the item"),
                    fieldWithPath("price").description("Item price"),
                    fieldWithPath("locationId").description("Location ID"),
                    fieldWithPath("condition").description("Item condition"),
                    fieldWithPath("shippingOptionId").description("Shipping option ID"),
                    fieldWithPath("latitude").description("Latitude coordinate"),
                    fieldWithPath("longitude").description("Longitude coordinate"),
                    fieldWithPath("size").description("Item size"),
                    fieldWithPath("brand").description("Item brand"),
                    fieldWithPath("color").description("Item color"),
                    fieldWithPath("vippsPaymentEnabled")
                        .description("Whether Vipps payment is enabled")
                        .optional(),
                    fieldWithPath("images").description("Item images").optional()),
                responseFields(
                    fieldWithPath("id").description("Item ID"),
                    fieldWithPath("title").description("Item title"),
                    fieldWithPath("shortDescription").description("Short description of the item"),
                    fieldWithPath("longDescription")
                        .description("Detailed description of the item"),
                    fieldWithPath("price").description("Item price"),
                    fieldWithPath("categoryId").description("Category ID"),
                    fieldWithPath("categoryName").description("Category name"),
                    fieldWithPath("sellerId").description("Seller ID"),
                    fieldWithPath("sellerName").description("Seller name"),
                    fieldWithPath("locationId").description("Location ID"),
                    fieldWithPath("locationName").description("Location name"),
                    fieldWithPath("shippingOptionId").description("Shipping option ID"),
                    fieldWithPath("shippingOptionName").description("Shipping option name"),
                    fieldWithPath("latitude").description("Latitude coordinate"),
                    fieldWithPath("longitude").description("Longitude coordinate"),
                    fieldWithPath("condition").description("Item condition"),
                    fieldWithPath("size").description("Item size"),
                    fieldWithPath("brand").description("Item brand"),
                    fieldWithPath("color").description("Item color"),
                    fieldWithPath("createdAt").description("Creation date and time"),
                    fieldWithPath("updatedAt").description("Last update date and time"),
                    fieldWithPath("images").description("Item images"),
                    fieldWithPath("available").description("Item availability status"),
                    fieldWithPath("vippsPaymentEnabled")
                        .description("Whether Vipps payment is enabled"))));

    verify(itemService).updateItem(eq(itemId), any(CreateItemDTO.class), eq(123L));
  }

  @Test
  void deactivateItem_ShouldReturnNoContent() throws Exception {
    // Given
    Long itemId = 1L;
    doNothing().when(itemService).deactivateItem(itemId, 123L);

    // When/Then
    mockMvc
        .perform(patch("/api/items/" + itemId + "/deactivate"))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "items-deactivate",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())));

    verify(itemService).deactivateItem(itemId, 123L);
  }

  @Test
  void activateItem_ShouldReturnNoContent() throws Exception {
    // Given
    Long itemId = 1L;
    doNothing().when(itemService).activateItem(itemId, 123L);

    // When/Then
    mockMvc
        .perform(patch("/api/items/" + itemId + "/activate"))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "items-activate",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())));

    verify(itemService).activateItem(itemId, 123L);
  }

  @Test
  void deleteItem_ShouldReturnNoContent() throws Exception {
    // Given
    Long itemId = 1L;
    doNothing().when(itemService).deleteItem(itemId, 123L);

    // When/Then
    mockMvc
        .perform(delete("/api/items/" + itemId))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "items-delete",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())));

    verify(itemService).deleteItem(itemId, 123L);
  }
}
