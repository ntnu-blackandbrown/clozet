package stud.ntnu.no.backend.transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the TransactionService interface.
 * <p>
 * This class provides methods for managing transactions, including CRUD operations.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ItemRepository itemRepository;

    /**
     * Constructs a new TransactionServiceImpl with the specified dependencies.
     *
     * @param transactionRepository the TransactionRepository
     * @param transactionMapper the TransactionMapper
     * @param itemRepository the ItemRepository
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository, 
                                  TransactionMapper transactionMapper,
                                  ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        logger.info("Retrieving all transactions");
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        logger.info("Retrieving transaction with ID: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return transactionMapper.toDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO createTransaction(CreateTransactionRequest request) {
        logger.info("Creating a new transaction");
        if (request == null) {
            logger.error("Transaction request is null");
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
        logger.info("Updating transaction with ID: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        logger.info("Deleting transaction with ID: {}", id);
        if (!transactionRepository.existsById(id)) {
            logger.error("Transaction with ID: {} not found", id);
            throw new TransactionNotFoundException(id);
        }
        transactionRepository.deleteById(id);
    }

    @Override
    public List<TransactionDTO> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        logger.info("Finding transactions between {} and {}", start, end);
        return transactionRepository.findByCreatedAtBetween(start, end)
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByBuyerId(String buyerId) {
        logger.info("Retrieving transactions for buyerId: {}", buyerId);
        return transactionRepository.findByBuyerId(buyerId)
                .stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDTO handlePurchaseTransaction(CreateTransactionRequest request) {
        logger.info("Handling purchase transaction for item ID: {}", request.getItemId());

        // Convert request to entity and set any default values
        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setCreatedAt(LocalDateTime.now());

        // Save the transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Deactivate the item
        Item item = transaction.getItem();
        if (item != null) {
            item.setAvailable(false);
            itemRepository.save(item);
            logger.info("Item ID: {} set to unavailable", item.getId());
        }

        // Return the saved transaction as DTO
        return transactionMapper.toDTO(savedTransaction);
    }
}
