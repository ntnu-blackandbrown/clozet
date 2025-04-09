package stud.ntnu.no.backend.favorite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceImplTest {

  @Mock private FavoriteRepository favoriteRepository;

  @Mock private FavoriteMapper favoriteMapper;

  @InjectMocks private FavoriteServiceImpl favoriteService;

  private Favorite favorite1;
  private Favorite favorite2;
  private FavoriteDTO favoriteDTO1;
  private FavoriteDTO favoriteDTO2;
  private CreateFavoriteRequest createRequest;

  @BeforeEach
  void setUp() {
    // Opprett test data - tilpasser Favorite objekter basert på den faktiske strukturen
    favorite1 = new Favorite();
    favorite1.setId(1L);
    // Setter userId og itemId basert på faktisk implementasjon av Favorite entity

    favorite2 = new Favorite();
    favorite2.setId(2L);
    // Setter userId og itemId basert på faktisk implementasjon av Favorite entity

    favoriteDTO1 = new FavoriteDTO();
    favoriteDTO1.setId(1L);
    favoriteDTO1.setUserId("1");
    favoriteDTO1.setItemId(1L);

    favoriteDTO2 = new FavoriteDTO();
    favoriteDTO2.setId(2L);
    favoriteDTO2.setUserId("1");
    favoriteDTO2.setItemId(2L);

    createRequest = new CreateFavoriteRequest();
    createRequest.setUserId("1");
    createRequest.setItemId(3L);
  }

  @Test
  void getAllFavorites_shouldReturnAllFavorites() {
    // Arrange
    when(favoriteRepository.findAll()).thenReturn(Arrays.asList(favorite1, favorite2));
    when(favoriteMapper.toDTO(favorite1)).thenReturn(favoriteDTO1);
    when(favoriteMapper.toDTO(favorite2)).thenReturn(favoriteDTO2);

    // Act
    List<FavoriteDTO> result = favoriteService.getAllFavorites();

    // Assert
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).getId());
    assertEquals(2L, result.get(1).getId());
    verify(favoriteRepository, times(1)).findAll();
    verify(favoriteMapper, times(1)).toDTO(favorite1);
    verify(favoriteMapper, times(1)).toDTO(favorite2);
  }

  @Test
  void getFavoritesByUserId_withValidUserId_shouldReturnUserFavorites() {
    // Arrange
    when(favoriteRepository.findByUserId("1")).thenReturn(Arrays.asList(favorite1, favorite2));
    when(favoriteMapper.toDTO(favorite1)).thenReturn(favoriteDTO1);
    when(favoriteMapper.toDTO(favorite2)).thenReturn(favoriteDTO2);

    // Act
    List<FavoriteDTO> result = favoriteService.getFavoritesByUserId("1");

    // Assert
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).getId());
    assertEquals(2L, result.get(1).getId());
    verify(favoriteRepository, times(1)).findByUserId("1");
    verify(favoriteMapper, times(1)).toDTO(favorite1);
    verify(favoriteMapper, times(1)).toDTO(favorite2);
  }

  @Test
  void getFavoritesByUserId_withNullUserId_shouldThrowException() {
    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.getFavoritesByUserId(null);
        });
  }

  @Test
  void getFavoritesByUserId_withEmptyUserId_shouldThrowException() {
    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.getFavoritesByUserId("");
        });
  }

  @Test
  void getFavoritesByUserId_withNonNumericUserId_shouldThrowException() {
    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.getFavoritesByUserId("abc");
        });
  }

  @Test
  void getFavoriteById_withValidId_shouldReturnFavorite() {
    // Arrange
    when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite1));
    when(favoriteMapper.toDTO(favorite1)).thenReturn(favoriteDTO1);

    // Act
    FavoriteDTO result = favoriteService.getFavoriteById(1L);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("1", result.getUserId());
    assertEquals(1L, result.getItemId());
    verify(favoriteRepository, times(1)).findById(1L);
    verify(favoriteMapper, times(1)).toDTO(favorite1);
  }

  @Test
  void getFavoriteById_withNullId_shouldThrowException() {
    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.getFavoriteById(null);
        });
  }

  @Test
  void getFavoriteById_withInvalidId_shouldThrowException() {
    // Arrange
    when(favoriteRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        FavoriteNotFoundException.class,
        () -> {
          favoriteService.getFavoriteById(99L);
        });
    verify(favoriteRepository, times(1)).findById(99L);
  }

  @Test
  void createFavorite_withValidRequest_shouldReturnCreatedFavorite() {
    // Arrange
    when(favoriteRepository.existsByUserIdAndItemId("1", 3L)).thenReturn(false);
    when(favoriteMapper.toEntity(createRequest)).thenReturn(favorite1);
    when(favoriteRepository.save(favorite1)).thenReturn(favorite1);
    when(favoriteMapper.toDTO(favorite1)).thenReturn(favoriteDTO1);

    // Act
    FavoriteDTO result = favoriteService.createFavorite(createRequest);

    // Assert
    assertNotNull(result);
    verify(favoriteRepository, times(1)).existsByUserIdAndItemId("1", 3L);
    verify(favoriteMapper, times(1)).toEntity(createRequest);
    verify(favoriteRepository, times(1)).save(favorite1);
    verify(favoriteMapper, times(1)).toDTO(favorite1);
  }

  @Test
  void createFavorite_withNullRequest_shouldThrowException() {
    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.createFavorite(null);
        });
  }

  @Test
  void createFavorite_withAlreadyExistingFavorite_shouldThrowException() {
    // Arrange
    when(favoriteRepository.existsByUserIdAndItemId("1", 3L)).thenReturn(true);

    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.createFavorite(createRequest);
        });
    verify(favoriteRepository, times(1)).existsByUserIdAndItemId("1", 3L);
  }

  @Test
  void updateFavorite_withValidIdAndRequest_shouldReturnUpdatedFavorite() {
    // Arrange
    when(favoriteRepository.findById(1L)).thenReturn(Optional.of(favorite1));
    doNothing().when(favoriteMapper).updateEntity(favorite1, createRequest);
    when(favoriteRepository.save(favorite1)).thenReturn(favorite1);
    when(favoriteMapper.toDTO(favorite1)).thenReturn(favoriteDTO1);

    // Act
    FavoriteDTO result = favoriteService.updateFavorite(1L, createRequest);

    // Assert
    assertNotNull(result);
    verify(favoriteRepository, times(1)).findById(1L);
    verify(favoriteMapper, times(1)).updateEntity(favorite1, createRequest);
    verify(favoriteRepository, times(1)).save(favorite1);
    verify(favoriteMapper, times(1)).toDTO(favorite1);
  }

  @Test
  void updateFavorite_withNullId_shouldThrowException() {
    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.updateFavorite(null, createRequest);
        });
  }

  @Test
  void updateFavorite_withNullRequest_shouldThrowException() {
    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.updateFavorite(1L, null);
        });
  }

  @Test
  void updateFavorite_withInvalidId_shouldThrowException() {
    // Arrange
    when(favoriteRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        FavoriteNotFoundException.class,
        () -> {
          favoriteService.updateFavorite(99L, createRequest);
        });
    verify(favoriteRepository, times(1)).findById(99L);
  }

  @Test
  void deleteFavorite_withValidId_shouldDeleteFavorite() {
    // Arrange
    when(favoriteRepository.existsById(1L)).thenReturn(true);
    doNothing().when(favoriteRepository).deleteById(1L);

    // Act
    favoriteService.deleteFavorite(1L);

    // Assert
    verify(favoriteRepository, times(1)).existsById(1L);
    verify(favoriteRepository, times(1)).deleteById(1L);
  }

  @Test
  void deleteFavorite_withNullId_shouldThrowException() {
    // Act & Assert
    assertThrows(
        FavoriteValidationException.class,
        () -> {
          favoriteService.deleteFavorite(null);
        });
  }

  @Test
  void deleteFavorite_withInvalidId_shouldThrowException() {
    // Arrange
    when(favoriteRepository.existsById(99L)).thenReturn(false);

    // Act & Assert
    assertThrows(
        FavoriteNotFoundException.class,
        () -> {
          favoriteService.deleteFavorite(99L);
        });
    verify(favoriteRepository, times(1)).existsById(99L);
    verify(favoriteRepository, never()).deleteById(anyLong());
  }
}
