package stud.ntnu.no.backend.History.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.config.TestSecurityConfig;
import stud.ntnu.no.backend.history.controller.HistoryController;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.service.HistoryService;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HistoryController.class)
@Import(TestSecurityConfig.class)
class HistoryControllerTest {
    // Rest of your test class remains the same
    
    @Test
    @WithMockUser(username = "testuser")
    void addToHistory() throws Exception {
        when(historyService.addToHistory(anyLong(), eq(1L))).thenReturn(historyDTO1);

        mockMvc.perform(post("/api/history/add/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemId").value(1));

        verify(historyService).addToHistory(anyLong(), eq(1L));
    }
    
    // Update other test methods similarly
}