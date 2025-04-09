package stud.ntnu.no.backend.history.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.service.HistoryService;
import stud.ntnu.no.backend.user.entity.User;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

  private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
  private final HistoryService historyService;

  @Autowired
  public HistoryController(HistoryService historyService) {
    this.historyService = historyService;
  }

  /**
   * Adds an item to the user's history.
   *
   * @param itemId the ID of the item to add
   * @param user the authenticated user
   * @return the added HistoryDTO
   */
  @PostMapping("/add/{itemId}")
  @ResponseStatus(HttpStatus.CREATED)
  public HistoryDTO addToHistory(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
    logger.info("Adding item {} to history for user {}", itemId, user.getId());
    HistoryDTO historyDTO = historyService.addToHistory(user.getId(), itemId);
    logger.info("Item {} added to history for user {}", itemId, user.getId());
    return historyDTO;
  }

  /**
   * Removes an item from the user's history.
   *
   * @param itemId the ID of the item to remove
   * @param user the authenticated user
   */
  @DeleteMapping("/remove/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeFromHistory(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
    logger.info("Removing item {} from history for user {}", itemId, user.getId());
    historyService.removeFromHistory(user.getId(), itemId);
    logger.info("Item {} removed from history for user {}", itemId, user.getId());
  }

  /**
   * Clears the user's history.
   *
   * @param user the authenticated user
   */
  @DeleteMapping("/clear")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteHistory(@AuthenticationPrincipal User user) {
    logger.info("Clearing history for user {}", user.getId());
    historyService.deleteHistory(user.getId());
    logger.info("History cleared for user {}", user.getId());
  }

  /**
   * Pauses or resumes the user's history tracking.
   *
   * @param pause true to pause, false to resume
   * @param user the authenticated user
   */
  @PostMapping("/pause/{pause}")
  @ResponseStatus(HttpStatus.OK)
  public void pauseHistory(@PathVariable boolean pause, @AuthenticationPrincipal User user) {
    logger.info("Pausing history for user {} - {}", user.getId(), pause);
    historyService.pauseHistory(user.getId(), pause);
    logger.info("History paused for user {} - {}", user.getId(), pause);
  }

  /**
   * Retrieves the user's history.
   *
   * @param user the authenticated user
   * @return a list of HistoryDTOs
   */
  @GetMapping
  public List<HistoryDTO> getUserHistory(@AuthenticationPrincipal User user) {
    logger.info("Getting history for user {}", user.getId());
    List<HistoryDTO> historyDTOs = historyService.getUserHistory(user.getId());
    logger.info("History retrieved for user {}", user.getId());
    return historyDTOs;
  }
}
