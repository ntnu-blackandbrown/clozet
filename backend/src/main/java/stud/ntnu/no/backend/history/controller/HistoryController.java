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
    
    @PostMapping("/add/{itemId}")
    @ResponseStatus(HttpStatus.CREATED)
    public HistoryDTO addToHistory(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
        return historyService.addToHistory(user.getId(), itemId);
    }
    
    @DeleteMapping("/remove/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromHistory(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
        historyService.removeFromHistory(user.getId(), itemId);
    }
    
    @DeleteMapping("/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHistory(@AuthenticationPrincipal User user) {
        historyService.deleteHistory(user.getId());
    }
    
    @PostMapping("/pause/{pause}")
    @ResponseStatus(HttpStatus.OK)
    public void pauseHistory(@PathVariable boolean pause, @AuthenticationPrincipal User user) {
        historyService.pauseHistory(user.getId(), pause);
    }
    
    @GetMapping
    public List<HistoryDTO> getUserHistory(@AuthenticationPrincipal User user) {
        return historyService.getUserHistory(user.getId());
    }
}