package stud.ntnu.no.backend.message.entity;

import jakarta.persistence.*;
import stud.ntnu.no.backend.item.entity.Item;
import java.time.LocalDateTime;

/**
 * Entity representing a message.
 * <p>
 * This class is mapped to the "messages" table in the database and holds
 * information about a message, including its ID, sender, receiver, content,
 * creation time, read status, and associated item.
 */
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String senderId;
    private String receiverId;
    
    // Add this field to match with mappedBy="item" in Item class
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;
    private boolean archivedBySender = false;
    private boolean archivedByReceiver = false;
    
    // Constructors
    public Message() {
    }
    
    // Getters and setters
    /**
     * Returns the ID of the message.
     *
     * @return the message ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the ID of the message.
     *
     * @param id the message ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Returns the ID of the sender.
     *
     * @return the sender ID
     */
    public String getSenderId() {
        return senderId;
    }
    
    /**
     * Sets the ID of the sender.
     *
     * @param senderId the sender ID to set
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    /**
     * Returns the ID of the receiver.
     *
     * @return the receiver ID
     */
    public String getReceiverId() {
        return receiverId;
    }
    
    /**
     * Sets the ID of the receiver.
     *
     * @param receiverId the receiver ID to set
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    
    /**
     * Returns the associated item.
     *
     * @return the item
     */
    public Item getItem() {
        return item;
    }
    
    /**
     * Sets the associated item.
     *
     * @param item the item to set
     */
    public void setItem(Item item) {
        this.item = item;
    }
    
    /**
     * Returns the content of the message.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Sets the content of the message.
     *
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Returns the creation time of the message.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation time of the message.
     *
     * @param createdAt the creation time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Returns the read status of the message.
     *
     * @return true if read, false otherwise
     */
    public boolean isRead() {
        return isRead;
    }
    
    /**
     * Sets the read status of the message.
     *
     * @param read true if read, false otherwise
     */
    public void setRead(boolean read) {
        isRead = read;
    }
    
    /**
     * Returns whether the message is archived by the sender.
     *
     * @return true if archived by sender, false otherwise
     */
    public boolean isArchivedBySender() {
        return archivedBySender;
    }
    
    /**
     * Sets whether the message is archived by the sender.
     *
     * @param archivedBySender true if archived by sender, false otherwise
     */
    public void setArchivedBySender(boolean archivedBySender) {
        this.archivedBySender = archivedBySender;
    }
    
    /**
     * Returns whether the message is archived by the receiver.
     *
     * @return true if archived by receiver, false otherwise
     */
    public boolean isArchivedByReceiver() {
        return archivedByReceiver;
    }
    
    /**
     * Sets whether the message is archived by the receiver.
     *
     * @param archivedByReceiver true if archived by receiver, false otherwise
     */
    public void setArchivedByReceiver(boolean archivedByReceiver) {
        this.archivedByReceiver = archivedByReceiver;
    }
}