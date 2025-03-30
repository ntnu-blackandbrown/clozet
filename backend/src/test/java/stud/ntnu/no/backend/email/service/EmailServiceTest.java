package stud.ntnu.no.backend.email.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import stud.ntnu.no.backend.common.service.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> simpleMailCaptor;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @Test
    void testSendTextEmail() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Hello, this is a test!";

        // Act
        emailService.sendTextEmail(to, subject, text);

        // Assert
        // Verifiser at mailSender.send(SimpleMailMessage) ble kalt nøyaktig 1 gang
        verify(mailSender, times(1)).send(simpleMailCaptor.capture());

        SimpleMailMessage sentMessage = simpleMailCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
    }

    @Test
    void testSendHtmlEmail() throws Exception {
        // Arrange
        String to = "html@example.com";
        String subject = "HTML Test";
        String htmlBody = "<h1>Hello!</h1><p>This is a <b>test</b> email</p>";

        // VIKTIG: si hva 'createMimeMessage()' skal returnere
        MimeMessage mockMimeMessage = new MimeMessage((jakarta.mail.Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        // Act
        emailService.sendHtmlEmail(to, subject, htmlBody);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage capturedMime = mimeMessageCaptor.getValue();
        assertNotNull(capturedMime);
        // Du kan gjøre mer inngående sjekk, parse mime osv.
    }

}
