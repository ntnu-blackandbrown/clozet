package stud.ntnu.no.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttlsEnable;

    @Value("${app.email.from:noreply@clozet.com}")
    private String emailFrom;

    @Value("${app.email.verification-expiry-hours:24}")
    private int verificationExpiryHours;

    @Value("${app.email.password-reset-expiry-hours:1}")
    private int passwordResetExpiryHours;

    @Value("${app.email.base-url}")
    private String baseUrl;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        
        if (username != null && !username.isEmpty()) {
            mailSender.setUsername(username);
        }
        
        if (password != null && !password.isEmpty()) {
            mailSender.setPassword(password);
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        
        return mailSender;
    }

    @Bean
    @Profile("dev")
    public PapercutStarter papercutStarter() {
        return new PapercutStarter();
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public int getVerificationExpiryHours() {
        return verificationExpiryHours;
    }

    public int getPasswordResetExpiryHours() {
        return passwordResetExpiryHours;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
} 