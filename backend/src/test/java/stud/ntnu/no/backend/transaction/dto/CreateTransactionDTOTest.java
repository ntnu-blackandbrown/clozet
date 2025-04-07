package stud.ntnu.no.backend.transaction.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateTransactionDTOTest {

    @Test
    void testEmptyConstructor() {
        CreateTransactionDTO dto = new CreateTransactionDTO();
        
        assertNull(dto.getItemId());
        assertNull(dto.getBuyerId());
        assertNull(dto.getSellerId());
        assertEquals(0.0, dto.getAmount(), 0.001);
        assertNull(dto.getStatus());
        assertNull(dto.getPaymentMethod());
    }
    
    @Test
    void testParameterizedConstructor() {
        Long itemId = 1L;
        String buyerId = "buyer123";
        String sellerId = "seller456";
        double amount = 149.99;
        String status = "PENDING";
        String paymentMethod = "CARD";
        
        CreateTransactionDTO dto = new CreateTransactionDTO(
            itemId, buyerId, sellerId, amount, status, paymentMethod
        );
        
        assertEquals(itemId, dto.getItemId());
        assertEquals(buyerId, dto.getBuyerId());
        assertEquals(sellerId, dto.getSellerId());
        assertEquals(amount, dto.getAmount(), 0.001);
        assertEquals(status, dto.getStatus());
        assertEquals(paymentMethod, dto.getPaymentMethod());
    }
    
    @Test
    void testGettersAndSetters() {
        CreateTransactionDTO dto = new CreateTransactionDTO();
        
        Long itemId = 1L;
        String buyerId = "buyer123";
        String sellerId = "seller456";
        double amount = 149.99;
        String status = "PENDING";
        String paymentMethod = "CARD";
        
        dto.setItemId(itemId);
        dto.setBuyerId(buyerId);
        dto.setSellerId(sellerId);
        dto.setAmount(amount);
        dto.setStatus(status);
        dto.setPaymentMethod(paymentMethod);
        
        assertEquals(itemId, dto.getItemId());
        assertEquals(buyerId, dto.getBuyerId());
        assertEquals(sellerId, dto.getSellerId());
        assertEquals(amount, dto.getAmount(), 0.001);
        assertEquals(status, dto.getStatus());
        assertEquals(paymentMethod, dto.getPaymentMethod());
    }
} 