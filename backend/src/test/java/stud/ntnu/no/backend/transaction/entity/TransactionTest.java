package stud.ntnu.no.backend.transaction.entity;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.item.entity.Item;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testDefaultConstructor() {
        Transaction transaction = new Transaction();
        assertNull(transaction.getId());
        assertNull(transaction.getItem());
        assertNull(transaction.getBuyerId());
        assertNull(transaction.getSellerId());
        assertEquals(0.0, transaction.getAmount());
        assertNull(transaction.getStatus());
        assertNull(transaction.getPaymentMethod());
        assertNull(transaction.getCreatedAt());
        assertNull(transaction.getUpdatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        String buyerId = "buyer123";
        String sellerId = "seller456";
        double amount = 100.0;
        String status = TransactionStatus.PENDING.name();
        String paymentMethod = "Credit Card";

        Transaction transaction = new Transaction(buyerId, sellerId, amount, status, paymentMethod);

        assertEquals(buyerId, transaction.getBuyerId());
        assertEquals(sellerId, transaction.getSellerId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(status, transaction.getStatus());
        assertEquals(paymentMethod, transaction.getPaymentMethod());
        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Transaction transaction = new Transaction();
        
        Long id = 1L;
        Item item = new Item();
        String buyerId = "buyer123";
        String sellerId = "seller456";
        double amount = 100.0;
        String status = TransactionStatus.COMPLETED.name();
        String paymentMethod = "PayPal";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        transaction.setId(id);
        transaction.setItem(item);
        transaction.setBuyerId(buyerId);
        transaction.setSellerId(sellerId);
        transaction.setAmount(amount);
        transaction.setStatus(status);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setCreatedAt(createdAt);
        transaction.setUpdatedAt(updatedAt);

        assertEquals(id, transaction.getId());
        assertEquals(item, transaction.getItem());
        assertEquals(buyerId, transaction.getBuyerId());
        assertEquals(sellerId, transaction.getSellerId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(status, transaction.getStatus());
        assertEquals(paymentMethod, transaction.getPaymentMethod());
        assertEquals(createdAt, transaction.getCreatedAt());
        assertEquals(updatedAt, transaction.getUpdatedAt());
    }
} 