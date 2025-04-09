package stud.ntnu.no.backend.common.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.common.security.model.CustomUserDetails;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

/**
 * Service for loading user details from the database.
 * <p>
 * This service implements UserDetailsService and provides user details for authentication.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

  @Autowired
  private UserRepository userRepository;

  /**
   * Loads user details by username.
   *
   * @param username the username
   * @return the user details
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.debug("Loading user details for: {}", username);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          logger.warn("User not found: {}", username);
          return new UsernameNotFoundException("User not found: " + username);
        });
    logger.debug("User found: {}, active: {}", username, user.isActive());

    return new CustomUserDetails(user);
  }
}
