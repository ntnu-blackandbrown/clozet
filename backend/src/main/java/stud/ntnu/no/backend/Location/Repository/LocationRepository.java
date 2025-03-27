package stud.ntnu.no.backend.Location.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.backend.Location.Entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}