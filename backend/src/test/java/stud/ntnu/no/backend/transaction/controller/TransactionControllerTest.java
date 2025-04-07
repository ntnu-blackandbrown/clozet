package stud.ntnu.no.backend.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import stud.ntnu.no.backend.transaction.dto.CreateTransactionRequest;
import stud.ntnu.no.backend.transaction.dto.TransactionDTO;
import stud.ntnu.no.backend.transaction.dto.UpdateTransactionRequest;
import stud.ntnu.no.backend.transaction.service.TransactionService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void getAllTransactions_ShouldReturnListOfTransactions() throws Exception {
        // Given
        TransactionDTO transaction1 = new TransactionDTO();
        transaction1.setId(1L);
        transaction1.setBuyerId("user101");
        transaction1.setSellerId("user201");
        transaction1.setItemId(301L);
        
        TransactionDTO transaction2 = new TransactionDTO();
        transaction2.setId(2L);
        transaction2.setBuyerId("user102");
        transaction2.setSellerId("user202");
        transaction2.setItemId(302L);
        
        List<TransactionDTO> transactions = Arrays.asList(transaction1, transaction2);
        
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // When/Then
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].buyerId").value("user101"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].buyerId").value("user102"))
                .andDo(document("transactions-get-all",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Transaction ID"),
                                fieldWithPath("[].buyerId").description("Buyer ID"),
                                fieldWithPath("[].sellerId").description("Seller ID"),
                                fieldWithPath("[].itemId").description("Item ID")
                        )
                ));
                
        verify(transactionService).getAllTransactions();
    }

    @Test
    void getTransactionById_ShouldReturnTransaction() throws Exception {
        // Given
        Long transactionId = 1L;
        
        TransactionDTO transaction = new TransactionDTO();
        transaction.setId(transactionId);
        transaction.setBuyerId("user101");
        transaction.setSellerId("user201");
        transaction.setItemId(301L);
        
        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);

        // When/Then
        mockMvc.perform(get("/api/transactions/" + transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.buyerId").value("user101"))
                .andDo(document("transaction-get-by-id",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("Transaction ID"),
                                fieldWithPath("buyerId").description("Buyer ID"),
                                fieldWithPath("sellerId").description("Seller ID"),
                                fieldWithPath("itemId").description("Item ID")
                        )
                ));
                
        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() throws Exception {
        // Given
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setBuyerId("user101");
        request.setSellerId("user201");
        request.setItemId(301L);
        
        TransactionDTO createdTransaction = new TransactionDTO();
        createdTransaction.setId(1L);
        createdTransaction.setBuyerId("user101");
        createdTransaction.setSellerId("user201");
        createdTransaction.setItemId(301L);
        
        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(createdTransaction);

        // When/Then
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.buyerId").value("user101"))
                .andDo(document("transaction-create",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("buyerId").description("Buyer ID"),
                                fieldWithPath("sellerId").description("Seller ID"),
                                fieldWithPath("itemId").description("Item ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Transaction ID"),
                                fieldWithPath("buyerId").description("Buyer ID"),
                                fieldWithPath("sellerId").description("Seller ID"),
                                fieldWithPath("itemId").description("Item ID")
                        )
                ));
                
        verify(transactionService).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void updateTransaction_ShouldReturnUpdatedTransaction() throws Exception {
        // Given
        Long transactionId = 1L;
        
        UpdateTransactionRequest request = new UpdateTransactionRequest();
        request.setStatus("COMPLETED");
        
        TransactionDTO updatedTransaction = new TransactionDTO();
        updatedTransaction.setId(transactionId);
        updatedTransaction.setBuyerId("user101");
        updatedTransaction.setSellerId("user201");
        updatedTransaction.setItemId(301L);
        updatedTransaction.setStatus("COMPLETED");
        
        when(transactionService.updateTransaction(eq(transactionId), any(UpdateTransactionRequest.class)))
                .thenReturn(updatedTransaction);

        // When/Then
        mockMvc.perform(put("/api/transactions/" + transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andDo(document("transaction-update",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("status").description("Transaction status")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Transaction ID"),
                                fieldWithPath("buyerId").description("Buyer ID"),
                                fieldWithPath("sellerId").description("Seller ID"),
                                fieldWithPath("itemId").description("Item ID"),
                                fieldWithPath("status").description("Transaction status")
                        )
                ));
                
        verify(transactionService).updateTransaction(eq(transactionId), any(UpdateTransactionRequest.class));
    }

    @Test
    void deleteTransaction_ShouldReturnNoContent() throws Exception {
        // Given
        Long transactionId = 1L;
        doNothing().when(transactionService).deleteTransaction(transactionId);

        // When/Then
        mockMvc.perform(delete("/api/transactions/" + transactionId))
                .andExpect(status().isNoContent())
                .andDo(document("transaction-delete",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint())
                ));
                
        verify(transactionService).deleteTransaction(transactionId);
    }

    @Test
    void getTransactionsBetweenDates_ShouldReturnFilteredTransactions() throws Exception {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);
        
        TransactionDTO transaction1 = new TransactionDTO();
        transaction1.setId(1L);
        transaction1.setBuyerId("user101");
        transaction1.setSellerId("user201");
        transaction1.setItemId(301L);
        
        TransactionDTO transaction2 = new TransactionDTO();
        transaction2.setId(2L);
        transaction2.setBuyerId("user102");
        transaction2.setSellerId("user202");
        transaction2.setItemId(302L);
        
        List<TransactionDTO> transactions = Arrays.asList(transaction1, transaction2);
        
        when(transactionService.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(transactions);

        // When/Then
        mockMvc.perform(get("/api/transactions/between")
                .param("start", start.toString())
                .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andDo(document("transactions-between-dates",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Transaction ID"),
                                fieldWithPath("[].buyerId").description("Buyer ID"),
                                fieldWithPath("[].sellerId").description("Seller ID"),
                                fieldWithPath("[].itemId").description("Item ID")
                        )
                ));
                
        verify(transactionService).findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
} 