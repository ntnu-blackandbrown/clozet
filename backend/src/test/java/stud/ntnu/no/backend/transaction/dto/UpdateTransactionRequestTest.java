package stud.ntnu.no.backend.transaction.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UpdateTransactionRequestTest {

  @Test
  void testEmptyConstructor() {
    UpdateTransactionRequest request = new UpdateTransactionRequest();

    assertNull(request.getStatus());
    assertNull(request.getPaymentMethod());
  }

  @Test
  void testParameterizedConstructor() {
    String status = "COMPLETED";
    String paymentMethod = "VIPPS";

    UpdateTransactionRequest request = new UpdateTransactionRequest(status, paymentMethod);

    assertEquals(status, request.getStatus());
    assertEquals(paymentMethod, request.getPaymentMethod());
  }

  @Test
  void testGettersAndSetters() {
    UpdateTransactionRequest request = new UpdateTransactionRequest();

    String status = "PROCESSING";
    String paymentMethod = "CARD";

    request.setStatus(status);
    request.setPaymentMethod(paymentMethod);

    assertEquals(status, request.getStatus());
    assertEquals(paymentMethod, request.getPaymentMethod());
  }
}
