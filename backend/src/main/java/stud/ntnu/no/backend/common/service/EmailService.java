package stud.ntnu.no.backend.common.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String token);
    void sendTextEmail(String toEmail, String subject, String message);
}
