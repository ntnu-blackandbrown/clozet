package stud.ntnu.no.backend.history.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.no.backend.history.dto.HistoryDTO;
import stud.ntnu.no.backend.history.service.HistoryService;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class HistoryControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HistoryService historyService;

    @Mock
    private User mockUser;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HistoryController historyController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        // Setup authentication with mock user
        when(mockUser.getId()).thenReturn(123L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        mockMvc = MockMvcBuilders.standaloneSetup(historyController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void addToHistory_ShouldReturnAddedHistory() throws Exception {
        // Given
        Long itemId = 101L;
        
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setId(1L);
        historyDTO.setUserId(123L);
        historyDTO.setItemId(itemId);
        historyDTO.setViewedAt(LocalDateTime.now());
        
        when(historyService.addToHistory(anyLong(), anyLong())).thenReturn(historyDTO);

        // When/Then
        mockMvc.perform(post("/api/history/add/" + itemId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(123))
                .andExpect(jsonPath("$.itemId").value(101))
                .andDo(document("history-add",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("History entry ID"),
                                fieldWithPath("userId").description("User ID"),
                                fieldWithPath("itemId").description("Item ID"),
                                fieldWithPath("viewedAt").description("Timestamp when the item was viewed")
                        )
                ));
                
        verify(historyService).addToHistory(anyLong(), eq(itemId));
    }

    @Test
    void removeFromHistory_ShouldReturnNoContent() throws Exception {
        // Given
        Long itemId = 101L;
        doNothing().when(historyService).removeFromHistory(anyLong(), anyLong());

        // When/Then
        mockMvc.perform(delete("/api/history/remove/" + itemId))
                .andExpect(status().isNoContent())
                .andDo(document("history-remove",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint())
                ));
                
        verify(historyService).removeFromHistory(anyLong(), eq(itemId));
    }

    @Test
    void deleteHistory_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(historyService).deleteHistory(anyLong());

        // When/Then
        mockMvc.perform(delete("/api/history/clear"))
                .andExpect(status().isNoContent())
                .andDo(document("history-clear",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint())
                ));
                
        verify(historyService).deleteHistory(anyLong());
    }

    @Test
    void pauseHistory_ShouldReturnOk() throws Exception {
        // Given
        boolean pause = true;
        doNothing().when(historyService).pauseHistory(anyLong(), eq(pause));

        // When/Then
        mockMvc.perform(post("/api/history/pause/" + pause))
                .andExpect(status().isOk())
                .andDo(document("history-pause",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint())
                ));
                
        verify(historyService).pauseHistory(anyLong(), eq(pause));
    }

    @Test
    void getUserHistory_ShouldReturnHistoryList() throws Exception {
        // Given
        HistoryDTO historyDTO1 = new HistoryDTO();
        historyDTO1.setId(1L);
        historyDTO1.setUserId(123L);
        historyDTO1.setItemId(101L);
        historyDTO1.setViewedAt(LocalDateTime.now().minusDays(1));
        
        HistoryDTO historyDTO2 = new HistoryDTO();
        historyDTO2.setId(2L);
        historyDTO2.setUserId(123L);
        historyDTO2.setItemId(102L);
        historyDTO2.setViewedAt(LocalDateTime.now());
        
        List<HistoryDTO> historyList = Arrays.asList(historyDTO1, historyDTO2);
        
        when(historyService.getUserHistory(anyLong())).thenReturn(historyList);

        // When/Then
        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(123))
                .andExpect(jsonPath("$[0].itemId").value(101))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].itemId").value(102))
                .andDo(document("history-get",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("History entry ID"),
                                fieldWithPath("[].userId").description("User ID"),
                                fieldWithPath("[].itemId").description("Item ID"),
                                fieldWithPath("[].viewedAt").description("Timestamp when the item was viewed")
                        )
                ));
                
        verify(historyService).getUserHistory(anyLong());
    }
} 