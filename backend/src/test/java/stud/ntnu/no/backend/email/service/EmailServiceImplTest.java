package stud.ntnu.no.backend.email.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import stud.ntnu.no.backend.common.config.EmailConfig;
import stud.ntnu.no.backend.common.service.EmailServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailConfig emailConfig;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Sett opp default verdier for EmailConfig
        when(emailConfig.getBaseUrl()).thenReturn("http://localhost:5173");
        when(emailConfig.getVerificationExpiryHours()).thenReturn(24);
    }

    @Test
    void testSendVerificationEmail() throws Exception {
        // Arrange
        String token = "dummy-token";
        String toEmail = "test@example.com";

        MimeMessage mimeMessage = new MimeMessage((jakarta.mail.Session) null);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendVerificationEmail(toEmail, token);

        // Assert
        // Verifiser at JavaMailSender.send() ble kalt med en MimeMessage
        verify(mailSender, times(1)).send(any(MimeMessage.class));

        // Hent ut innholdet fra MimeMessage og sjekk at den inneholder riktig lenke
        Object content = mimeMessage.getContent();
        assertNotNull(content);
        String contentStr = content.toString();
        String expectedLink = "http://localhost:5173/verify?token=" + token;
        assertTrue(contentStr.contains(expectedLink), "E-postinnholdet bør inneholde verifikasjonslenken.");
    }
}
