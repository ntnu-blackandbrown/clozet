package stud.ntnu.no.backend.location.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationDTOTest {

    @Test
    void testGettersAndSetters() {
        LocationDTO dto = new LocationDTO();
        
        Long id = 1L;
        String city = "Trondheim";
        String region = "Trøndelag";
        double latitude = 63.4305;
        double longitude = 10.3951;
        String name = "Trondheim, Trøndelag";
        
        dto.setId(id);
        dto.setCity(city);
        dto.setRegion(region);
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setName(name);
        
        assertEquals(id, dto.getId());
        assertEquals(city, dto.getCity());
        assertEquals(region, dto.getRegion());
        assertEquals(latitude, dto.getLatitude());
        assertEquals(longitude, dto.getLongitude());
        assertEquals(name, dto.getName());
    }
    
    @Test
    void testDefaultValues() {
        LocationDTO dto = new LocationDTO();
        
        assertNull(dto.getId());
        assertNull(dto.getCity());
        assertNull(dto.getRegion());
        assertEquals(0.0, dto.getLatitude());
        assertEquals(0.0, dto.getLongitude());
        assertNull(dto.getName());
    }
} 