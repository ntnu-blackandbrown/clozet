package stud.ntnu.no.backend.email.service;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.common.config.EmailConfig;
import stud.ntnu.no.backend.common.service.EmailServiceImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
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

        // Unngå 'From address must not be null':
        when(emailConfig.getEmailFrom()).thenReturn("noreply@clozet.com");

        // Andre config-verdier
        when(emailConfig.getBaseUrl()).thenReturn("http://localhost:5173");
        when(emailConfig.getVerificationExpiryHours()).thenReturn(24);
    }

    @Test
    void testSendVerificationEmail() throws Exception {
        // Arrange
        String token = "dummy-token";
        String toEmail = "test@example.com";

        // Oppretter en MimeMessage (uten session)
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendVerificationEmail(toEmail, token);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));

        // Få tak i hele MIME-innholdet
        Object content = mimeMessage.getContent();
        assertNotNull(content, "E-postens innhold skal ikke være null.");

        // Vanligvis er content et MimeMultipart
        assertTrue(content instanceof MimeMultipart,
            "Forventet at content er MimeMultipart, siden vi sender HTML-epost.");

        MimeMultipart multipart = (MimeMultipart) content;

        // Hent ut all tekst (inkl. HTML) rekursivt
        String extractedContent = extractTextFromMimeMultipart(multipart);

        // Sjekk verifikasjonslenke
        String expectedLink = "http://localhost:5173/verify?token=" + token;
        assertTrue(extractedContent.contains(expectedLink),
            "HTML-innholdet bør inneholde verifikasjonslenken: " + expectedLink);
    }

    /**
     * Itererer rekursivt gjennom en MimeMultipart og henter ut alle text/html eller text/plain-deler
     * som String. Dette unngår ClassCastException dersom e-posten er nestet (f.eks. multipart/alternative).
     */
    private String extractTextFromMimeMultipart(MimeMultipart mimeMultipart)
        throws MessagingException, IOException {

        StringBuilder sb = new StringBuilder();
        int count = mimeMultipart.getCount();

        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);

            // Hvis denne delen selv er en nested multipart, kall metoden rekursivt
            if (bodyPart.getContent() instanceof MimeMultipart) {
                sb.append(
                    extractTextFromMimeMultipart((MimeMultipart) bodyPart.getContent())
                );
            }
            // Ellers, hvis innholdet er tekst (HTML eller vanlig), hent ut som streng
            else {
                Object partContent = bodyPart.getContent();
                if (partContent != null) {
                    sb.append(partContent.toString());
                }
            }
        }
        return sb.toString();
    }
}
