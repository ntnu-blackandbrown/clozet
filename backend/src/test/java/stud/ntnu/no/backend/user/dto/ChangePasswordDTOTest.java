package stud.ntnu.no.backend.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordDTOTest {

    @Test
    void testGettersAndSetters() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        
        String currentPassword = "oldPassword123";
        String newPassword = "newPassword456";
        
        dto.setCurrentPassword(currentPassword);
        dto.setNewPassword(newPassword);
        
        assertEquals(currentPassword, dto.getCurrentPassword());
        assertEquals(newPassword, dto.getNewPassword());
    }
    
    @Test
    void testDefaultValues() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        
        assertNull(dto.getCurrentPassword());
        assertNull(dto.getNewPassword());
    }
} 