package stud.ntnu.no.backend.message.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MessageRepositoryTestConfig.class)
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void testFindBySenderIdOrReceiverId_WhenUserIsSender_ShouldReturnMessages() {
        // Arrange
        User sender = TestHelper.createUser(entityManager, "sender", "sender@example.com");
        User receiver = TestHelper.createUser(entityManager, "receiver", "receiver@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        Item item = TestHelper.createBasicItem(entityManager, "Test Item", sender, category);
        
        Message message1 = TestHelper.createMessage(entityManager, sender, receiver, item, "Hello from sender");
        Message message2 = TestHelper.createMessage(entityManager, receiver, sender, item, "Hello from receiver");
        
        entityManager.flush();

        // Act
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(
            sender.getId().toString(), sender.getId().toString());

        // Assert
        assertThat(messages).hasSize(2);
        assertThat(messages).extracting(Message::getContent)
                           .containsExactlyInAnyOrder("Hello from sender", "Hello from receiver");
    }

    @Test
    public void testFindBySenderIdOrReceiverId_WhenUserIsReceiver_ShouldReturnMessages() {
        // Arrange
        User user1 = TestHelper.createUser(entityManager, "user1", "user1@example.com");
        User user2 = TestHelper.createUser(entityManager, "user2", "user2@example.com");
        User user3 = TestHelper.createUser(entityManager, "user3", "user3@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        Item item = TestHelper.createBasicItem(entityManager, "Test Item", user1, category);
        
        Message message1 = TestHelper.createMessage(entityManager, user1, user2, item, "Message from 1 to 2");
        Message message2 = TestHelper.createMessage(entityManager, user3, user2, item, "Message from 3 to 2");
        Message message3 = TestHelper.createMessage(entityManager, user1, user3, item, "Message from 1 to 3");
        
        entityManager.flush();

        // Act
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(
            user2.getId().toString(), user2.getId().toString());

        // Assert
        assertThat(messages).hasSize(2);
        assertThat(messages).extracting(Message::getContent)
                           .containsExactlyInAnyOrder("Message from 1 to 2", "Message from 3 to 2");
    }

    @Test
    public void testFindBySenderIdOrReceiverId_WhenUserHasNoMessages_ShouldReturnEmptyList() {
        // Arrange
        User user1 = TestHelper.createUser(entityManager, "user1", "user1@example.com");
        User user2 = TestHelper.createUser(entityManager, "user2", "user2@example.com");
        User user3 = TestHelper.createUser(entityManager, "user3", "user3@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        Item item = TestHelper.createBasicItem(entityManager, "Test Item", user1, category);
        
        Message message = TestHelper.createMessage(entityManager, user1, user2, item, "Message from 1 to 2");
        
        entityManager.flush();

        // Act
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(
            user3.getId().toString(), user3.getId().toString());

        // Assert
        assertThat(messages).isEmpty();
    }
} 