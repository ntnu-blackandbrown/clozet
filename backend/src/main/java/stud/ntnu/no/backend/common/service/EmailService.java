package stud.ntnu.no.backend.common.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String token);
    void sendPasswordResetEmail(String toEmail, String token);
    void sendHtmlEmail(String toEmail, String subject, String htmlContent);
    void sendPasswordChangeConfirmationEmail(String email);
    void sendPasswordResetConfirmationEmail(String email);
}
