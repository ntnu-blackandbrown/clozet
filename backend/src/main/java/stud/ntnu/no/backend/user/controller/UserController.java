package stud.ntnu.no.backend.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.user.dto.*;
import stud.ntnu.no.backend.user.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping
  public List<StatusUserDTO> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public UserDTO getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  // Registrering (uten e-post-sending)
  @PostMapping("/register")
  public ResponseEntity<?> createUser(@RequestBody RegisterUserDTO registerUserDTO) {
    // Oppretter bruker i DB i inaktiv tilstand
    UserDTO createdUser = userService.createUser(registerUserDTO);
    return ResponseEntity.ok(createdUser);
  }

  @PutMapping("/{id}")
  public UserDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO ) {
    return userService.updateUser(id, updateUserDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/login")
  public UserDTO login(@RequestBody LoginDTO loginDTO) {
    return userService.login(loginDTO);
  }
}
