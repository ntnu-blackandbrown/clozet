package stud.ntnu.no.backend.history.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.service.HistoryService;
import stud.ntnu.no.backend.user.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;
    
    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }
    
    /**
     * Adds an item to the user's history.
     * @param itemId the ID of the item to add
     * @param user the authenticated user
     * @return the added HistoryDTO
     */
    @PostMapping("/add/{itemId}")
    @ResponseStatus(HttpStatus.CREATED)
    public HistoryDTO addToHistory(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
        return historyService.addToHistory(user.getId(), itemId);
    }
    
    /**
     * Removes an item from the user's history.
     * @param itemId the ID of the item to remove
     * @param user the authenticated user
     */
    @DeleteMapping("/remove/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromHistory(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
        historyService.removeFromHistory(user.getId(), itemId);
    }
    
    /**
     * Clears the user's history.
     * @param user the authenticated user
     */
    @DeleteMapping("/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHistory(@AuthenticationPrincipal User user) {
        historyService.deleteHistory(user.getId());
    }
    
    /**
     * Pauses or resumes the user's history tracking.
     * @param pause true to pause, false to resume
     * @param user the authenticated user
     */
    @PostMapping("/pause/{pause}")
    @ResponseStatus(HttpStatus.OK)
    public void pauseHistory(@PathVariable boolean pause, @AuthenticationPrincipal User user) {
        historyService.pauseHistory(user.getId(), pause);
    }
    
    /**
     * Retrieves the user's history.
     * @param user the authenticated user
     * @return a list of HistoryDTOs
     */
    @GetMapping
    public List<HistoryDTO> getUserHistory(@AuthenticationPrincipal User user) {
        return historyService.getUserHistory(user.getId());
    }
}
