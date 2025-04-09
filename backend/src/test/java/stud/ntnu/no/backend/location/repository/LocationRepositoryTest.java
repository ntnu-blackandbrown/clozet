package stud.ntnu.no.backend.location.repository;

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
import stud.ntnu.no.backend.location.entity.Location;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(LocationRepositoryTestConfig.class)
@TestPropertySource(
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
    })
public class LocationRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private LocationRepository locationRepository;

  @Test
  public void testSaveAndFindById_ShouldReturnSavedLocation() {
    // Arrange
    Location location = TestHelper.createLocation(entityManager, "Oslo");
    entityManager.flush();
    entityManager.clear();

    // Act
    Optional<Location> found = locationRepository.findById(location.getId());

    // Assert
    assertThat(found).isPresent();
    assertThat(found.get().getCity()).isEqualTo("Oslo");
    assertThat(found.get().getRegion()).isEqualTo("Oslo Region");
  }

  @Test
  public void testFindAll_ShouldReturnAllLocations() {
    // Arrange
    TestHelper.createLocation(entityManager, "Oslo");
    TestHelper.createLocation(entityManager, "Bergen");
    TestHelper.createLocation(entityManager, "Trondheim");
    entityManager.flush();
    entityManager.clear();

    // Act
    List<Location> locations = locationRepository.findAll();

    // Assert
    assertThat(locations).hasSize(3);
    assertThat(locations)
        .extracting(Location::getCity)
        .containsExactlyInAnyOrder("Oslo", "Bergen", "Trondheim");
  }

  @Test
  public void testSaveLocationWithCoordinates_ShouldPersistCoordinates() {
    // Arrange
    Location location =
        TestHelper.createLocationWithCoordinates(
            entityManager, "Stockholm", "Stockholm Region", 59.3293, 18.0686);
    entityManager.flush();
    entityManager.clear();

    // Act
    Optional<Location> found = locationRepository.findById(location.getId());

    // Assert
    assertThat(found).isPresent();
    assertThat(found.get().getCity()).isEqualTo("Stockholm");
    assertThat(found.get().getLatitude()).isEqualTo(59.3293);
    assertThat(found.get().getLongitude()).isEqualTo(18.0686);
  }

  @Test
  public void testDeleteLocation_ShouldRemoveLocation() {
    // Arrange
    Location location = TestHelper.createLocation(entityManager, "Stavanger");
    entityManager.flush();

    Long locationId = location.getId();

    // Act
    locationRepository.deleteById(locationId);
    entityManager.flush();
    entityManager.clear();

    Optional<Location> found = locationRepository.findById(locationId);

    // Assert
    assertThat(found).isEmpty();
  }
}
