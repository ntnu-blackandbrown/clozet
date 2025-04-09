package stud.ntnu.no.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.common.service.EmailServiceImpl;

/**
 * Configuration class for the email service.
 * <p>
 * Configures the {@link EmailService} bean.
 * </p>
 */
@Configuration
public class EmailServiceConfig {

  /**
   * Configures and returns the primary {@code EmailService} bean.
   *
   * @param javaMailSender the JavaMailSender to use
   * @param emailConfig    the email configuration
   * @return the configured EmailService
   */
  @Bean
  @Primary
  public EmailService emailService(JavaMailSender javaMailSender, EmailConfig emailConfig) {
    return new EmailServiceImpl(javaMailSender, emailConfig);
  }
} 