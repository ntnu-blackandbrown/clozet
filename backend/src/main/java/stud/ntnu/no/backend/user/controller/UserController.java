package stud.ntnu.no.backend.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.StatusUserDTO;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
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