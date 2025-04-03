package stud.ntnu.no.backend.favorite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.favorite.repository.FavoriteRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrasjonstester for favoritt-funksjonaliteten.
 * Tester interaksjonen mellom alle lag i applikasjonen.
 * Bruker en in-memory H2-database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FavoriteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User testUser;
    private Item testItem;
    private Favorite testFavorite;
    private CreateFavoriteRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        // Rydder opp før hver test
        favoriteRepository.deleteAll();

        // Oppretter testdata
        testUser = createTestUser("user1", "user1@example.com");
        testItem = createTestItem("Test Item", testUser);
        testFavorite = createTestFavorite(testUser, testItem, true);
        
        testCreateRequest = new CreateFavoriteRequest();
        testCreateRequest.setUserId(testUser.getId().toString());
        testCreateRequest.setItemId(testItem.getId());
        testCreateRequest.setActive(true);
    }

    /**
     * Tester at GET /api/favorites returnerer alle favoritter.
     */
    @Test
    void getAllFavorites_shouldReturnAllFavorites() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(testUser.getId().toString())))
                .andExpect(jsonPath("$[0].itemId", is(testItem.getId().intValue())))
                .andExpect(jsonPath("$[0].active", is(testFavorite.isActive())));
    }

    /**
     * Tester at GET /api/favorites/{id} returnerer en spesifikk favoritt.
     */
    @Test
    void getFavoriteById_whenFavoriteExists_shouldReturnFavorite() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/favorites/{id}", testFavorite.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testFavorite.getId().intValue())))
                .andExpect(jsonPath("$.userId", is(testUser.getId().toString())))
                .andExpect(jsonPath("$.itemId", is(testItem.getId().intValue())))
                .andExpect(jsonPath("$.active", is(testFavorite.isActive())));
    }

    /**
     * Tester at GET /api/favorites/{id} returnerer 404 når favoritten ikke finnes.
     */
    @Test
    void getFavoriteById_whenFavoriteDoesNotExist_shouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/favorites/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * Tester at GET /api/favorites/user/{userId} returnerer alle favoritter for en bruker.
     */
    @Test
    void getFavoritesByUserId_shouldReturnUserFavorites() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/favorites/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(testUser.getId().toString())))
                .andExpect(jsonPath("$[0].itemId", is(testItem.getId().intValue())))
                .andExpect(jsonPath("$[0].active", is(testFavorite.isActive())));
    }

    /**
     * Tester at POST /api/favorites oppretter en ny favoritt.
     */
    @Test
    void createFavorite_withValidRequest_shouldCreateFavorite() throws Exception {
        // Arrange
        Item newItem = createTestItem("New Item", testUser);
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId(testUser.getId().toString());
        request.setItemId(newItem.getId());
        request.setActive(true);

        // Verifiserer at favoritten ikke eksisterer
        assertFalse(favoriteRepository.existsByUserIdAndItemId(
                request.getUserId(), request.getItemId()));

        // Act & Assert
        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId", is(request.getUserId())))
                .andExpect(jsonPath("$.itemId", is(request.getItemId().intValue())))
                .andExpect(jsonPath("$.active", is(request.getActive())));

        // Verifiserer at favoritten nå er opprettet
        assertTrue(favoriteRepository.existsByUserIdAndItemId(
                request.getUserId(), request.getItemId()));
    }

    /**
     * Tester at PUT /api/favorites/{id} oppdaterer en eksisterende favoritt.
     */
    @Test
    void updateFavorite_withValidRequest_shouldUpdateFavorite() throws Exception {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId(testUser.getId().toString());
        request.setItemId(testItem.getId());
        request.setActive(false); // Endrer active fra true til false

        // Act & Assert
        mockMvc.perform(put("/api/favorites/{id}", testFavorite.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testFavorite.getId().intValue())))
                .andExpect(jsonPath("$.userId", is(request.getUserId())))
                .andExpect(jsonPath("$.itemId", is(request.getItemId().intValue())))
                .andExpect(jsonPath("$.active", is(request.getActive())));

        // Verifiserer at favoritten er oppdatert i databasen
        Favorite updatedFavorite = favoriteRepository.findById(testFavorite.getId()).orElseThrow();
        assertFalse(updatedFavorite.isActive());
    }

    /**
     * Tester at DELETE /api/favorites/{id} sletter en favoritt.
     */
    @Test
    void deleteFavorite_whenFavoriteExists_shouldDeleteFavorite() throws Exception {
        // Verifiserer at favoritten eksisterer
        assertTrue(favoriteRepository.existsById(testFavorite.getId()));

        // Act & Assert
        mockMvc.perform(delete("/api/favorites/{id}", testFavorite.getId()))
                .andExpect(status().isNoContent());

        // Verifiserer at favoritten er slettet
        assertFalse(favoriteRepository.existsById(testFavorite.getId()));
    }

    /**
     * Tester at POST /api/favorites returnerer feil når en favoritt allerede eksisterer.
     */
    @Test
    void createFavorite_whenFavoriteAlreadyExists_shouldReturnBadRequest() throws Exception {
        // Arrange - bruker den eksisterende favorittens data
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId(testUser.getId().toString());
        request.setItemId(testItem.getId());
        request.setActive(true);

        // Act & Assert
        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verifiserer at det ikke ble opprettet en duplikat
        List<Favorite> favorites = favoriteRepository.findByUserId(testUser.getId().toString());
        assertEquals(1, favorites.size());
    }

    /**
     * Hjelpermetode for å opprette en testbruker.
     */
    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("hashedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Hjelpermetode for å opprette et testelement.
     */
    private Item createTestItem(String title, User seller) {
        Item item = new Item();
        item.setTitle(title);
        item.setShortDescription("Short description");
        item.setLongDescription("Test long description");
        item.setPrice(100.0);
        item.setLatitude(10.0);
        item.setLongitude(10.0);
        item.setCondition("New");
        item.setSize("M");
        item.setBrand("TestBrand");
        item.setColor("Blue");
        item.setAvailable(true);
        item.setVippsPaymentEnabled(true);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        item.setSeller(seller);
        // Merk: Vi mangler å sette category, location og shippingOption
        return itemRepository.save(item);
    }

    /**
     * Hjelpermetode for å opprette en testfavoritt.
     */
    private Favorite createTestFavorite(User user, Item item, boolean active) {
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItem(item);
        favorite.setCreatedAt(LocalDateTime.now());
        favorite.setActive(active);
        return favoriteRepository.save(favorite);
    }
} 