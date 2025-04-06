package stud.ntnu.no.backend.common.service;

/**
 * Service interface for sending emails.
 * <p>
 * Defines methods for sending various types of emails, such as verification
 * and password reset emails.
 * </p>
 */
public interface EmailService {

    /**
     * Sends a verification email to the specified email address.
     *
     * @param toEmail the recipient's email address
     * @param token   the verification token
     */
    void sendVerificationEmail(String toEmail, String token);

    /**
     * Sends a password reset email to the specified email address.
     *
     * @param toEmail the recipient's email address
     * @param token   the password reset token
     */
    void sendPasswordResetEmail(String toEmail, String token);

    /**
     * Sends an HTML email to the specified email address.
     *
     * @param toEmail     the recipient's email address
     * @param subject     the subject of the email
     * @param htmlContent the HTML content of the email
     */
    void sendHtmlEmail(String toEmail, String subject, String htmlContent);

    /**
     * Sends a password change confirmation email to the specified email address.
     *
     * @param email the recipient's email address
     */
    void sendPasswordChangeConfirmationEmail(String email);

    /**
     * Sends a password reset confirmation email to the specified email address.
     *
     * @param email the recipient's email address
     */
    void sendPasswordResetConfirmationEmail(String email);
}
