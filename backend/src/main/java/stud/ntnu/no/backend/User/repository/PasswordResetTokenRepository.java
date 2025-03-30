package stud.ntnu.no.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.user.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
