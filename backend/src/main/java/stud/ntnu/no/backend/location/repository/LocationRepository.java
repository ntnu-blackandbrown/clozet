package stud.ntnu.no.backend.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.backend.location.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}