package stud.ntnu.no.backend.transaction.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.config.TestConfig;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void testFindByCreatedAtBetween_WhenTransactionsExistInRange_ShouldReturnTransactions() {
        // Arrange
        User buyer = createUser("buyer", "buyer@example.com");
        User seller = createUser("seller", "seller@example.com");
        
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Category Description");
        entityManager.persist(category);
        
        Item item = createItem("Test Item", seller, category);
        entityManager.persist(item);
        
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        LocalDateTime nextWeek = LocalDateTime.now().plusWeeks(1);
        
        Transaction transaction1 = new Transaction();
        transaction1.setBuyerId(buyer.getId().toString());
        transaction1.setSellerId(seller.getId().toString());
        transaction1.setItem(item);
        transaction1.setAmount(100.0);
        transaction1.setStatus("COMPLETED");
        transaction1.setCreatedAt(yesterday);
        entityManager.persist(transaction1);
        
        Transaction transaction2 = new Transaction();
        transaction2.setBuyerId(buyer.getId().toString());
        transaction2.setSellerId(seller.getId().toString());
        transaction2.setItem(item);
        transaction2.setAmount(200.0);
        transaction2.setStatus("COMPLETED");
        transaction2.setCreatedAt(tomorrow);
        entityManager.persist(transaction2);
        
        Transaction transaction3 = new Transaction();
        transaction3.setBuyerId(buyer.getId().toString());
        transaction3.setSellerId(seller.getId().toString());
        transaction3.setItem(item);
        transaction3.setAmount(300.0);
        transaction3.setStatus("COMPLETED");
        transaction3.setCreatedAt(lastWeek);
        entityManager.persist(transaction3);
        
        Transaction transaction4 = new Transaction();
        transaction4.setBuyerId(buyer.getId().toString());
        transaction4.setSellerId(seller.getId().toString());
        transaction4.setItem(item);
        transaction4.setAmount(400.0);
        transaction4.setStatus("COMPLETED");
        transaction4.setCreatedAt(nextWeek);
        entityManager.persist(transaction4);
        
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
        User buyer = createUser("buyer", "buyer@example.com");
        User seller = createUser("seller", "seller@example.com");
        
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Category Description");
        entityManager.persist(category);
        
        Item item = createItem("Test Item", seller, category);
        entityManager.persist(item);
        
        Transaction transaction = new Transaction();
        transaction.setBuyerId(buyer.getId().toString());
        transaction.setSellerId(seller.getId().toString());
        transaction.setItem(item);
        transaction.setAmount(100.0);
        transaction.setStatus("COMPLETED");
        transaction.setCreatedAt(LocalDateTime.now().minusMonths(1));
        entityManager.persist(transaction);
        
        entityManager.flush();
        
        // Act
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(startDate, endDate);
        
        // Assert
        assertThat(transactions).isEmpty();
    }
    
    // Helper methods
    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("password");
        user.setActive(true);
        entityManager.persist(user);
        return user;
    }
    
    private Item createItem(String title, User seller, Category category) {
        Item item = new Item();
        item.setTitle(title);
        item.setSeller(seller);
        item.setCategory(category);
        item.setShortDescription("Short description");
        item.setLongDescription("Long description");
        item.setPrice(100.0);
        item.setCondition("New");
        item.setSize("M");
        item.setBrand("Brand");
        item.setColor("Black");
        item.setLatitude(0.0);
        item.setLongitude(0.0);
        item.setAvailable(true);
        item.setVippsPaymentEnabled(true);
        return item;
    }
} 