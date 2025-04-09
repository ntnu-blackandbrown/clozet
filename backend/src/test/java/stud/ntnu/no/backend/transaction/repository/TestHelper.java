package stud.ntnu.no.backend.transaction.repository;

import java.time.LocalDateTime;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.user.entity.User;

/** Helper class for providing test entities. */
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
    user.setPasswordHash("password");
    user.setActive(true);
    entityManager.persist(user);
    return user;
  }

  /**
   * Creates a basic location entity and persists it.
   *
   * @param entityManager test entity manager
   * @param name name of the location
   * @return the created and persisted location
   */
  public static Location createLocation(TestEntityManager entityManager, String name) {
    Location location = new Location();
    location.setCity(name);
    location.setRegion("Oslo Region");
    location.setLatitude(59.9139);
    location.setLongitude(10.7522);
    entityManager.persist(location);
    return location;
  }

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
    option.setDescription("Test shipping option");
    option.setPrice(price);
    entityManager.persist(option);
    return option;
  }

  /**
   * Creates a basic category with required fields
   *
   * @param entityManager test entity manager
   * @param name name of the category
   * @return the created and persisted category
   */
  public static Category createCategory(TestEntityManager entityManager, String name) {
    Category category = new Category();
    category.setName(name);
    category.setDescription(name + " Description");
    entityManager.persist(category);
    return category;
  }

  /**
   * Creates a basic item with all required dependencies.
   *
   * @param entityManager test entity manager
   * @param title title of the item
   * @param seller seller of the item
   * @param category category of the item
   * @return the created and persisted item
   */
  public static Item createBasicItem(
      TestEntityManager entityManager, String title, User seller, Category category) {
    Location location = createLocation(entityManager, "Oslo");
    ShippingOption shippingOption = createShippingOption(entityManager, "Standard", 99.0);

    Item item = new Item();
    item.setTitle(title);
    item.setSeller(seller);
    item.setCategory(category);
    item.setLocation(location);
    item.setShippingOption(shippingOption);
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

    // Set timestamp fields
    LocalDateTime now = LocalDateTime.now();
    item.setCreatedAt(now);
    item.setUpdatedAt(now);

    entityManager.persist(item);
    return item;
  }

  /**
   * Creates a basic transaction with required fields.
   *
   * @param entityManager test entity manager
   * @param buyer buyer user
   * @param seller seller user
   * @param item item being transacted
   * @param amount transaction amount
   * @param status transaction status
   * @param createdAt when transaction was created
   * @return the created and persisted transaction
   */
  public static Transaction createTransaction(
      TestEntityManager entityManager,
      User buyer,
      User seller,
      Item item,
      double amount,
      String status,
      LocalDateTime createdAt) {
    Transaction transaction = new Transaction();
    transaction.setBuyerId(buyer.getId().toString());
    transaction.setSellerId(seller.getId().toString());
    transaction.setItem(item);
    transaction.setAmount(amount);
    transaction.setStatus(status);
    transaction.setCreatedAt(createdAt);
    entityManager.persist(transaction);
    return transaction;
  }
}
