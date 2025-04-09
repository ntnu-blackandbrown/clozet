package stud.ntnu.no.backend.common.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for testing authentication.
 *
 * <p>Provides an endpoint to check the current authenticated user's details.
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

  /**
   * Returns information about the currently authenticated user.
   *
   * @return a response entity containing authentication details
   */
  @GetMapping("/who-am-i")
  public ResponseEntity<?> whoAmI() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    Map<String, Object> response = new HashMap<>();
    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
      response.put("authenticated", true);
      response.put("username", auth.getName());
      response.put("authorities", auth.getAuthorities().toString());
    } else {
      response.put("authenticated", false);
    }

    return ResponseEntity.ok(response);
  }
}
