package stud.ntnu.no.backend.common.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String token);
    void sendPasswordResetEmail(String toEmail, String token);
    void sendTextEmail(String toEmail, String subject, String message);
    void sendHtmlEmail(String toEmail, String subject, String htmlContent);
    void sendMessageNotification(String toEmail, String senderName);
}
