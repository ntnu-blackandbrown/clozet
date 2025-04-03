package stud.ntnu.no.backend.favorite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.exception.FavoriteNotFoundException;
import stud.ntnu.no.backend.favorite.exception.FavoriteValidationException;
import stud.ntnu.no.backend.favorite.service.FavoriteService;
import stud.ntnu.no.backend.favorite.controller.FavoriteController;
import stud.ntnu.no.backend.favorite.config.TestSecurityConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-tester for favoritt-API.
 * Tester at endepunktene fungerer som forventet og returnerer korrekte statuskoder og data.
 */
@WebMvcTest(controllers = FavoriteController.class)
@Import(TestSecurityConfig.class)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FavoriteService favoriteService;

    private FavoriteDTO testFavoriteDTO;
    private CreateFavoriteRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        // Oppretter testdata
        testFavoriteDTO = new FavoriteDTO();
        testFavoriteDTO.setId(1L);
        testFavoriteDTO.setUserId("1");
        testFavoriteDTO.setItemId(1L);
        testFavoriteDTO.setActive(true);
        testFavoriteDTO.setCreatedAt(LocalDateTime.now());
        testFavoriteDTO.setUpdatedAt(LocalDateTime.now());

        testCreateRequest = new CreateFavoriteRequest();
        testCreateRequest.setUserId("1");
        testCreateRequest.setItemId(1L);
        testCreateRequest.setActive(true);
    }

    /**
     * Tester at getAllFavorites returnerer en liste med favoritter og status 200 OK.
     */
    @Test
    @WithMockUser
    void getAllFavorites_shouldReturnListOfFavorites() throws Exception {
        // Arrange
        List<FavoriteDTO> favorites = Arrays.asList(testFavoriteDTO);
        when(favoriteService.getAllFavorites()).thenReturn(favorites);

        // Act & Assert
        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testFavoriteDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].userId", is(testFavoriteDTO.getUserId())))
                .andExpect(jsonPath("$[0].itemId", is(testFavoriteDTO.getItemId().intValue())))
                .andExpect(jsonPath("$[0].active", is(testFavoriteDTO.isActive())));

        verify(favoriteService).getAllFavorites();
    }

    /**
     * Tester at getFavoriteById returnerer en favoritt og status 200 OK når favoritten finnes.
     */
    @Test
    @WithMockUser
    void getFavoriteById_whenFavoriteExists_shouldReturnFavorite() throws Exception {
        // Arrange
        Long favoriteId = 1L;
        when(favoriteService.getFavoriteById(favoriteId)).thenReturn(testFavoriteDTO);

        // Act & Assert
        mockMvc.perform(get("/api/favorites/{id}", favoriteId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testFavoriteDTO.getId().intValue())))
                .andExpect(jsonPath("$.userId", is(testFavoriteDTO.getUserId())))
                .andExpect(jsonPath("$.itemId", is(testFavoriteDTO.getItemId().intValue())))
                .andExpect(jsonPath("$.active", is(testFavoriteDTO.isActive())));

        verify(favoriteService).getFavoriteById(favoriteId);
    }

    /**
     * Tester at getFavoriteById returnerer status 404 NOT FOUND når favoritten ikke finnes.
     */
    @Test
    @WithMockUser
    void getFavoriteById_whenFavoriteDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        Long favoriteId = 999L;
        when(favoriteService.getFavoriteById(favoriteId))
                .thenThrow(new FavoriteNotFoundException(favoriteId));

        // Act & Assert
        mockMvc.perform(get("/api/favorites/{id}", favoriteId))
                .andExpect(status().isNotFound());

        verify(favoriteService).getFavoriteById(favoriteId);
    }

    /**
     * Tester at getFavoritesByUserId returnerer en liste med favoritter og status 200 OK.
     */
    @Test
    @WithMockUser
    void getFavoritesByUserId_shouldReturnUserFavorites() throws Exception {
        // Arrange
        String userId = "1";
        List<FavoriteDTO> favorites = Arrays.asList(testFavoriteDTO);
        when(favoriteService.getFavoritesByUserId(userId)).thenReturn(favorites);

        // Act & Assert
        mockMvc.perform(get("/api/favorites/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testFavoriteDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].userId", is(testFavoriteDTO.getUserId())))
                .andExpect(jsonPath("$[0].itemId", is(testFavoriteDTO.getItemId().intValue())))
                .andExpect(jsonPath("$[0].active", is(testFavoriteDTO.isActive())));

        verify(favoriteService).getFavoritesByUserId(userId);
    }

    /**
     * Tester at getFavoritesByUserId returnerer status 400 BAD REQUEST når bruker-ID er ugyldig.
     */
    @Test
    @WithMockUser
    void getFavoritesByUserId_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        // Arrange
        String userId = "invalid";
        when(favoriteService.getFavoritesByUserId(userId))
                .thenThrow(new FavoriteValidationException("Invalid user ID format"));

        // Act & Assert
        mockMvc.perform(get("/api/favorites/user/{userId}", userId))
                .andExpect(status().isBadRequest());

        verify(favoriteService).getFavoritesByUserId(userId);
    }

    /**
     * Tester at createFavorite oppretter en ny favoritt og returnerer status 201 CREATED.
     */
    @Test
    @WithMockUser
    void createFavorite_withValidRequest_shouldCreateFavorite() throws Exception {
        // Arrange
        when(favoriteService.createFavorite(any(CreateFavoriteRequest.class))).thenReturn(testFavoriteDTO);

        // Act & Assert
        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testFavoriteDTO.getId().intValue())))
                .andExpect(jsonPath("$.userId", is(testFavoriteDTO.getUserId())))
                .andExpect(jsonPath("$.itemId", is(testFavoriteDTO.getItemId().intValue())))
                .andExpect(jsonPath("$.active", is(testFavoriteDTO.isActive())));

        verify(favoriteService).createFavorite(any(CreateFavoriteRequest.class));
    }

    /**
     * Tester at createFavorite returnerer status 400 BAD REQUEST når forespørselen er ugyldig.
     */
    @Test
    @WithMockUser
    void createFavorite_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        // Arrange
        when(favoriteService.createFavorite(any(CreateFavoriteRequest.class)))
                .thenThrow(new FavoriteValidationException("Invalid request"));

        // Act & Assert
        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isBadRequest());

        verify(favoriteService).createFavorite(any(CreateFavoriteRequest.class));
    }

    /**
     * Tester at updateFavorite oppdaterer en favoritt og returnerer status 200 OK.
     */
    @Test
    @WithMockUser
    void updateFavorite_withValidRequest_shouldUpdateFavorite() throws Exception {
        // Arrange
        Long favoriteId = 1L;
        when(favoriteService.updateFavorite(eq(favoriteId), any(CreateFavoriteRequest.class)))
                .thenReturn(testFavoriteDTO);

        // Act & Assert
        mockMvc.perform(put("/api/favorites/{id}", favoriteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testFavoriteDTO.getId().intValue())))
                .andExpect(jsonPath("$.userId", is(testFavoriteDTO.getUserId())))
                .andExpect(jsonPath("$.itemId", is(testFavoriteDTO.getItemId().intValue())))
                .andExpect(jsonPath("$.active", is(testFavoriteDTO.isActive())));

        verify(favoriteService).updateFavorite(eq(favoriteId), any(CreateFavoriteRequest.class));
    }

    /**
     * Tester at updateFavorite returnerer status 404 NOT FOUND når favoritten ikke finnes.
     */
    @Test
    @WithMockUser
    void updateFavorite_whenFavoriteDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        Long favoriteId = 999L;
        when(favoriteService.updateFavorite(eq(favoriteId), any(CreateFavoriteRequest.class)))
                .thenThrow(new FavoriteNotFoundException(favoriteId));

        // Act & Assert
        mockMvc.perform(put("/api/favorites/{id}", favoriteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isNotFound());

        verify(favoriteService).updateFavorite(eq(favoriteId), any(CreateFavoriteRequest.class));
    }

    /**
     * Tester at deleteFavorite sletter en favoritt og returnerer status 204 NO CONTENT.
     */
    @Test
    @WithMockUser
    void deleteFavorite_whenFavoriteExists_shouldDeleteFavorite() throws Exception {
        // Arrange
        Long favoriteId = 1L;
        doNothing().when(favoriteService).deleteFavorite(favoriteId);

        // Act & Assert
        mockMvc.perform(delete("/api/favorites/{id}", favoriteId))
                .andExpect(status().isNoContent());

        verify(favoriteService).deleteFavorite(favoriteId);
    }

    /**
     * Tester at deleteFavorite returnerer status 404 NOT FOUND når favoritten ikke finnes.
     */
    @Test
    @WithMockUser
    void deleteFavorite_whenFavoriteDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        Long favoriteId = 999L;
        doThrow(new FavoriteNotFoundException(favoriteId)).when(favoriteService).deleteFavorite(favoriteId);

        // Act & Assert
        mockMvc.perform(delete("/api/favorites/{id}", favoriteId))
                .andExpect(status().isNotFound());

        verify(favoriteService).deleteFavorite(favoriteId);
    }
} 