package stud.ntnu.no.backend.ShippingOption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.backend.ShippingOption.Entity.ShippingOption;

@Repository
public interface ShippingOptionRepository extends JpaRepository<ShippingOption, Long> {
}