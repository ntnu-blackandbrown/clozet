package stud.ntnu.no.backend.Transaction.DTOs;

import java.math.BigDecimal;
import java.util.Objects;

public class CreateTransactionRequest {
    private String description;
    private BigDecimal amount;
    private String status;

    public CreateTransactionRequest() {
    }

    public CreateTransactionRequest(String description, BigDecimal amount, String status) {
        this.description = description;
        this.amount = amount;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateTransactionRequest that = (CreateTransactionRequest) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, amount, status);
    }

    @Override
    public String toString() {
        return "CreateTransactionRequest{" +
                "description='" + description + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}