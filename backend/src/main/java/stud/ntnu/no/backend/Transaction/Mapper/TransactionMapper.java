package stud.ntnu.no.backend.Transaction.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.Item.Entity.Item;
import stud.ntnu.no.backend.Item.Repository.ItemRepository;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.UpdateTransactionDTO;
import stud.ntnu.no.backend.Transaction.Entity.Transaction;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {

    @Autowired
    private ItemRepository itemRepository;

    public TransactionDTO toDto(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getBuyerId(),
                transaction.getSellerId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getPaymentMethod(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt(),
                transaction.getItem() != null ? transaction.getItem().getId() : null
        );
    }

    public List<TransactionDTO> toDtoList(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Transaction toEntity(CreateTransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setBuyerId(dto.getBuyerId());
        transaction.setSellerId(dto.getSellerId());
        transaction.setAmount(dto.getAmount().doubleValue()); // Convert from BigDecimal to double
        transaction.setStatus(dto.getStatus());
        transaction.setPaymentMethod(dto.getPaymentMethod());
        transaction.setCreatedAt(java.time.LocalDateTime.now());
        transaction.setUpdatedAt(java.time.LocalDateTime.now());

        if (dto.getItemId() != null) {
            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + dto.getItemId()));
            transaction.setItem(item);
        }

        return transaction;
    }

    public void updateEntityFromDto(Transaction transaction, UpdateTransactionDTO dto) {
        if (dto.getStatus() != null) {
            transaction.setStatus(dto.getStatus());
        }
        if (dto.getPaymentMethod() != null) {
            transaction.setPaymentMethod(dto.getPaymentMethod());
        }
        if (dto.getAmount() != null) {
            transaction.setAmount(dto.getAmount().doubleValue()); // Convert from BigDecimal to double
        }
        transaction.setUpdatedAt(java.time.LocalDateTime.now());
    }
}