package stud.ntnu.no.backend.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestDTOTest {

    @Test
    void testGettersAndSetters() {
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO();
        
        String email = "reset@example.com";
        
        dto.setEmail(email);
        
        assertEquals(email, dto.getEmail());
    }
    
    @Test
    void testDefaultValues() {
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO();
        
        assertNull(dto.getEmail());
    }
} 