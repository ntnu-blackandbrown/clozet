package stud.ntnu.no.backend.Favorite.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stud.ntnu.no.backend.Favorite.DTOs.CreateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.DTOs.FavoriteDTO;
import stud.ntnu.no.backend.Favorite.DTOs.UpdateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.Exceptions.FavoriteNotFoundException;
import stud.ntnu.no.backend.Favorite.Service.FavoriteService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FavoriteControllerTest {

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private FavoriteController favoriteController;

    private FavoriteDTO favoriteDTO;
    private FavoriteDTO secondFavoriteDTO;
    private List<FavoriteDTO> favoriteDTOList;
    private CreateFavoriteRequest createFavoriteRequest;
    private UpdateFavoriteRequest updateFavoriteRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        favoriteDTO = new FavoriteDTO(
                1L,
                "user123",
                101L,
                "PRODUCT",
                LocalDateTime.now()
        );

        secondFavoriteDTO = new FavoriteDTO(
                2L,
                "user123",
                102L,
                "LISTING",
                LocalDateTime.now()
        );

        favoriteDTOList = Arrays.asList(favoriteDTO, secondFavoriteDTO);

        createFavoriteRequest = new CreateFavoriteRequest(
                "user456",
                103L,
                "PRODUCT"
        );

        updateFavoriteRequest = new UpdateFavoriteRequest(
                "LISTING"
        );
    }

    @Test
    void getAllFavorites_ShouldReturnListOfFavorites() {
        when(favoriteService.getAllFavorites()).thenReturn(favoriteDTOList);

        ResponseEntity<List<FavoriteDTO>> response = favoriteController.getAllFavorites();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());
        verify(favoriteService, times(1)).getAllFavorites();
    }

    @Test
    void getFavoritesByUserId_ShouldReturnUserFavorites() {
        when(favoriteService.getFavoritesByUserId("user123")).thenReturn(favoriteDTOList);

        ResponseEntity<List<FavoriteDTO>> response = favoriteController.getFavoritesByUserId("user123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("user123", response.getBody().get(0).getUserId());
        verify(favoriteService, times(1)).getFavoritesByUserId("user123");
    }

    @Test
    void getFavoriteById_WithValidId_ShouldReturnFavorite() {
        when(favoriteService.getFavoriteById(1L)).thenReturn(favoriteDTO);

        ResponseEntity<FavoriteDTO> response = favoriteController.getFavoriteById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("PRODUCT", response.getBody().getItemType());
        verify(favoriteService, times(1)).getFavoriteById(1L);
    }

    @Test
    void getFavoriteById_WithInvalidId_ShouldThrowException() {
        when(favoriteService.getFavoriteById(99L)).thenThrow(new FavoriteNotFoundException(99L));

        try {
            favoriteController.getFavoriteById(99L);
        } catch (FavoriteNotFoundException e) {
            assertEquals("Favorite not found with id: 99", e.getMessage());
        }

        verify(favoriteService, times(1)).getFavoriteById(99L);
    }

    @Test
    void createFavorite_WithValidData_ShouldReturnCreatedFavorite() {
        when(favoriteService.createFavorite(any(CreateFavoriteRequest.class))).thenReturn(favoriteDTO);

        ResponseEntity<FavoriteDTO> response = favoriteController.createFavorite(createFavoriteRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(favoriteService, times(1)).createFavorite(any(CreateFavoriteRequest.class));
    }

    @Test
    void updateFavorite_WithValidData_ShouldReturnUpdatedFavorite() {
        when(favoriteService.updateFavorite(eq(1L), any(UpdateFavoriteRequest.class))).thenReturn(favoriteDTO);

        ResponseEntity<FavoriteDTO> response = favoriteController.updateFavorite(1L, updateFavoriteRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(favoriteService, times(1)).updateFavorite(eq(1L), any(UpdateFavoriteRequest.class));
    }

    @Test
    void deleteFavorite_ShouldReturnNoContent() {
        doNothing().when(favoriteService).deleteFavorite(1L);

        ResponseEntity<Void> response = favoriteController.deleteFavorite(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(favoriteService, times(1)).deleteFavorite(1L);
    }
}