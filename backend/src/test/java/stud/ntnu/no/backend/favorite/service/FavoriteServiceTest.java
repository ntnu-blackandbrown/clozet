package stud.ntnu.no.backend.favorite.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.favorite.exception.FavoriteNotFoundException;
import stud.ntnu.no.backend.favorite.exception.FavoriteValidationException;
import stud.ntnu.no.backend.favorite.mapper.FavoriteMapper;
import stud.ntnu.no.backend.favorite.repository.FavoriteRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Enhetstester for FavoriteService.
 * Bruker Mockito for å mocke avhengigheter.
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User testUser;
    private Item testItem;
    private Favorite testFavorite;
    private FavoriteDTO testFavoriteDTO;
    private CreateFavoriteRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        // Sett opp testdata
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        
        testItem = new Item();
        testItem.setId(1L);
        testItem.setTitle("Test Item");
        
        testFavorite = new Favorite();
        testFavorite.setId(1L);
        testFavorite.setUser(testUser);
        testFavorite.setItem(testItem);
        testFavorite.setActive(true);
        testFavorite.setCreatedAt(LocalDateTime.now());
        
        testFavoriteDTO = new FavoriteDTO();
        testFavoriteDTO.setId(1L);
        testFavoriteDTO.setUserId(testUser.getId().toString());
        testFavoriteDTO.setItemId(testItem.getId());
        testFavoriteDTO.setActive(true);
        testFavoriteDTO.setCreatedAt(LocalDateTime.now());
        testFavoriteDTO.setUpdatedAt(LocalDateTime.now());
        
        testCreateRequest = new CreateFavoriteRequest();
        testCreateRequest.setUserId(testUser.getId().toString());
        testCreateRequest.setItemId(testItem.getId());
        testCreateRequest.setActive(true);
    }

    /**
     * Tester at getAllFavorites returnerer alle favoritter.
     */
    @Test
    void getAllFavorites_shouldReturnAllFavorites() {
        // Arrange
        List<Favorite> favorites = Arrays.asList(testFavorite);
        when(favoriteRepository.findAll()).thenReturn(favorites);
        when(favoriteMapper.toDTO(any(Favorite.class))).thenReturn(testFavoriteDTO);
        
        // Act
        List<FavoriteDTO> result = favoriteService.getAllFavorites();
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testFavoriteDTO, result.get(0));
        verify(favoriteRepository).findAll();
        verify(favoriteMapper).toDTO(testFavorite);
    }

    /**
     * Tester at getFavoritesByUserId returnerer favoritter for en spesifikk bruker.
     */
    @Test
    void getFavoritesByUserId_withValidUserId_shouldReturnUserFavorites() {
        // Arrange
        String userId = testUser.getId().toString();
        List<Favorite> favorites = Arrays.asList(testFavorite);
        when(favoriteRepository.findByUserId(userId)).thenReturn(favorites);
        when(favoriteMapper.toDTO(any(Favorite.class))).thenReturn(testFavoriteDTO);
        
        // Act
        List<FavoriteDTO> result = favoriteService.getFavoritesByUserId(userId);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testFavoriteDTO, result.get(0));
        verify(favoriteRepository).findByUserId(userId);
        verify(favoriteMapper).toDTO(testFavorite);
    }

    /**
     * Tester at getFavoritesByUserId kaster unntak når bruker-ID er null.
     */
    @Test
    void getFavoritesByUserId_withNullUserId_shouldThrowValidationException() {
        // Act & Assert
        assertThrows(FavoriteValidationException.class, () -> favoriteService.getFavoritesByUserId(null));
        verify(favoriteRepository, never()).findByUserId(anyString());
    }

    /**
     * Tester at getFavoriteById returnerer en favoritt når den finnes.
     */
    @Test
    void getFavoriteById_whenFavoriteExists_shouldReturnFavorite() {
        // Arrange
        Long favoriteId = 1L;
        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.of(testFavorite));
        when(favoriteMapper.toDTO(testFavorite)).thenReturn(testFavoriteDTO);
        
        // Act
        FavoriteDTO result = favoriteService.getFavoriteById(favoriteId);
        
        // Assert
        assertNotNull(result);
        assertEquals(testFavoriteDTO, result);
        verify(favoriteRepository).findById(favoriteId);
        verify(favoriteMapper).toDTO(testFavorite);
    }

    /**
     * Tester at getFavoriteById kaster unntak når favoritten ikke finnes.
     */
    @Test
    void getFavoriteById_whenFavoriteDoesNotExist_shouldThrowNotFoundException() {
        // Arrange
        Long favoriteId = 999L;
        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(FavoriteNotFoundException.class, () -> favoriteService.getFavoriteById(favoriteId));
        verify(favoriteRepository).findById(favoriteId);
        verify(favoriteMapper, never()).toDTO(any(Favorite.class));
    }

    /**
     * Tester at createFavorite oppretter en ny favoritt.
     */
    @Test
    void createFavorite_withValidRequest_shouldCreateFavorite() {
        // Arrange
        when(favoriteRepository.existsByUserIdAndItemId(anyString(), anyLong())).thenReturn(false);
        when(favoriteMapper.toEntity(testCreateRequest)).thenReturn(testFavorite);
        when(favoriteRepository.save(testFavorite)).thenReturn(testFavorite);
        when(favoriteMapper.toDTO(testFavorite)).thenReturn(testFavoriteDTO);
        
        // Act
        FavoriteDTO result = favoriteService.createFavorite(testCreateRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(testFavoriteDTO, result);
        verify(favoriteRepository).existsByUserIdAndItemId(testCreateRequest.getUserId(), testCreateRequest.getItemId());
        verify(favoriteMapper).toEntity(testCreateRequest);
        verify(favoriteRepository).save(testFavorite);
        verify(favoriteMapper).toDTO(testFavorite);
    }

    /**
     * Tester at createFavorite kaster unntak når en favoritt allerede eksisterer.
     */
    @Test
    void createFavorite_whenFavoriteAlreadyExists_shouldThrowValidationException() {
        // Arrange
        when(favoriteRepository.existsByUserIdAndItemId(anyString(), anyLong())).thenReturn(true);
        
        // Act & Assert
        assertThrows(FavoriteValidationException.class, () -> favoriteService.createFavorite(testCreateRequest));
        verify(favoriteRepository).existsByUserIdAndItemId(testCreateRequest.getUserId(), testCreateRequest.getItemId());
        verify(favoriteMapper, never()).toEntity(any(CreateFavoriteRequest.class));
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    /**
     * Tester at updateFavorite oppdaterer en eksisterende favoritt.
     */
    @Test
    void updateFavorite_whenFavoriteExists_shouldUpdateFavorite() {
        // Arrange
        Long favoriteId = 1L;
        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.of(testFavorite));
        when(favoriteRepository.save(testFavorite)).thenReturn(testFavorite);
        when(favoriteMapper.toDTO(testFavorite)).thenReturn(testFavoriteDTO);
        
        // Act
        FavoriteDTO result = favoriteService.updateFavorite(favoriteId, testCreateRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(testFavoriteDTO, result);
        verify(favoriteRepository).findById(favoriteId);
        verify(favoriteMapper).updateEntity(testFavorite, testCreateRequest);
        verify(favoriteRepository).save(testFavorite);
        verify(favoriteMapper).toDTO(testFavorite);
    }

    /**
     * Tester at updateFavorite kaster unntak når favoritten ikke finnes.
     */
    @Test
    void updateFavorite_whenFavoriteDoesNotExist_shouldThrowNotFoundException() {
        // Arrange
        Long favoriteId = 999L;
        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(FavoriteNotFoundException.class, () -> favoriteService.updateFavorite(favoriteId, testCreateRequest));
        verify(favoriteRepository).findById(favoriteId);
        verify(favoriteMapper, never()).updateEntity(any(Favorite.class), any(CreateFavoriteRequest.class));
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    /**
     * Tester at deleteFavorite sletter en favoritt når den finnes.
     */
    @Test
    void deleteFavorite_whenFavoriteExists_shouldDeleteFavorite() {
        // Arrange
        Long favoriteId = 1L;
        when(favoriteRepository.existsById(favoriteId)).thenReturn(true);
        
        // Act
        favoriteService.deleteFavorite(favoriteId);
        
        // Assert
        verify(favoriteRepository).existsById(favoriteId);
        verify(favoriteRepository).deleteById(favoriteId);
    }

    /**
     * Tester at deleteFavorite kaster unntak når favoritten ikke finnes.
     */
    @Test
    void deleteFavorite_whenFavoriteDoesNotExist_shouldThrowNotFoundException() {
        // Arrange
        Long favoriteId = 999L;
        when(favoriteRepository.existsById(favoriteId)).thenReturn(false);
        
        // Act & Assert
        assertThrows(FavoriteNotFoundException.class, () -> favoriteService.deleteFavorite(favoriteId));
        verify(favoriteRepository).existsById(favoriteId);
        verify(favoriteRepository, never()).deleteById(anyLong());
    }
} 