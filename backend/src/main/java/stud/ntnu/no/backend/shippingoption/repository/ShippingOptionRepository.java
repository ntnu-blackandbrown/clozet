package stud.ntnu.no.backend.shippingoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;

/**
 * Repository interface for ShippingOption entities.
 *
 * <p>This interface extends JpaRepository to provide CRUD operations for ShippingOption entities.
 */
@Repository
public interface ShippingOptionRepository extends JpaRepository<ShippingOption, Long> {}
