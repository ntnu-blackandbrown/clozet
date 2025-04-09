package stud.ntnu.no.backend.shippingoption.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ShippingOptionRepositoryTestConfig.class)
@TestPropertySource(
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
    })
public class ShippingOptionRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private ShippingOptionRepository shippingOptionRepository;

  @Test
  public void testSaveAndFindById_ShouldReturnSavedShippingOption() {
    // Arrange
    ShippingOption option =
        TestHelper.createShippingOption(entityManager, "Standard Shipping", 99.0);
    entityManager.flush();
    entityManager.clear();

    // Act
    Optional<ShippingOption> found = shippingOptionRepository.findById(option.getId());

    // Assert
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Standard Shipping");
    assertThat(found.get().getPrice()).isEqualTo(99.0);
  }

  @Test
  public void testFindAll_ShouldReturnAllShippingOptions() {
    // Arrange
    TestHelper.createShippingOption(entityManager, "Standard Shipping", 99.0);
    TestHelper.createShippingOption(entityManager, "Express Shipping", 199.0);
    TestHelper.createShippingOption(entityManager, "Next Day Delivery", 299.0);
    entityManager.flush();
    entityManager.clear();

    // Act
    List<ShippingOption> options = shippingOptionRepository.findAll();

    // Assert
    assertThat(options).hasSize(3);
    assertThat(options)
        .extracting(ShippingOption::getName)
        .containsExactlyInAnyOrder("Standard Shipping", "Express Shipping", "Next Day Delivery");
  }

  @Test
  public void testUpdateShippingOption_ShouldPersistChanges() {
    // Arrange
    ShippingOption option =
        TestHelper.createShippingOption(entityManager, "Standard Shipping", 99.0);
    entityManager.flush();

    // Act
    option.setPrice(129.0);
    option.setDescription("Updated description");
    shippingOptionRepository.save(option);
    entityManager.flush();
    entityManager.clear();

    // Assert
    Optional<ShippingOption> updated = shippingOptionRepository.findById(option.getId());
    assertThat(updated).isPresent();
    assertThat(updated.get().getPrice()).isEqualTo(129.0);
    assertThat(updated.get().getDescription()).isEqualTo("Updated description");
  }

  @Test
  public void testDeleteShippingOption_ShouldRemoveOption() {
    // Arrange
    ShippingOption option = TestHelper.createShippingOption(entityManager, "To be deleted", 99.0);
    entityManager.flush();

    Long optionId = option.getId();

    // Act
    shippingOptionRepository.deleteById(optionId);
    entityManager.flush();
    entityManager.clear();

    // Assert
    Optional<ShippingOption> found = shippingOptionRepository.findById(optionId);
    assertThat(found).isEmpty();
  }
}
