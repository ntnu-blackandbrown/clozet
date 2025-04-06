package stud.ntnu.no.backend.common.exception;

import java.time.LocalDateTime;

/**
 * Represents an error response.
 * <p>
 * Contains details about the error, including a message, timestamp, and status code.
 * </p>
 */
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private int statusCode;

    /**
     * Constructs an {@code ErrorResponse} with the specified details.
     *
     * @param message   the error message
     * @param timestamp the time the error occurred
     * @param statusCode the HTTP status code
     */
    public ErrorResponse(String message, LocalDateTime timestamp, int statusCode) {
        this.message = message;
        this.timestamp = timestamp;
        this.statusCode = statusCode;
    }

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the error message.
     *
     * @param message the error message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the timestamp of the error.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the error.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the HTTP status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the HTTP status code.
     *
     * @param statusCode the status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}