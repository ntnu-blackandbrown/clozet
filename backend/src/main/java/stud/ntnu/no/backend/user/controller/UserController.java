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

    // Endre mapping slik at kun numeriske verdier matches
    @GetMapping("/{id:\\d+}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }


    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterUserDTO dto) {
        // Kall ny service-metode som håndterer alt (oppretting + verifikasjons-e-post)
        userService.createUserAndSendVerificationEmail(dto);

        // Returner en enkel suksessmelding (eller en UserDTO, hvis du ønsker)
        return ResponseEntity.ok("Bruker opprettet. Sjekk e-post for verifisering.");
    }

    @PutMapping("/{id:\\d+}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO ) {
        return userService.updateUser(id, updateUserDTO);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/login")
    public UserDTO login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }
}
