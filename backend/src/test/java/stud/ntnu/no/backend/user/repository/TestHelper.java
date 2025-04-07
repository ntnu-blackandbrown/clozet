package stud.ntnu.no.backend.user.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import stud.ntnu.no.backend.user.entity.User;

/**
 * Helper class for providing test entities.
 */
public class TestHelper {

    /**
     * Creates and persists a basic user in the test database.
     * 
     * @param entityManager test entity manager
     * @param username username for the user
     * @param email email for the user
     * @return the created and persisted user
     */
    public static User createUser(TestEntityManager entityManager, String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("password_hash");
        user.setActive(true);
        entityManager.persist(user);
        return user;
    }
} 