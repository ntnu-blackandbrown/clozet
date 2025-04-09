package stud.ntnu.no.backend.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.common.config.EmailConfig;
import stud.ntnu.no.backend.common.util.EmailTemplateUtil;

/**
 * Implementation of the {@link EmailService} interface.
 *
 * <p>Provides methods to send various types of emails, including verification, password reset, and
 * confirmation emails.
 */
@Service
public class EmailServiceImpl implements EmailService {

  private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

  private final JavaMailSender mailSender;
  private final EmailConfig emailConfig;

  @Autowired
  public EmailServiceImpl(JavaMailSender mailSender, EmailConfig emailConfig) {
    this.mailSender = mailSender;
    this.emailConfig = emailConfig;
  }

  /**
   * Sends a verification email to the specified email address.
   *
   * @param toEmail the recipient's email address
   * @param token the verification token
   */
  @Override
  public void sendVerificationEmail(String toEmail, String token) {
    String verificationLink = emailConfig.getBaseUrl() + "verify?token=" + token;
    String subject = "Bekreft din konto på Clozet";

    try {
      String template = EmailTemplateUtil.loadTemplate("verification");
      Map<String, String> variables = EmailTemplateUtil.createCommonVariables();
      variables.put("verificationLink", verificationLink);
      variables.put("expiryHours", String.valueOf(emailConfig.getVerificationExpiryHours()));

      String htmlContent = EmailTemplateUtil.processTemplate(template, variables);
      sendHtmlEmail(toEmail, subject, htmlContent);
      logger.info("Verification email sent to: {}", toEmail);
    } catch (IOException e) {
      logger.error("Failed to load verification email template: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to send verification email", e);
    }
  }

  /**
   * Sends a password reset email to the specified email address.
   *
   * @param toEmail the recipient's email address
   * @param token the password reset token
   */
  @Override
  public void sendPasswordResetEmail(String toEmail, String token) {
    String resetLink = emailConfig.getBaseUrl() + "reset-password?token=" + token;
    String subject = "Tilbakestill ditt passord på Clozet";

    try {
      String template = EmailTemplateUtil.loadTemplate("password-reset");
      Map<String, String> variables = EmailTemplateUtil.createCommonVariables();
      variables.put("resetLink", resetLink);
      variables.put("expiryHours", String.valueOf(emailConfig.getPasswordResetExpiryHours()));

      String htmlContent = EmailTemplateUtil.processTemplate(template, variables);
      sendHtmlEmail(toEmail, subject, htmlContent);
      logger.info("Password reset email sent to: {}", toEmail);
    } catch (IOException e) {
      logger.error("Failed to load password reset email template: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to send password reset email", e);
    }
  }

  /**
   * Sends a password change confirmation email to the specified email address.
   *
   * @param email the recipient's email address
   */
  @Override
  public void sendPasswordChangeConfirmationEmail(String email) {
    String resetLink = emailConfig.getBaseUrl() + "forgot-password";
    String subject = "Ditt passord er endret på Clozet";

    try {
      String template = EmailTemplateUtil.loadTemplate("password-change-confirmation");
      Map<String, String> variables = EmailTemplateUtil.createCommonVariables();
      variables.put("resetLink", resetLink);

      String htmlContent = EmailTemplateUtil.processTemplate(template, variables);
      sendHtmlEmail(email, subject, htmlContent);
      logger.info("Password change confirmation email sent to: {}", email);
    } catch (IOException e) {
      logger.error(
          "Failed to load password change confirmation email template: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to send password change confirmation email", e);
    }
  }

  /**
   * Sends a password reset confirmation email to the specified email address.
   *
   * @param email the recipient's email address
   */
  @Override
  public void sendPasswordResetConfirmationEmail(String email) {
    String loginLink = emailConfig.getBaseUrl() + "login";
    String subject = "Passord tilbakestilt på Clozet";

    try {
      String template = EmailTemplateUtil.loadTemplate("password-reset-confirmation");
      Map<String, String> variables = EmailTemplateUtil.createCommonVariables();
      variables.put("loginLink", loginLink);

      String htmlContent = EmailTemplateUtil.processTemplate(template, variables);
      sendHtmlEmail(email, subject, htmlContent);
      logger.info("Password reset confirmation email sent to: {}", email);
    } catch (IOException e) {
      logger.error(
          "Failed to load password reset confirmation email template: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to send password reset confirmation email", e);
    }
  }

  /**
   * Sends an HTML email to the specified email address.
   *
   * @param toEmail the recipient's email address
   * @param subject the subject of the email
   * @param htmlContent the HTML content of the email
   */
  @Override
  public void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom(emailConfig.getEmailFrom());
      helper.setTo(toEmail);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      // Add the logo as an inline attachment
      ClassPathResource logoResource = new ClassPathResource("static/images/light-green.png");
      helper.addInline("logo", logoResource);
      logger.info("Added logo as inline attachment");

      mailSender.send(mimeMessage);
      logger.info("HTML email sent to: {}", toEmail);
    } catch (MessagingException e) {
      logger.error("Failed to send HTML email to {}: {}", toEmail, e.getMessage(), e);
      throw new RuntimeException("Failed to send HTML email", e);
    }
  }
}
