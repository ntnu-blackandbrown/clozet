package stud.ntnu.no.backend.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.common.config.EmailConfig;

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

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        String verificationLink = emailConfig.getBaseUrl() + "/verify?token=" + token;
        String subject = "Bekreft din konto på Clozet";
        
        String htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #333; padding: 10px; text-align: center; }
                    .header h1 { color: white; margin: 0; }
                    .content { padding: 20px; border: 1px solid #ddd; border-top: none; }
                    .button { display: inline-block; background-color: #333; color: white; text-decoration: none; 
                              padding: 10px 20px; border-radius: 5px; margin-top: 20px; }
                    .footer { margin-top: 20px; font-size: 12px; color: #777; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Clozet</h1>
                    </div>
                    <div class="content">
                        <h2>Bekreft din konto</h2>
                        <p>Hei!</p>
                        <p>Takk for at du registrerte deg på Clozet. For å aktivere kontoen din, vennligst klikk på knappen nedenfor:</p>
                        <a href="%s" class="button">Bekreft min konto</a>
                        <p>Hvis du ikke kan klikke på knappen, kan du kopiere og lime inn følgende URL i nettleseren din:</p>
                        <p><a href="%s">%s</a></p>
                        <p>Denne lenken er gyldig i %d timer.</p>
                        <p>Hvis du ikke har registrert deg på Clozet, kan du trygt ignorere denne e-posten.</p>
                    </div>
                    <div class="footer">
                        <p>© %d Clozet. Alle rettigheter reservert.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                verificationLink, 
                verificationLink, 
                verificationLink, 
                emailConfig.getVerificationExpiryHours(),
                java.time.Year.now().getValue()
            );
            
        sendHtmlEmail(toEmail, subject, htmlContent);
        logger.info("Verification email sent to: {}", toEmail);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = emailConfig.getBaseUrl() + "/reset-password?token=" + token;
        String subject = "Tilbakestill ditt passord på Clozet";
        
        String htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #333; padding: 10px; text-align: center; }
                    .header h1 { color: white; margin: 0; }
                    .content { padding: 20px; border: 1px solid #ddd; border-top: none; }
                    .button { display: inline-block; background-color: #333; color: white; text-decoration: none; 
                              padding: 10px 20px; border-radius: 5px; margin-top: 20px; }
                    .footer { margin-top: 20px; font-size: 12px; color: #777; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Clozet</h1>
                    </div>
                    <div class="content">
                        <h2>Tilbakestill ditt passord</h2>
                        <p>Hei!</p>
                        <p>Vi har mottatt en forespørsel om å tilbakestille passordet for din Clozet-konto. Klikk på knappen nedenfor for å sette et nytt passord:</p>
                        <a href="%s" class="button">Tilbakestill passord</a>
                        <p>Hvis du ikke kan klikke på knappen, kan du kopiere og lime inn følgende URL i nettleseren din:</p>
                        <p><a href="%s">%s</a></p>
                        <p>Denne lenken er gyldig i %d time(r).</p>
                        <p>Hvis du ikke har bedt om å tilbakestille passordet ditt, kan du trygt ignorere denne e-posten.</p>
                    </div>
                    <div class="footer">
                        <p>© %d Clozet. Alle rettigheter reservert.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                resetLink, 
                resetLink, 
                resetLink, 
                emailConfig.getPasswordResetExpiryHours(),
                java.time.Year.now().getValue()
            );
            
        sendHtmlEmail(toEmail, subject, htmlContent);
        logger.info("Password reset email sent to: {}", toEmail);
    }

    @Override
    public void sendMessageNotification(String toEmail, String senderName) {
        String loginLink = emailConfig.getBaseUrl() + "/messages";
        String subject = "Ny melding på Clozet";
        
        String htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #333; padding: 10px; text-align: center; }
                    .header h1 { color: white; margin: 0; }
                    .content { padding: 20px; border: 1px solid #ddd; border-top: none; }
                    .button { display: inline-block; background-color: #333; color: white; text-decoration: none; 
                              padding: 10px 20px; border-radius: 5px; margin-top: 20px; }
                    .footer { margin-top: 20px; font-size: 12px; color: #777; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Clozet</h1>
                    </div>
                    <div class="content">
                        <h2>Du har fått en ny melding</h2>
                        <p>Hei!</p>
                        <p>Du har mottatt en ny melding fra %s på Clozet.</p>
                        <p>Logg inn for å lese og svare på meldingen:</p>
                        <a href="%s" class="button">Se meldingen</a>
                    </div>
                    <div class="footer">
                        <p>© %d Clozet. Alle rettigheter reservert.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                senderName, 
                loginLink,
                java.time.Year.now().getValue()
            );
            
        sendHtmlEmail(toEmail, subject, htmlContent);
        logger.info("Message notification email sent to: {}", toEmail);
    }


    @Override
    public void sendPasswordChangeConfirmationEmail(String email) {
        String resetLink = emailConfig.getBaseUrl() + "/forgot-password";
        String subject = "Ditt passord er endret på Clozet";

        String htmlContent = """
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #333; padding: 10px; text-align: center; }
                .header h1 { color: white; margin: 0; }
                .content { padding: 20px; border: 1px solid #ddd; border-top: none; }
                .button { display: inline-block; background-color: #333; color: white; text-decoration: none;
                          padding: 10px 20px; border-radius: 5px; margin-top: 20px; }
                .footer { margin-top: 20px; font-size: 12px; color: #777; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Clozet</h1>
                </div>
                <div class="content">
                    <h2>Passord endret</h2>
                    <p>Hei!</p>
                    <p>Passordet ditt ble nylig endret.</p>
                    <p>Hvis dette var deg, kan du ignorere denne e-posten.</p>
                    <p>Hvis dette <strong>ikke</strong> var deg, kan du klikke på knappen nedenfor for å tilbakestille passordet ditt umiddelbart:</p>
                    <a href="%s" class="button">Tilbakestill passord</a>
                    <p>Om du har spørsmål eller trenger hjelp, kan du svare på denne e-posten.</p>
                </div>
                <div class="footer">
                    <p>© %d Clozet. Alle rettigheter reservert.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
            resetLink,
            java.time.Year.now().getValue()
        );

        sendHtmlEmail(email, subject, htmlContent);
        logger.info("Password change confirmation email sent to: {}", email);
    }

    @Override
    public void sendTextEmail(String toEmail, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailConfig.getEmailFrom());
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            
            mailSender.send(mailMessage);
            logger.info("Plain text email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendPasswordResetConfirmationEmail(String email) {
        String loginLink = emailConfig.getBaseUrl() + "/login";
        String subject = "Passord tilbakestilt på Clozet";

        String htmlContent = """
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #333; padding: 10px; text-align: center; }
                .header h1 { color: white; margin: 0; }
                .content { padding: 20px; border: 1px solid #ddd; border-top: none; }
                .button { display: inline-block; background-color: #333; color: white; text-decoration: none; 
                          padding: 10px 20px; border-radius: 5px; margin-top: 20px; }
                .footer { margin-top: 20px; font-size: 12px; color: #777; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Clozet</h1>
                </div>
                <div class="content">
                    <h2>Passord tilbakestilt</h2>
                    <p>Hei!</p>
                    <p>Passordet ditt er nå tilbakestilt.</p>
                    <p>Hvis dette var deg, kan du logge inn med ditt nye passord:</p>
                    <a href="%s" class="button">Logg inn</a>
                    <p>Hvis dette <strong>ikke</strong> var deg, vennligst ta kontakt med oss umiddelbart ved å svare på denne e-posten.</p>
                </div>
                <div class="footer">
                    <p>© %d Clozet. Alle rettigheter reservert.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
            loginLink,
            java.time.Year.now().getValue()
        );

        sendHtmlEmail(email, subject, htmlContent);
        logger.info("Password reset confirmation email sent to: {}", email);
    }

    @Override
    public void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(emailConfig.getEmailFrom());
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            logger.info("HTML email sent to: {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
}
