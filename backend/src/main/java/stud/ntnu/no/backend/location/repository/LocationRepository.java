package stud.ntnu.no.backend.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.backend.location.entity.Location;

/**
 * Repository interface for Location entities.
 *
 * <p>This interface extends JpaRepository to provide CRUD operations for Location entities.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {}
