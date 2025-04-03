package stud.ntnu.no.backend.History.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.history.controller.HistoryController;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.service.HistoryService;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {
    
    @Mock
    private HistoryService historyService;
    
    @InjectMocks
    private HistoryController historyController;
    
    private User testUser;
    private HistoryDTO historyDTO1;
    private HistoryDTO historyDTO2;
    private List<HistoryDTO> historyDTOs;
    
    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setActive(true);
        testUser.setRole("ROLE_USER");
        
        // Setup history DTOs
        historyDTO1 = new HistoryDTO();
        historyDTO1.setId(1L);
        historyDTO1.setUserId(1L);
        historyDTO1.setItemId(1L);
        historyDTO1.setItemTitle("Item 1");
        historyDTO1.setViewedAt(LocalDateTime.now());
        historyDTO1.setActive(true);
        
        historyDTO2 = new HistoryDTO();
        historyDTO2.setId(2L);
        historyDTO2.setUserId(1L);
        historyDTO2.setItemId(2L);
        historyDTO2.setItemTitle("Item 2");
        historyDTO2.setViewedAt(LocalDateTime.now());
        historyDTO2.setActive(true);
        
        historyDTOs = Arrays.asList(historyDTO1, historyDTO2);
    }
    
    @Test
    void addToHistory() {
        // Arrange
        Long itemId = 1L;
        when(historyService.addToHistory(eq(1L), eq(itemId))).thenReturn(historyDTO1);
        
        // Act
        HistoryDTO result = historyController.addToHistory(itemId, testUser);
        
        // Assert
        assertEquals(historyDTO1, result);
        verify(historyService).addToHistory(testUser.getId(), itemId);
    }
    
    @Test
    void removeFromHistory() {
        // Arrange
        Long itemId = 1L;
        doNothing().when(historyService).removeFromHistory(eq(1L), eq(itemId));
        
        // Act
        historyController.removeFromHistory(itemId, testUser);
        
        // Assert
        verify(historyService).removeFromHistory(testUser.getId(), itemId);
    }
    
    @Test
    void deleteHistory() {
        // Arrange
        doNothing().when(historyService).deleteHistory(eq(1L));
        
        // Act
        historyController.deleteHistory(testUser);
        
        // Assert
        verify(historyService).deleteHistory(testUser.getId());
    }
    
    @Test
    void pauseHistory() {
        // Arrange
        boolean pause = true;
        doNothing().when(historyService).pauseHistory(eq(1L), eq(pause));
        
        // Act
        historyController.pauseHistory(pause, testUser);
        
        // Assert
        verify(historyService).pauseHistory(testUser.getId(), pause);
    }
    
    @Test
    void getUserHistory() {
        // Arrange
        when(historyService.getUserHistory(eq(1L))).thenReturn(historyDTOs);
        
        // Act
        List<HistoryDTO> result = historyController.getUserHistory(testUser);
        
        // Assert
        assertEquals(historyDTOs, result);
        verify(historyService).getUserHistory(testUser.getId());
    }
}