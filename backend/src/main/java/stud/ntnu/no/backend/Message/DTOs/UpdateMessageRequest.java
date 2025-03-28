package stud.ntnu.no.backend.Message.DTOs;

public class UpdateMessageRequest {
    private String content;
    private Boolean isRead;

    // Default constructor
    public UpdateMessageRequest() {
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
}