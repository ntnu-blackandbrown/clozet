package stud.ntnu.no.backend.history.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.entity.History;
import stud.ntnu.no.backend.history.exception.HistoryNotFoundException;
import stud.ntnu.no.backend.history.exception.HistoryValidationException;
import stud.ntnu.no.backend.history.mapper.HistoryMapper;
import stud.ntnu.no.backend.history.repository.HistoryRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.exception.ItemNotFoundException;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryServiceImpl implements HistoryService {
    
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final HistoryMapper historyMapper;
    
    /**
     * Constructs a new HistoryServiceImpl with the specified repositories and mapper.
     *
     * @param historyRepository the history repository
     * @param userRepository the user repository
     * @param itemRepository the item repository
     * @param historyMapper the history mapper
     */
    public HistoryServiceImpl(
            HistoryRepository historyRepository,
            UserRepository userRepository,
            ItemRepository itemRepository,
            HistoryMapper historyMapper) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.historyMapper = historyMapper;
    }

    /**
     * Adds an item to the user's history.
     *
     * @param userId the user ID
     * @param itemId the item ID
     * @return the history DTO
     * @throws UserNotFoundException if the user is not found
     * @throws ItemNotFoundException if the item is not found
     * @throws HistoryValidationException if the user ID or item ID is null
     */
    @Override
    @Transactional
    public HistoryDTO addToHistory(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId));

        if (userId == null || itemId == null) {
            throw new HistoryValidationException("User ID and Item ID cannot be null");
        }

        Optional<History> existingHistory = historyRepository.findByUserAndItem(user, item);
        if (existingHistory.isPresent()) {
            History history = existingHistory.get();
            history.setViewedAt(LocalDateTime.now());
            history.setActive(true);
            return historyMapper.toDto(historyRepository.save(history));
        }

        History history = new History();
        history.setUser(user);
        history.setItem(item);
        history.setViewedAt(LocalDateTime.now());
        history.setActive(true);

        return historyMapper.toDto(historyRepository.save(history));
    }

    /**
     * Removes an item from the user's history.
     *
     * @param userId the user ID
     * @param itemId the item ID
     * @throws UserNotFoundException if the user is not found
     * @throws ItemNotFoundException if the item is not found
     * @throws HistoryNotFoundException if the history entry is not found
     */
    @Override
    @Transactional
    public void removeFromHistory(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId));

        History history = historyRepository.findByUserAndItem(user, item)
            .orElseThrow(() -> new HistoryNotFoundException("History entry not found for user ID: " + userId + " and item ID: " + itemId));

        historyRepository.delete(history);
    }
    
    /**
     * Deletes all history entries for a user.
     *
     * @param userId the user ID
     * @throws RuntimeException if the user is not found
     */
    @Override
    @Transactional
    public void deleteHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        historyRepository.deleteByUser(user);
    }
    
    /**
     * Pauses or resumes the user's history.
     *
     * @param userId the user ID
     * @param pause true to pause, false to resume
     * @throws RuntimeException if the user is not found
     */
    @Override
    @Transactional
    public void pauseHistory(Long userId, boolean pause) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        List<History> userHistory = historyRepository.findByUserOrderByViewedAtDesc(user);
        for (History history : userHistory) {
            history.setActive(!pause);
        }
        historyRepository.saveAll(userHistory);
    }
    
    /**
     * Retrieves the user's active history.
     *
     * @param userId the user ID
     * @return the list of history DTOs
     * @throws RuntimeException if the user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public List<HistoryDTO> getUserHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return historyMapper.toDtoList(
            historyRepository.findByUserAndActiveOrderByViewedAtDesc(user, true)
        );
    }
}
