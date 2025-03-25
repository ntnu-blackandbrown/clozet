package stud.ntnu.no.backend.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.User.Model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  Optional<User> findByUsername(String username);
}