package stud.ntnu.no.backend.transaction.dto;

public class CreateTransactionDTO {
    private Long itemId;
    private String buyerId;
    private String sellerId;
    private double amount; // Changed from BigDecimal to double
    private String status;
    private String paymentMethod;

    // Constructors
    public CreateTransactionDTO() {
    }

    public CreateTransactionDTO(Long itemId, String buyerId, String sellerId, double amount, 
                              String status, String paymentMethod) {
        this.itemId = itemId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    // Getters and setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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