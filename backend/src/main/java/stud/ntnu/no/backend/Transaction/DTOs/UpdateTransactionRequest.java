package stud.ntnu.no.backend.Transaction.DTOs;

public class UpdateTransactionRequest {
    private String status;
    private String paymentMethod;

    // Constructors
    public UpdateTransactionRequest() {
    }

    public UpdateTransactionRequest(String status, String paymentMethod) {
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
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