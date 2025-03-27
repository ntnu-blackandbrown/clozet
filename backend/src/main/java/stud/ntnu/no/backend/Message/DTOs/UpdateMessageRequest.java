package stud.ntnu.no.backend.Message.DTOs;

import java.util.Objects;

public class UpdateMessageRequest {
    private String content;
    private Boolean isRead;

    public UpdateMessageRequest() {
    }

    public UpdateMessageRequest(String content, Boolean isRead) {
        this.content = content;
        this.isRead = isRead;
    }

    // Getters and setters
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
        UpdateMessageRequest that = (UpdateMessageRequest) o;
        return Objects.equals(content, that.content) &&
               Objects.equals(isRead, that.isRead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, isRead);
    }

    @Override
    public String toString() {
        return "UpdateMessageRequest{" +
                "content='" + content + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}