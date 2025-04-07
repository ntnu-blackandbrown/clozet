package stud.ntnu.no.backend.transaction.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionStatusTest {

    @Test
    void testEnumValues() {
        // Test that the enum contains the expected values
        assertEquals(4, TransactionStatus.values().length);
        assertEquals(TransactionStatus.PENDING, TransactionStatus.valueOf("PENDING"));
        assertEquals(TransactionStatus.COMPLETED, TransactionStatus.valueOf("COMPLETED"));
        assertEquals(TransactionStatus.FAILED, TransactionStatus.valueOf("FAILED"));
        assertEquals(TransactionStatus.CANCELLED, TransactionStatus.valueOf("CANCELLED"));
    }
    
    @Test
    void testEnumOrder() {
        // Test the order of enum values
        TransactionStatus[] expectedOrder = {
            TransactionStatus.PENDING,
            TransactionStatus.COMPLETED,
            TransactionStatus.FAILED,
            TransactionStatus.CANCELLED
        };
        
        assertArrayEquals(expectedOrder, TransactionStatus.values());
    }
    
    @Test
    void testOrdinalValues() {
        // Test the ordinal values of the enums
        assertEquals(0, TransactionStatus.PENDING.ordinal());
        assertEquals(1, TransactionStatus.COMPLETED.ordinal());
        assertEquals(2, TransactionStatus.FAILED.ordinal());
        assertEquals(3, TransactionStatus.CANCELLED.ordinal());
    }
} 