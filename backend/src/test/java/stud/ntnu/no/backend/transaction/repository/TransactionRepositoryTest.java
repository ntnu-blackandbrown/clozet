package stud.ntnu.no.backend.transaction.repository;

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
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TransactionRepositoryTestConfig.class)
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void testFindByCreatedAtBetween_WhenTransactionsExistInRange_ShouldReturnTransactions() {
        // Arrange
        User buyer = TestHelper.createUser(entityManager, "buyer", "buyer@example.com");
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);
        
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        LocalDateTime nextWeek = LocalDateTime.now().plusWeeks(1);
        
        TestHelper.createTransaction(entityManager, buyer, seller, item, 100.0, "COMPLETED", yesterday);
        TestHelper.createTransaction(entityManager, buyer, seller, item, 200.0, "COMPLETED", tomorrow);
        TestHelper.createTransaction(entityManager, buyer, seller, item, 300.0, "COMPLETED", lastWeek);
        TestHelper.createTransaction(entityManager, buyer, seller, item, 400.0, "COMPLETED", nextWeek);
        
        entityManager.flush();
        
        // Act
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(startDate, endDate);
        
        // Assert
        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getAmount)
                                .containsExactlyInAnyOrder(100.0, 200.0);
    }
    
    @Test
    public void testFindByCreatedAtBetween_WhenNoTransactionsExistInRange_ShouldReturnEmptyList() {
        // Arrange
        User buyer = TestHelper.createUser(entityManager, "buyer", "buyer@example.com");
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);
        
        TestHelper.createTransaction(entityManager, buyer, seller, item, 100.0, "COMPLETED", LocalDateTime.now().minusMonths(1));
        
        entityManager.flush();
        
        // Act
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(startDate, endDate);
        
        // Assert
        assertThat(transactions).isEmpty();
    }
} 