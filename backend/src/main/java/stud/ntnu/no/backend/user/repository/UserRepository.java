package stud.ntnu.no.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
}