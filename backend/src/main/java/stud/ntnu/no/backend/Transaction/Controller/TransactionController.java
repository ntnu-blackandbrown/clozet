package stud.ntnu.no.backend.Transaction.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionRequest;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionRequest;
import stud.ntnu.no.backend.Transaction.Service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody CreateTransactionRequest request) {
        CreateTransactionDTO dto = convertToCreateDto(request);
        return new ResponseEntity<>(transactionService.createTransaction(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @RequestBody UpdateTransactionRequest request) {
        UpdateTransactionDTO dto = convertToUpdateDto(request);
        return ResponseEntity.ok(transactionService.updateTransaction(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
    
    // Adapter methods to convert between Request and DTO objects
    private CreateTransactionDTO convertToCreateDto(CreateTransactionRequest request) {
        CreateTransactionDTO dto = new CreateTransactionDTO();
        dto.setBuyerId(request.getBuyerId());
        dto.setSellerId(request.getSellerId());
        dto.setAmount(request.getAmount());
        dto.setStatus(request.getStatus());
        dto.setPaymentMethod(request.getPaymentMethod());
        dto.setItemId(request.getItemId());
        return dto;
    }
    
    private UpdateTransactionDTO convertToUpdateDto(UpdateTransactionRequest request) {
        UpdateTransactionDTO dto = new UpdateTransactionDTO();
        dto.setAmount(request.getAmount());
        dto.setStatus(request.getStatus());
        dto.setPaymentMethod(request.getPaymentMethod());
        return dto;
    }
}