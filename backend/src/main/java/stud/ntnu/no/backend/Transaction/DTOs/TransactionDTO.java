package stud.ntnu.no.backend.Transaction.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String status;

    public TransactionDTO() {
    }

    public TransactionDTO(Long id, String description, BigDecimal amount, LocalDateTime timestamp, String status) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
        TransactionDTO that = (TransactionDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, amount, timestamp, status);
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}