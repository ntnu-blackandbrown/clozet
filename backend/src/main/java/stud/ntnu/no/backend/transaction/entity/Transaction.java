package stud.ntnu.no.backend.transaction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import stud.ntnu.no.backend.item.entity.Item;

/**
 * Entity representing a transaction.
 * <p>
 * This class is mapped to the "transactions" table in the database and holds information about a
 * transaction, including its ID, item, buyer ID, seller ID, amount, status, payment method, and
 * timestamps.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  private String buyerId;
  private String sellerId;
  private double amount;
  private String status;
  private String paymentMethod;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Constructors
  public Transaction() {
  }

  public Transaction(String buyerId, String sellerId, double amount, String status,
      String paymentMethod) {
    this.buyerId = buyerId;
    this.sellerId = sellerId;
    this.amount = amount;
    this.status = status;
    this.paymentMethod = paymentMethod;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // Getters and setters

  /**
   * Returns the ID of the transaction.
   *
   * @return the ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the transaction.
   *
   * @param id the ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the item associated with the transaction.
   *
   * @return the item
   */
  public Item getItem() {
    return item;
  }

  /**
   * Sets the item associated with the transaction.
   *
   * @param item the item to set
   */
  public void setItem(Item item) {
    this.item = item;
  }

  /**
   * Returns the buyer ID of the transaction.
   *
   * @return the buyer ID
   */
  public String getBuyerId() {
    return buyerId;
  }

  /**
   * Sets the buyer ID of the transaction.
   *
   * @param buyerId the buyer ID to set
   */
  public void setBuyerId(String buyerId) {
    this.buyerId = buyerId;
  }

  /**
   * Returns the seller ID of the transaction.
   *
   * @return the seller ID
   */
  public String getSellerId() {
    return sellerId;
  }

  /**
   * Sets the seller ID of the transaction.
   *
   * @param sellerId the seller ID to set
   */
  public void setSellerId(String sellerId) {
    this.sellerId = sellerId;
  }

  /**
   * Returns the amount of the transaction.
   *
   * @return the amount
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Sets the amount of the transaction.
   *
   * @param amount the amount to set
   */
  public void setAmount(double amount) {
    this.amount = amount;
  }

  /**
   * Returns the status of the transaction.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status of the transaction.
   *
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Returns the payment method of the transaction.
   *
   * @return the payment method
   */
  public String getPaymentMethod() {
    return paymentMethod;
  }

  /**
   * Sets the payment method of the transaction.
   *
   * @param paymentMethod the payment method to set
   */
  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  /**
   * Returns the creation timestamp of the transaction.
   *
   * @return the creation timestamp
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the creation timestamp of the transaction.
   *
   * @param createdAt the creation timestamp to set
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Returns the update timestamp of the transaction.
   *
   * @return the update timestamp
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Sets the update timestamp of the transaction.
   *
   * @param updatedAt the update timestamp to set
   */
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}