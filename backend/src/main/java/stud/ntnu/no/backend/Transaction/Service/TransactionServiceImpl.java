package stud.ntnu.no.backend.Transaction.Service;

import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionRequest;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionRequest;
import stud.ntnu.no.backend.Transaction.Entity.Transaction;
import stud.ntnu.no.backend.Transaction.Mapper.TransactionMapper;
import stud.ntnu.no.backend.Transaction.Repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                 TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
        return transactionMapper.toDTO(transaction);
    }

    @Override
    public TransactionDTO createTransaction(CreateTransactionRequest dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public TransactionDTO updateTransaction(Long id, UpdateTransactionRequest dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));

        if (dto.getStatus() != null) {
            transaction.setStatus(dto.getStatus());
        }
        if (dto.getPaymentMethod() != null) {
            transaction.setPaymentMethod(dto.getPaymentMethod());
        }

        transaction.setUpdatedAt(LocalDateTime.now());
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(updatedTransaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Override
    public List<TransactionDTO> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByCreatedAtBetween(start, end)
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}