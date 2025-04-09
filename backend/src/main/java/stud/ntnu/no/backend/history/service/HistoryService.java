package stud.ntnu.no.backend.history.service;

import java.util.List;
import stud.ntnu.no.backend.history.dto.HistoryDTO;

public interface HistoryService {

  /**
   * Adds an item to the user's history.
   *
   * @param userId the user ID
   * @param itemId the item ID
   * @return the history DTO
   */
  HistoryDTO addToHistory(Long userId, Long itemId);

  /**
   * Removes an item from the user's history.
   *
   * @param userId the user ID
   * @param itemId the item ID
   */
  void removeFromHistory(Long userId, Long itemId);

  /**
   * Deletes all history entries for a user.
   *
   * @param userId the user ID
   */
  void deleteHistory(Long userId);

  /**
   * Pauses or resumes the user's history.
   *
   * @param userId the user ID
   * @param pause  true to pause, false to resume
   */
  void pauseHistory(Long userId, boolean pause);

  /**
   * Retrieves the user's active history.
   *
   * @param userId the user ID
   * @return the list of history DTOs
   */
  List<HistoryDTO> getUserHistory(Long userId);
}
