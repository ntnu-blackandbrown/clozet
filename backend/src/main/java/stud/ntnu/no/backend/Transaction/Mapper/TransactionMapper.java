package stud.ntnu.no.backend.Transaction.Mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionRequest;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionRequest;
import stud.ntnu.no.backend.Transaction.Entity.Transaction;

@Component
public class TransactionMapper {
    public TransactionDTO toDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getStatus()
        );
    }

    public Transaction toEntity(CreateTransactionRequest request) {
        return new Transaction(
                request.getDescription(),
                request.getAmount(),
                request.getStatus()
        );
    }

    public void updateEntityFromRequest(Transaction transaction, UpdateTransactionRequest request) {
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getStatus() != null) {
            transaction.setStatus(request.getStatus());
        }
    }
}