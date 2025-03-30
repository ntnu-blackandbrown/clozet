package stud.ntnu.no.backend.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        String verificationLink = "http://localhost:8080/api/users/verify?token=" + token;
        String subject = "Bekreft din konto";
        String message = "Hei!\nKlikk på denne lenken for å bekrefte kontoen din:\n" + verificationLink;
        sendTextEmail(toEmail, subject, message);
    }

    @Override
    public void sendTextEmail(String toEmail, String subject, String message) {
        logger.info("Sending email to: {}", toEmail);
        logger.info("Subject: {}", subject);
        logger.info("Message: {}", message);
        System.out.println("Sending email to " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
    }
}
