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
    
    @Override
    @Transactional
    public void deleteHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        historyRepository.deleteByUser(user);
    }
    
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