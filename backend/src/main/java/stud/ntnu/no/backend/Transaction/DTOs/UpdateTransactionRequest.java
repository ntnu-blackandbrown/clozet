package stud.ntnu.no.backend.Transaction.DTOs;

import java.math.BigDecimal;

public class UpdateTransactionRequest {
    private BigDecimal amount;
    private String status;
    private String paymentMethod;

    // Default constructor
    public UpdateTransactionRequest() {
    }

    // Getters and setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}