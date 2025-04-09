package stud.ntnu.no.backend.transaction.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class UpdateTransactionDTOTest {

  @Test
  void testEmptyConstructor() {
    UpdateTransactionDTO dto = new UpdateTransactionDTO();

    assertNull(dto.getAmount());
    assertNull(dto.getStatus());
    assertNull(dto.getPaymentMethod());
  }

  @Test
  void testGettersAndSetters() {
    UpdateTransactionDTO dto = new UpdateTransactionDTO();

    BigDecimal amount = new BigDecimal("199.99");
    String status = "SHIPPED";
    String paymentMethod = "BANK_TRANSFER";

    dto.setAmount(amount);
    dto.setStatus(status);
    dto.setPaymentMethod(paymentMethod);

    assertEquals(amount, dto.getAmount());
    assertEquals(status, dto.getStatus());
    assertEquals(paymentMethod, dto.getPaymentMethod());
  }
}
