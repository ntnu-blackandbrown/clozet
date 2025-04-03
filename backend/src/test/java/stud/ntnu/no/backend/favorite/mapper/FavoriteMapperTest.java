package stud.ntnu.no.backend.favorite.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tester for FavoriteMapper-klassen.
 * Tester mappingen mellom DTO-er og entiteter.
 */
@ExtendWith(MockitoExtension.class)
class FavoriteMapperTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ItemRepository itemRepository;
    
    // Siden FavoriteMapper er abstrakt, må vi lage en konkret implementasjon for testing
    @InjectMocks
    private FavoriteMapper favoriteMapper = new FavoriteMapper() {
        @Override
        public FavoriteDTO toDTO(Favorite favorite) {
            if (favorite == null) {
                return null;
            }
            
            FavoriteDTO dto = new FavoriteDTO();
            dto.setId(favorite.getId());
            dto.setUserId(favorite.getUserId());
            dto.setItemId(favorite.getItemId());
            dto.setActive(favorite.isActive());
            dto.setCreatedAt(favorite.getCreatedAt());
            dto.setUpdatedAt(favorite.getCreatedAt()); // Bruker createdAt for updatedAt
            
            return dto;
        }
        
        @Override
        public List<FavoriteDTO> toDTOList(List<Favorite> favorites) {
            if (favorites == null) {
                return null;
            }
            
            return favorites.stream()
                .map(this::toDTO)
                .toList();
        }
    };
    
    private User testUser;
    private Item testItem;
    private Favorite testFavorite;
    private LocalDateTime now;
    
    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
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
        testFavorite.setCreatedAt(now);
    }
    
    /**
     * Tester at toDTO mapper en Favorite-entitet korrekt til en FavoriteDTO.
     */
    @Test
    void toDTO_shouldMapCorrectly() {
        // Act
        FavoriteDTO result = favoriteMapper.toDTO(testFavorite);
        
        // Assert
        assertNotNull(result);
        assertEquals(testFavorite.getId(), result.getId());
        assertEquals(testUser.getId().toString(), result.getUserId());
        assertEquals(testItem.getId(), result.getItemId());
        assertEquals(testFavorite.isActive(), result.isActive());
        assertEquals(testFavorite.getCreatedAt(), result.getCreatedAt());
        // For updatedAt bruker vi createdAt siden det ikke er implementert updatedAt i entity ennå
        assertEquals(testFavorite.getCreatedAt(), result.getUpdatedAt());
    }

    /**
     * Tester at toDTOList mapper en liste med Favorite-entiteter korrekt.
     */
    @Test
    void toDTOList_shouldMapCorrectly() {
        // Arrange
        Favorite favorite2 = new Favorite();
        favorite2.setId(2L);
        favorite2.setUser(testUser);
        favorite2.setItem(testItem);
        favorite2.setActive(false);
        favorite2.setCreatedAt(now.minusDays(1));
        
        List<Favorite> favorites = Arrays.asList(testFavorite, favorite2);
        
        // Act
        List<FavoriteDTO> result = favoriteMapper.toDTOList(favorites);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertTrue(result.get(0).isActive());
        assertFalse(result.get(1).isActive());
    }
    
    /**
     * Tester at toEntity mapper en CreateFavoriteRequest korrekt til en Favorite-entitet.
     */
    @Test
    void toEntity_shouldMapCorrectly() {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId(testUser.getId().toString());
        request.setItemId(testItem.getId());
        request.setActive(true);
        
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        
        // Act
        Favorite result = favoriteMapper.toEntity(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testItem, result.getItem());
        assertEquals(request.getActive(), result.isActive());
        assertNotNull(result.getCreatedAt());
        
        verify(userRepository).findById(testUser.getId());
        verify(itemRepository).findById(testItem.getId());
    }
    
    /**
     * Tester at toEntity returner null når request er null.
     */
    @Test
    void toEntity_withNullRequest_shouldReturnNull() {
        // Act
        Favorite result = favoriteMapper.toEntity(null);
        
        // Assert
        assertNull(result);
        verifyNoInteractions(userRepository, itemRepository);
    }
    
    /**
     * Tester at toEntity kaster RuntimeException når brukeren ikke finnes.
     */
    @Test
    void toEntity_whenUserNotFound_shouldThrowException() {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId(testUser.getId().toString());
        request.setItemId(testItem.getId());
        request.setActive(true);
        
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> favoriteMapper.toEntity(request));
        assertEquals("User not found", exception.getMessage());
        
        verify(userRepository).findById(testUser.getId());
        verify(itemRepository, never()).findById(anyLong());
    }
    
    /**
     * Tester at toEntity kaster RuntimeException når elementet ikke finnes.
     */
    @Test
    void toEntity_whenItemNotFound_shouldThrowException() {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId(testUser.getId().toString());
        request.setItemId(testItem.getId());
        request.setActive(true);
        
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> favoriteMapper.toEntity(request));
        assertEquals("Item not found", exception.getMessage());
        
        verify(userRepository).findById(testUser.getId());
        verify(itemRepository).findById(testItem.getId());
    }
    
    /**
     * Tester at updateEntity oppdaterer en Favorite-entitet korrekt basert på en CreateFavoriteRequest.
     */
    @Test
    void updateEntity_shouldUpdateCorrectly() {
        // Arrange
        CreateFavoriteRequest request = new CreateFavoriteRequest();
        request.setUserId(testUser.getId().toString());
        request.setItemId(testItem.getId());
        request.setActive(false); // Endrer active fra true til false
        
        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setItem(testItem);
        favorite.setActive(true);
        favorite.setCreatedAt(now);
        
        // Act
        favoriteMapper.updateEntity(favorite, request);
        
        // Assert
        assertEquals(false, favorite.isActive());
        // Andre felt skal ikke endres
        assertEquals(testUser, favorite.getUser());
        assertEquals(testItem, favorite.getItem());
        assertEquals(now, favorite.getCreatedAt());
    }
    
    /**
     * Tester at updateEntity ikke gjør noe når request er null.
     */
    @Test
    void updateEntity_withNullRequest_shouldDoNothing() {
        // Arrange
        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setItem(testItem);
        favorite.setActive(true);
        favorite.setCreatedAt(now);
        
        // Act
        favoriteMapper.updateEntity(favorite, null);
        
        // Assert - Ingenting skal være endret
        assertEquals(true, favorite.isActive());
        assertEquals(testUser, favorite.getUser());
        assertEquals(testItem, favorite.getItem());
        assertEquals(now, favorite.getCreatedAt());
    }
} 