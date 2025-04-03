package stud.ntnu.no.backend.History.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import stud.ntnu.no.backend.history.entity.History;
import stud.ntnu.no.backend.history.repository.HistoryRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class HistoryRepositoryTest {

    @MockBean
    private HistoryRepository historyRepository;

    private User testUser;
    private Item testItem1;
    private Item testItem2;
    private History history1;
    private History history2;

    @BeforeEach
    void setUp() {
        // Create entities without persistence
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testItem1 = new Item();
        testItem1.setId(1L);
        testItem1.setTitle("Item 1");

        testItem2 = new Item();
        testItem2.setId(2L);
        testItem2.setTitle("Item 2");

        // Create test histories
        history1 = new History();
        history1.setId(1L);
        history1.setUser(testUser);
        history1.setItem(testItem1);
        history1.setViewedAt(LocalDateTime.now().minusHours(1));
        history1.setActive(true);

        history2 = new History();
        history2.setId(2L);
        history2.setUser(testUser);
        history2.setItem(testItem2);
        history2.setViewedAt(LocalDateTime.now());
        history2.setActive(true);
    }

    @Test
    void findByUserOrderByViewedAtDesc() {
        // Configure mock behavior
        when(historyRepository.findByUserOrderByViewedAtDesc(testUser))
                .thenReturn(Arrays.asList(history2, history1));

        // Execute test
        List<History> histories = historyRepository.findByUserOrderByViewedAtDesc(testUser);

        // Verify results
        assertEquals(2, histories.size());
        assertEquals(history2.getId(), histories.get(0).getId());
        assertEquals(history1.getId(), histories.get(1).getId());

        // Verify mock was called
        verify(historyRepository).findByUserOrderByViewedAtDesc(testUser);
    }

    @Test
    void findByUserAndActiveOrderByViewedAtDesc() {
        // Configure mock
        when(historyRepository.findByUserAndActiveOrderByViewedAtDesc(testUser, true))
                .thenReturn(List.of(history1));

        // Execute test
        List<History> histories = historyRepository.findByUserAndActiveOrderByViewedAtDesc(testUser, true);

        // Verify results
        assertEquals(1, histories.size());
        assertEquals(history1.getId(), histories.get(0).getId());
        
        verify(historyRepository).findByUserAndActiveOrderByViewedAtDesc(testUser, true);
    }

    @Test
    void findByUserAndItem() {
        // Configure mock
        when(historyRepository.findByUserAndItem(testUser, testItem1))
                .thenReturn(Optional.of(history1));

        // Execute test
        Optional<History> foundHistory = historyRepository.findByUserAndItem(testUser, testItem1);

        // Verify results
        assertTrue(foundHistory.isPresent());
        assertEquals(history1.getId(), foundHistory.get().getId());
        
        verify(historyRepository).findByUserAndItem(testUser, testItem1);
    }

    @Test
    void deleteByUser() {
        // Execute test
        historyRepository.deleteByUser(testUser);

        // Verify the method was called with the correct argument
        verify(historyRepository).deleteByUser(testUser);
    }
}