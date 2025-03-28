package stud.ntnu.no.backend.transaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.transaction.dto.CreateTransactionRequest;
import stud.ntnu.no.backend.transaction.dto.TransactionDTO;
import stud.ntnu.no.backend.transaction.dto.UpdateTransactionRequest;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.transaction.exception.TransactionNotFoundException;
import stud.ntnu.no.backend.transaction.exception.TransactionValidationException;
import stud.ntnu.no.backend.transaction.mapper.TransactionMapper;
import stud.ntnu.no.backend.transaction.repository.TransactionRepository;
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
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return transactionMapper.toDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO createTransaction(CreateTransactionRequest request) {
        if (request == null) {
            throw new TransactionValidationException("Transaction request cannot be null");
        }
        
        // Convert request to entity and set any default values
        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setCreatedAt(LocalDateTime.now());
        
        // Save the transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Return the saved transaction as DTO
        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(Long id, UpdateTransactionRequest dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        // Update logic remains the same
        // (Assuming this would update fields based on the dto)
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new TransactionNotFoundException(id);
        }
        transactionRepository.deleteById(id);
    }

    @Override
    public List<TransactionDTO> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }
}