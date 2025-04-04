package stud.ntnu.no.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.common.service.EmailServiceImpl;

@Configuration
public class EmailServiceConfig {

    @Bean
    @Primary
    public EmailService emailService(JavaMailSender javaMailSender, EmailConfig emailConfig) {
        return new EmailServiceImpl(javaMailSender, emailConfig);
    }
} 