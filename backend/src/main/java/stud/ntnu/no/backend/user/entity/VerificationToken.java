package stud.ntnu.no.backend.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a verification token entity.
 * <p>
 * This entity is used to store verification tokens associated with users.
 * </p>
 */
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

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
    protected VerificationToken() {}

    /**
     * Constructs a new VerificationToken with the specified token, expiry date, and user.
     *
     * @param token the token string
     * @param expiryDate the expiry date of the token
     * @param user the user associated with the token
     */
    public VerificationToken(String token, LocalDateTime expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public User getUser() {
        return user;
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