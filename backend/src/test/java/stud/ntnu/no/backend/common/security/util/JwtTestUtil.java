package stud.ntnu.no.backend.common.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtTestUtil {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure key

    public static String createValidJwt() {
        return Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration
                .signWith(key) // Use the secure key
                .compact();
    }
} 