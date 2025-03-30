package stud.ntnu.no.backend.email.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kjører en integrasjonstest som sender ekte e-post til Papercut (lokalt).
 * Forutsetter at Papercut lytter på localhost:25.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")  // Sørg for at du bruker application-test.properties med Papercut-oppsett
@Profile("test")
class RegistrationIntegrationTestPapercut {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    /**
     * Testen kaller /api/users/register med en ny bruker,
     * og forventer at e-post faktisk sendes (til Papercut-lokal SMTP).
     *
     * Du bekrefter manuelt i Papercut-vinduet at e-posten dukket opp.
     */
    @Test
    void testUserRegistrationSendsEmailToPapercut() {
        // 1) Forbered testdata
        RegisterUserDTO registerDto = new RegisterUserDTO();
        registerDto.setUsername("papercutTestUser");
        registerDto.setEmail("papercut@example.com");
        registerDto.setPassword("Password123");

        // 2) Kall endepunktet over HTTP
        String baseUrl = "http://localhost:" + port;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterUserDTO> requestEntity = new HttpEntity<>(registerDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/users/register",
                requestEntity,
                String.class
        );

        // 3) Bekreft at vi får HTTP 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Bruker opprettet") 
                || response.getBody().contains("User created"),
                "Responsen bør indikere at bruker er opprettet");

        // 4) Bekreft at brukeren ligger i databasen (inaktiv før verifisering?)
        Optional<User> userOpt = userRepository.findByUsername("papercutTestUser");
        assertTrue(userOpt.isPresent(), "Brukeren ble ikke funnet i databasen");
        User createdUser = userOpt.get();
        assertFalse(createdUser.isActive(), "Brukeren skal vanligvis være inaktiv til verifisering");

        // 5) Til slutt: Åpne Papercut og sjekk at e-posten dukket opp!
        //    (Ingen automatisk validering i koden, du gjør dette manuelt.)
    }
}
