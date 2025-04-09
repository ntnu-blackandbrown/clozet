package stud.ntnu.no.backend.common.config.email;

import java.util.Properties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Test configuration for email-related tests. This ensures that even when using the 'test' profile,
 * we can have a mock JavaMailSender that doesn't try to connect to a real server.
 */
@TestConfiguration
@Profile("test")
public class EmailTestConfig {

  /**
   * Creates a no-op JavaMailSender for test cases. This prevents tests from trying to connect to
   * real mail servers.
   */
  @Bean
  @Primary
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    // Configure to a dummy server that will never be used
    mailSender.setHost("localhost");
    mailSender.setPort(25);

    // Set up properties to prevent any actual connection attempts
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "false");
    props.put("mail.smtp.starttls.enable", "false");
    props.put("mail.debug", "false");
    props.put("mail.test-connection", "false");

    return mailSender;
  }
} 