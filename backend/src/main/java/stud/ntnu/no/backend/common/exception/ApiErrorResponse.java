package stud.ntnu.no.backend.common.exception;

import java.time.LocalDateTime;

/**
 * Represents an API error response.
 * <p>
 * Contains details about the error, including a message, timestamp, and status code.
 * </p>
 */
public class ApiErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private int status;

    public ApiErrorResponse() {
    }

    /**
     * Constructs an {@code ApiErrorResponse} with the specified details.
     *
     * @param message   the error message
     * @param timestamp the time the error occurred
     * @param status    the HTTP status code
     */
    public ApiErrorResponse(String message, LocalDateTime timestamp, int status) {
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
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
    public int getStatus() {
        return status;
    }

    /**
     * Sets the HTTP status code.
     *
     * @param status the status code
     */
    public void setStatus(int status) {
        this.status = status;
    }
}