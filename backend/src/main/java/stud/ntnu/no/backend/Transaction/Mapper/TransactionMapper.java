package stud.ntnu.no.backend.Transaction.Mapper;

import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.Item.Entity.Item;
import stud.ntnu.no.backend.Item.Repository.ItemRepository;
import stud.ntnu.no.backend.Transaction.DTOs.CreateTransactionDTO;
import stud.ntnu.no.backend.Transaction.DTOs.TransactionDTO;
import stud.ntnu.no.backend.Transaction.Entity.Transaction;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    private final ItemRepository itemRepository;

    public TransactionMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setBuyerId(transaction.getBuyerId());
        dto.setSellerId(transaction.getSellerId());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());

        if (transaction.getItem() != null) {
            dto.setItemId(transaction.getItem().getId());
        }

        return dto;
    }

    public Transaction toEntity(CreateTransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setBuyerId(dto.getBuyerId());
        transaction.setSellerId(dto.getSellerId());
        transaction.setAmount(dto.getAmount()); // Now directly assigning double value
        transaction.setStatus(dto.getStatus());
        transaction.setPaymentMethod(dto.getPaymentMethod());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        // Handle item if itemId is provided
        if (dto.getItemId() != null) {
            Item item = itemRepository.findById(dto.getItemId())
                    .orElse(null);
            transaction.setItem(item);
        }

        return transaction;
    }
}