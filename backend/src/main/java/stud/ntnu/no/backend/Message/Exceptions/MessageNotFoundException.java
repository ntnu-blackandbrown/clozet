package stud.ntnu.no.backend.Message.Exceptions;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(Long id) {
        super("Message not found with id: " + id);
    }
}