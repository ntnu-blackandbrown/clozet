package stud.ntnu.no.backend.history.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.entity.History;
import stud.ntnu.no.backend.history.exception.HistoryNotFoundException;
import stud.ntnu.no.backend.history.mapper.HistoryMapper;
import stud.ntnu.no.backend.history.repository.HistoryRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.exception.ItemNotFoundException;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceImplTest {

  @Mock private HistoryRepository historyRepository;

  @Mock private UserRepository userRepository;

  @Mock private ItemRepository itemRepository;

  @Mock private HistoryMapper historyMapper;

  @InjectMocks private HistoryServiceImpl historyService;

  private User user;
  private Item item;
  private History history;
  private HistoryDTO historyDTO;

  @BeforeEach
  void setUp() {
    // Opprett test data
    user = new User();
    user.setId(1L);
    user.setUsername("testuser");

    item = new Item();
    item.setId(1L);
    item.setTitle("Test Item");

    history = new History();
    history.setId(1L);
    history.setUser(user);
    history.setItem(item);
    history.setViewedAt(LocalDateTime.now());
    history.setActive(true);

    historyDTO = new HistoryDTO();
    historyDTO.setId(1L);
    historyDTO.setUserId(1L);
    historyDTO.setItemId(1L);
    historyDTO.setViewedAt(LocalDateTime.now());
    historyDTO.setActive(true);
  }

  @Test
  void addToHistory_withNewHistory_shouldCreateAndReturnHistory() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(historyRepository.findByUserAndItem(user, item)).thenReturn(Optional.empty());
    when(historyRepository.save(any(History.class))).thenReturn(history);
    when(historyMapper.toDto(history)).thenReturn(historyDTO);

    // Act
    HistoryDTO result = historyService.addToHistory(1L, 1L);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getUserId());
    assertEquals(1L, result.getItemId());
    verify(userRepository, times(1)).findById(1L);
    verify(itemRepository, times(1)).findById(1L);
    verify(historyRepository, times(1)).findByUserAndItem(user, item);
    verify(historyRepository, times(1)).save(any(History.class));
    verify(historyMapper, times(1)).toDto(history);
  }

  @Test
  void addToHistory_withExistingHistory_shouldUpdateAndReturnHistory() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(historyRepository.findByUserAndItem(user, item)).thenReturn(Optional.of(history));
    when(historyRepository.save(history)).thenReturn(history);
    when(historyMapper.toDto(history)).thenReturn(historyDTO);

    // Act
    HistoryDTO result = historyService.addToHistory(1L, 1L);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getUserId());
    assertEquals(1L, result.getItemId());
    verify(userRepository, times(1)).findById(1L);
    verify(itemRepository, times(1)).findById(1L);
    verify(historyRepository, times(1)).findByUserAndItem(user, item);
    verify(historyRepository, times(1)).save(history);
    verify(historyMapper, times(1)).toDto(history);
  }

  @Test
  void addToHistory_withInvalidUserId_shouldThrowException() {
    // Arrange
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          historyService.addToHistory(99L, 1L);
        });
    verify(userRepository, times(1)).findById(99L);
    verify(itemRepository, never()).findById(anyLong());
  }

  @Test
  void addToHistory_withInvalidItemId_shouldThrowException() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ItemNotFoundException.class,
        () -> {
          historyService.addToHistory(1L, 99L);
        });
    verify(userRepository, times(1)).findById(1L);
    verify(itemRepository, times(1)).findById(99L);
  }

  @Test
  void addToHistory_withNullIds_shouldThrowException() {
    // Act & Assert
    assertThrows(
        UserNotFoundException.class,
        () -> {
          historyService.addToHistory(null, 1L);
        });
  }

  @Test
  void removeFromHistory_withValidData_shouldRemoveHistory() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(historyRepository.findByUserAndItem(user, item)).thenReturn(Optional.of(history));
    doNothing().when(historyRepository).delete(history);

    // Act
    historyService.removeFromHistory(1L, 1L);

    // Assert
    verify(userRepository, times(1)).findById(1L);
    verify(itemRepository, times(1)).findById(1L);
    verify(historyRepository, times(1)).findByUserAndItem(user, item);
    verify(historyRepository, times(1)).delete(history);
  }

  @Test
  void removeFromHistory_withInvalidHistory_shouldThrowException() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(historyRepository.findByUserAndItem(user, item)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        HistoryNotFoundException.class,
        () -> {
          historyService.removeFromHistory(1L, 1L);
        });
    verify(userRepository, times(1)).findById(1L);
    verify(itemRepository, times(1)).findById(1L);
    verify(historyRepository, times(1)).findByUserAndItem(user, item);
    verify(historyRepository, never()).delete(any(History.class));
  }

  @Test
  void deleteHistory_withValidId_shouldDeleteAllUserHistory() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    doNothing().when(historyRepository).deleteByUser(user);

    // Act
    historyService.deleteHistory(1L);

    // Assert
    verify(userRepository, times(1)).findById(1L);
    verify(historyRepository, times(1)).deleteByUser(user);
  }

  @Test
  void deleteHistory_withInvalidId_shouldThrowException() {
    // Arrange
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> {
          historyService.deleteHistory(99L);
        });
    verify(userRepository, times(1)).findById(99L);
    verify(historyRepository, never()).deleteByUser(any(User.class));
  }

  @Test
  void pauseHistory_shouldUpdateAllUserHistory() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    List<History> historyList = Arrays.asList(history);
    when(historyRepository.findByUserOrderByViewedAtDesc(user)).thenReturn(historyList);
    when(historyRepository.saveAll(historyList)).thenReturn(historyList);

    // Act
    historyService.pauseHistory(1L, true);

    // Assert
    assertFalse(history.isActive()); // Should be set to inactive
    verify(userRepository, times(1)).findById(1L);
    verify(historyRepository, times(1)).findByUserOrderByViewedAtDesc(user);
    verify(historyRepository, times(1)).saveAll(historyList);
  }

  @Test
  void getUserHistory_shouldReturnActiveUserHistory() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    List<History> historyList = Arrays.asList(history);
    when(historyRepository.findByUserAndActiveOrderByViewedAtDesc(user, true))
        .thenReturn(historyList);
    when(historyMapper.toDtoList(historyList)).thenReturn(Arrays.asList(historyDTO));

    // Act
    List<HistoryDTO> result = historyService.getUserHistory(1L);

    // Assert
    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getUserId());
    assertEquals(1L, result.get(0).getItemId());
    verify(userRepository, times(1)).findById(1L);
    verify(historyRepository, times(1)).findByUserAndActiveOrderByViewedAtDesc(user, true);
    verify(historyMapper, times(1)).toDtoList(historyList);
  }

  @Test
  void getUserHistory_withInvalidId_shouldThrowException() {
    // Arrange
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        RuntimeException.class,
        () -> {
          historyService.getUserHistory(99L);
        });
    verify(userRepository, times(1)).findById(99L);
    verify(historyRepository, never())
        .findByUserAndActiveOrderByViewedAtDesc(any(User.class), anyBoolean());
  }
}
