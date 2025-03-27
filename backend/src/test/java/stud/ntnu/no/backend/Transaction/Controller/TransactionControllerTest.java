package stud.ntnu.no.backend.Transaction.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionRequest;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionRequest;
import stud.ntnu.no.backend.Transaction.Exceptions.TransactionNotFoundException;
import stud.ntnu.no.backend.Transaction.Service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDTO transactionDTO;
    private List<TransactionDTO> transactionDTOList;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO(
                1L,
                "Test Transaction",
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                "COMPLETED"
        );

        transactionDTOList = Arrays.asList(
                transactionDTO,
                new TransactionDTO(
                        2L,
                        "Another Transaction",
                        new BigDecimal("200.00"),
                        LocalDateTime.now(),
                        "PENDING"
                )
        );
    }

    @Test
    void getAllTransactions_shouldReturnListOfTransactions() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(transactionDTOList);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(transactionService).getAllTransactions();
    }

    @Test
    void getTransactionById_withValidId_shouldReturnTransaction() throws Exception {
        when(transactionService.getTransactionById(1L)).thenReturn(transactionDTO);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Transaction")));

        verify(transactionService).getTransactionById(1L);
    }

    @Test
    void getTransactionById_withInvalidId_shouldReturnNotFound() throws Exception {
        when(transactionService.getTransactionById(99L)).thenThrow(new TransactionNotFoundException(99L));

        mockMvc.perform(get("/api/transactions/99"))
                .andExpect(status().isNotFound());

        verify(transactionService).getTransactionById(99L);
    }

    @Test
    void createTransaction_shouldReturnCreatedTransaction() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                "New Transaction",
                new BigDecimal("150.00"),
                "PENDING"
        );

        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(transactionDTO);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Transaction")));

        verify(transactionService).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void updateTransaction_shouldReturnUpdatedTransaction() throws Exception {
        UpdateTransactionRequest request = new UpdateTransactionRequest(
                "Updated Transaction",
                new BigDecimal("175.00"),
                "COMPLETED"
        );

        when(transactionService.updateTransaction(eq(1L), any(UpdateTransactionRequest.class))).thenReturn(transactionDTO);

        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(transactionService).updateTransaction(eq(1L), any(UpdateTransactionRequest.class));
    }

    @Test
    void deleteTransaction_shouldReturnNoContent() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());

        verify(transactionService).deleteTransaction(1L);
    }
}