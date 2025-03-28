package stud.ntnu.no.backend.message.dto;

import java.time.LocalDateTime;

public class UpdateMessageRequest {
    private String content;
    private LocalDateTime timestamp;

    // Default constructor
    public UpdateMessageRequest() {
    }

    // Constructor with arguments
    public UpdateMessageRequest(String content, LocalDateTime timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and setters
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
}