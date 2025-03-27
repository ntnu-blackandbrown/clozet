package stud.ntnu.no.backend.Message.DTOs;

import java.util.Objects;

public class CreateMessageRequest {
    private String sender;
    private String receiver;
    private String content;
    private Boolean isRead;

    public CreateMessageRequest() {
    }

    public CreateMessageRequest(String sender, String receiver, String content, Boolean isRead) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.isRead = isRead;
    }

    // Getters and setters
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
        CreateMessageRequest that = (CreateMessageRequest) o;
        return Objects.equals(sender, that.sender) &&
               Objects.equals(receiver, that.receiver) &&
               Objects.equals(content, that.content) &&
               Objects.equals(isRead, that.isRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, content, isRead);
    }

    @Override
    public String toString() {
        return "CreateMessageRequest{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}