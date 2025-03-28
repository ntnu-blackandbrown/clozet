package stud.ntnu.no.backend.common.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(UserDetails userDetails) {
        logger.info("Genererer JWT for bruker: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            logger.debug("JWT token validert");
            return true;
        } catch (SignatureException e) {
            logger.error("Ugyldig JWT signatur: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Ugyldig JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token er utløpt: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token støttes ikke: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string er tom: {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        logger.debug("Henter brukernavn fra JWT");
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        logger.debug("Oppretter JWT med utløpsdato: {}", expiryDate);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}