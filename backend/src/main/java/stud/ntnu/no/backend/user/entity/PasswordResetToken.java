package stud.ntnu.no.backend.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a password reset token entity.
 * <p>
 * This entity is used to store password reset tokens associated with users.
 * </p>
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // For JPA
    protected PasswordResetToken() {}

    /**
     * Constructs a new PasswordResetToken with the specified token, expiry date, and user.
     *
     * @param token the token string
     * @param expiryDate the expiry date of the token
     * @param user the user associated with the token
     */
    public PasswordResetToken(String token, LocalDateTime expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Checks if the token is expired.
     *
     * @return true if the token is expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}