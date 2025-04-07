package stud.ntnu.no.backend.transaction.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDTOTest {

    @Test
    void testGettersAndSetters() {
        TransactionDTO dto = new TransactionDTO();
        
        Long id = 1L;
        Long itemId = 2L;
        String buyerId = "buyer123";
        String sellerId = "seller456";
        double amount = 299.99;
        String status = "COMPLETED";
        String paymentMethod = "VIPPS";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        dto.setId(id);
        dto.setItemId(itemId);
        dto.setBuyerId(buyerId);
        dto.setSellerId(sellerId);
        dto.setAmount(amount);
        dto.setStatus(status);
        dto.setPaymentMethod(paymentMethod);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        
        assertEquals(id, dto.getId());
        assertEquals(itemId, dto.getItemId());
        assertEquals(buyerId, dto.getBuyerId());
        assertEquals(sellerId, dto.getSellerId());
        assertEquals(amount, dto.getAmount(), 0.001);
        assertEquals(status, dto.getStatus());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
    }
    
    @Test
    void testDefaultValues() {
        TransactionDTO dto = new TransactionDTO();
        
        assertNull(dto.getId());
        assertNull(dto.getItemId());
        assertNull(dto.getBuyerId());
        assertNull(dto.getSellerId());
        assertEquals(0.0, dto.getAmount(), 0.001);
        assertNull(dto.getStatus());
        assertNull(dto.getPaymentMethod());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }
} 