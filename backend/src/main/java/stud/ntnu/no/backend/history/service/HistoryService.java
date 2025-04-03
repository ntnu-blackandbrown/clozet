package stud.ntnu.no.backend.history.service;

import stud.ntnu.no.backend.history.dto.HistoryDTO;

import java.util.List;

public interface HistoryService {
    HistoryDTO addToHistory(Long userId, Long itemId);
    void removeFromHistory(Long userId, Long itemId);
    void deleteHistory(Long userId);
    void pauseHistory(Long userId, boolean pause);
    List<HistoryDTO> getUserHistory(Long userId);
}