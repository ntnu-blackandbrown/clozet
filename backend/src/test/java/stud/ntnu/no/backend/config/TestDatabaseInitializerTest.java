package stud.ntnu.no.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestDatabaseInitializer.class)  // Eksplisitt import av testinitializeren
public class TestDatabaseInitializerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testOnlyAdminUserExists() {
        List<User> users = userRepository.findAll();
        // Vi forventer at kun admin-brukeren er tilstede
        assertEquals(1, users.size(), "Det skal kun være én bruker i databasen");

        User admin = users.get(0);
        assertEquals("Admin", admin.getUsername(), "Brukernavnet skal være 'Admin'");
        assertTrue(admin.isActive(), "Admin-brukeren skal være aktiv");
    }
}
