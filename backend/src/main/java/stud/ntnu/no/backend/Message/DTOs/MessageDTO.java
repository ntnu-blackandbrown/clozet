package stud.ntnu.no.backend.Message.DTOs;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageDTO {
    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;
    private Boolean isRead;

    public MessageDTO() {
    }

    public MessageDTO(Long id, String sender, String receiver, String content, 
                     LocalDateTime timestamp, Boolean isRead) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageDTO that = (MessageDTO) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(sender, that.sender) &&
               Objects.equals(receiver, that.receiver) &&
               Objects.equals(content, that.content) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(isRead, that.isRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, receiver, content, timestamp, isRead);
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}