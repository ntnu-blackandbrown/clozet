package stud.ntnu.no.backend.User.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.User.DTOs.LoginDTO;
import stud.ntnu.no.backend.User.DTOs.RegisterUserDTO;
import stud.ntnu.no.backend.User.DTOs.StatusUserDTO;
import stud.ntnu.no.backend.User.DTOs.UpdateUserDTO;
import stud.ntnu.no.backend.User.DTOs.UserDTO;
import stud.ntnu.no.backend.User.Service.UserService;

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

  @PostMapping
  public UserDTO createUser(@RequestBody RegisterUserDTO registerUserDTO) {
    return userService.createUser(registerUserDTO);
  }

  @PutMapping("/{id}")
  public UserDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
    return userService.updateUser(id, updateUserDTO );
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