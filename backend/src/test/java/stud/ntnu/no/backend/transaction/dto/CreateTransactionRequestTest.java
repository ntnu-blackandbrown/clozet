package stud.ntnu.no.backend.transaction.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CreateTransactionRequestTest {

  @Test
  void testEmptyConstructor() {
    CreateTransactionRequest request = new CreateTransactionRequest();

    assertNull(request.getItemId());
    assertNull(request.getBuyerId());
    assertNull(request.getSellerId());
    assertEquals(0.0, request.getAmount(), 0.001);
    assertNull(request.getStatus());
    assertNull(request.getPaymentMethod());
  }

  @Test
  void testParameterizedConstructor() {
    Long itemId = 1L;
    String buyerId = "buyer123";
    String sellerId = "seller456";
    double amount = 149.99;
    String status = "PENDING";
    String paymentMethod = "CARD";

    CreateTransactionRequest request =
        new CreateTransactionRequest(itemId, buyerId, sellerId, amount, status, paymentMethod);

    assertEquals(itemId, request.getItemId());
    assertEquals(buyerId, request.getBuyerId());
    assertEquals(sellerId, request.getSellerId());
    assertEquals(amount, request.getAmount(), 0.001);
    assertEquals(status, request.getStatus());
    assertEquals(paymentMethod, request.getPaymentMethod());
  }

  @Test
  void testGettersAndSetters() {
    CreateTransactionRequest request = new CreateTransactionRequest();

    Long itemId = 1L;
    String buyerId = "buyer123";
    String sellerId = "seller456";
    double amount = 149.99;
    String status = "PENDING";
    String paymentMethod = "CARD";

    request.setItemId(itemId);
    request.setBuyerId(buyerId);
    request.setSellerId(sellerId);
    request.setAmount(amount);
    request.setStatus(status);
    request.setPaymentMethod(paymentMethod);

    assertEquals(itemId, request.getItemId());
    assertEquals(buyerId, request.getBuyerId());
    assertEquals(sellerId, request.getSellerId());
    assertEquals(amount, request.getAmount(), 0.001);
    assertEquals(status, request.getStatus());
    assertEquals(paymentMethod, request.getPaymentMethod());
  }
}
