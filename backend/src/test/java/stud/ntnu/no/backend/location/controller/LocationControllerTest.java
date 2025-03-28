package stud.ntnu.no.backend.location.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.service.LocationService;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    @Mock
    private LocationService locationService;

    @InjectMocks
    private LocationController locationController;

    private LocationDTO sampleLocationDTO;
    private CreateLocationDTO createLocationDTO;
    private List<LocationDTO> locationDTOList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup sample data
        sampleLocationDTO = new LocationDTO();
        sampleLocationDTO.setId(1L);
        sampleLocationDTO.setCity("Trondheim");
        sampleLocationDTO.setRegion("Trøndelag");
        sampleLocationDTO.setLatitude(63.4308);
        sampleLocationDTO.setLongitude(10.4034);
        sampleLocationDTO.setName("Trondheim, Trøndelag");
        
        LocationDTO locationDTO2 = new LocationDTO();
        locationDTO2.setId(2L);
        locationDTO2.setCity("Oslo");
        locationDTO2.setRegion("Oslo");
        locationDTO2.setLatitude(59.9139);
        locationDTO2.setLongitude(10.7522);
        locationDTO2.setName("Oslo, Oslo");
        
        locationDTOList = Arrays.asList(sampleLocationDTO, locationDTO2);
        
        createLocationDTO = new CreateLocationDTO();
        createLocationDTO.setCity("Bergen");
        createLocationDTO.setRegion("Vestland");
        createLocationDTO.setLatitude(60.3913);
        createLocationDTO.setLongitude(5.3221);
    }

    @Test
    void getAllLocations_ShouldReturnListOfLocations() {
        when(locationService.getAllLocations()).thenReturn(locationDTOList);
        
        ResponseEntity<List<LocationDTO>> response = locationController.getAllLocations();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(locationService, times(1)).getAllLocations();
    }

    @Test
    void getLocation_WithValidId_ShouldReturnLocation() {
        when(locationService.getLocation(1L)).thenReturn(sampleLocationDTO);
        
        ResponseEntity<LocationDTO> response = locationController.getLocation(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Trondheim", response.getBody().getCity());
        verify(locationService, times(1)).getLocation(1L);
    }

    @Test
    void createLocation_WithValidData_ShouldReturnCreatedLocation() {
        when(locationService.createLocation(any(CreateLocationDTO.class))).thenReturn(sampleLocationDTO);
        
        ResponseEntity<LocationDTO> response = locationController.createLocation(createLocationDTO);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(locationService, times(1)).createLocation(any(CreateLocationDTO.class));
    }

    @Test
    void updateLocation_WithValidData_ShouldReturnUpdatedLocation() {
        when(locationService.updateLocation(eq(1L), any(CreateLocationDTO.class))).thenReturn(sampleLocationDTO);
        
        ResponseEntity<LocationDTO> response = locationController.updateLocation(1L, createLocationDTO);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(locationService, times(1)).updateLocation(eq(1L), any(CreateLocationDTO.class));
    }

    @Test
    void deleteLocation_ShouldReturnNoContent() {
        doNothing().when(locationService).deleteLocation(1L);
        
        ResponseEntity<Void> response = locationController.deleteLocation(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(locationService, times(1)).deleteLocation(1L);
    }
}