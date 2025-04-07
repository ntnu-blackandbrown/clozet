package stud.ntnu.no.backend.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginDTOTest {

    @Test
    void testGettersAndSetters() {
        LoginDTO dto = new LoginDTO();
        
        String usernameOrEmail = "user@example.com";
        String password = "securePassword789";
        
        dto.setUsernameOrEmail(usernameOrEmail);
        dto.setPassword(password);
        
        assertEquals(usernameOrEmail, dto.getUsernameOrEmail());
        assertEquals(password, dto.getPassword());
    }
    
    @Test
    void testDefaultValues() {
        LoginDTO dto = new LoginDTO();
        
        assertNull(dto.getUsernameOrEmail());
        assertNull(dto.getPassword());
    }
} 