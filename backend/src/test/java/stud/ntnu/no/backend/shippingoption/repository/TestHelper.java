package stud.ntnu.no.backend.shippingoption.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;

/** Helper class for providing test entities for ShippingOption repository tests. */
public class TestHelper {

  /**
   * Creates a basic shipping option entity and persists it.
   *
   * @param entityManager test entity manager
   * @param name name of the shipping option
   * @param price price of the shipping option
   * @return the created and persisted shipping option
   */
  public static ShippingOption createShippingOption(
      TestEntityManager entityManager, String name, double price) {
    ShippingOption option = new ShippingOption();
    option.setName(name);
    option.setDescription(name + " description");
    option.setPrice(price);
    entityManager.persist(option);
    return option;
  }

  /**
   * Creates a shipping option with full details.
   *
   * @param entityManager test entity manager
   * @param name name of the shipping option
   * @param description description of the shipping option
   * @param price price of the shipping option
   * @return the created and persisted shipping option
   */
  public static ShippingOption createShippingOptionWithDetails(
      TestEntityManager entityManager, String name, String description, double price) {
    ShippingOption option = new ShippingOption();
    option.setName(name);
    option.setDescription(description);
    option.setPrice(price);
    entityManager.persist(option);
    return option;
  }
}
