package stud.ntnu.no.backend.transaction.mapper;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.transaction.dto.CreateTransactionRequest;
import stud.ntnu.no.backend.transaction.dto.TransactionDTO;
import stud.ntnu.no.backend.transaction.entity.Transaction;

/**
 * Mapper for converting between Transaction entities and DTOs.
 * <p>
 * This class provides methods to convert Transaction entities to DTOs and vice versa.
 */
@Component
public class TransactionMapper {

  private final ItemRepository itemRepository;

  public TransactionMapper(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  /**
   * Converts a Transaction entity to a TransactionDTO.
   *
   * @param transaction the Transaction entity
   * @return the TransactionDTO
   */
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

  /**
   * Converts a CreateTransactionRequest to a Transaction entity.
   *
   * @param dto the CreateTransactionRequest
   * @return the Transaction entity
   */
  public Transaction toEntity(CreateTransactionRequest dto) {
    Transaction transaction = new Transaction();
    transaction.setBuyerId(dto.getBuyerId());
    transaction.setSellerId(dto.getSellerId());
    transaction.setAmount(dto.getAmount());
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