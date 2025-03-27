package stud.ntnu.no.backend.Transaction.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionRequest;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionRequest;
import stud.ntnu.no.backend.Transaction.Entity.Transaction;
import stud.ntnu.no.backend.Transaction.Exceptions.TransactionNotFoundException;
import stud.ntnu.no.backend.Transaction.Mapper.TransactionMapper;
import stud.ntnu.no.backend.Transaction.Repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
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
    public TransactionDTO createTransaction(CreateTransactionRequest request) {
        Transaction transaction = transactionMapper.toEntity(request);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public TransactionDTO updateTransaction(Long id, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionMapper.updateEntityFromRequest(transaction, request);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(updatedTransaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new TransactionNotFoundException(id);
        }
        transactionRepository.deleteById(id);
    }
}