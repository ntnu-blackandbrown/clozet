package stud.ntnu.no.backend.location.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.location.exception.LocationNotFoundException;
import stud.ntnu.no.backend.location.exception.LocationValidationException;
import stud.ntnu.no.backend.location.mapper.LocationMapper;
import stud.ntnu.no.backend.location.repository.LocationRepository;

@ExtendWith(MockitoExtension.class)
public class LocationServiceImplTest {

  @Mock private LocationRepository locationRepository;

  @Mock private LocationMapper locationMapper;

  @InjectMocks private LocationServiceImpl locationService;

  private Location location1;
  private Location location2;
  private LocationDTO locationDTO1;
  private LocationDTO locationDTO2;
  private CreateLocationDTO createLocationDTO;

  @BeforeEach
  void setUp() {
    // Setup test data
    location1 = new Location();
    location1.setId(1L);
    location1.setCity("Oslo");
    location1.setRegion("Oslo Region");
    location1.setLatitude(59.9139);
    location1.setLongitude(10.7522);

    location2 = new Location();
    location2.setId(2L);
    location2.setCity("Bergen");
    location2.setRegion("Vestland");
    location2.setLatitude(60.3913);
    location2.setLongitude(5.3221);

    locationDTO1 = new LocationDTO();
    locationDTO1.setId(1L);
    locationDTO1.setCity("Oslo");
    locationDTO1.setRegion("Oslo Region");
    locationDTO1.setLatitude(59.9139);
    locationDTO1.setLongitude(10.7522);

    locationDTO2 = new LocationDTO();
    locationDTO2.setId(2L);
    locationDTO2.setCity("Bergen");
    locationDTO2.setRegion("Vestland");
    locationDTO2.setLatitude(60.3913);
    locationDTO2.setLongitude(5.3221);

    createLocationDTO = new CreateLocationDTO();
    createLocationDTO.setCity("Trondheim");
    createLocationDTO.setRegion("Trøndelag");
    createLocationDTO.setLatitude(63.4305);
    createLocationDTO.setLongitude(10.3951);
  }

  @Test
  void getAllLocations_shouldReturnAllLocations() {
    // Arrange
    List<Location> locations = Arrays.asList(location1, location2);
    when(locationRepository.findAll()).thenReturn(locations);
    when(locationMapper.toDtoList(locations)).thenReturn(Arrays.asList(locationDTO1, locationDTO2));

    // Act
    List<LocationDTO> result = locationService.getAllLocations();

    // Assert
    assertEquals(2, result.size());
    assertEquals(locationDTO1, result.get(0));
    assertEquals(locationDTO2, result.get(1));
    verify(locationRepository, times(1)).findAll();
    verify(locationMapper, times(1)).toDtoList(locations);
  }

  @Test
  void getLocation_withValidId_shouldReturnLocation() {
    // Arrange
    when(locationRepository.findById(1L)).thenReturn(Optional.of(location1));
    when(locationMapper.toDto(location1)).thenReturn(locationDTO1);

    // Act
    LocationDTO result = locationService.getLocation(1L);

    // Assert
    assertEquals(locationDTO1, result);
    verify(locationRepository, times(1)).findById(1L);
    verify(locationMapper, times(1)).toDto(location1);
  }

  @Test
  void getLocation_withInvalidId_shouldThrowException() {
    // Arrange
    when(locationRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        LocationNotFoundException.class,
        () -> {
          locationService.getLocation(99L);
        });
    verify(locationRepository, times(1)).findById(99L);
  }

  @Test
  void createLocation_withValidData_shouldReturnCreatedLocation() {
    // Arrange
    Location newLocation = new Location();
    newLocation.setCity("Trondheim");
    newLocation.setRegion("Trøndelag");
    newLocation.setLatitude(63.4305);
    newLocation.setLongitude(10.3951);

    LocationDTO newLocationDTO = new LocationDTO();
    newLocationDTO.setId(3L);
    newLocationDTO.setCity("Trondheim");
    newLocationDTO.setRegion("Trøndelag");
    newLocationDTO.setLatitude(63.4305);
    newLocationDTO.setLongitude(10.3951);

    when(locationMapper.toEntity(createLocationDTO)).thenReturn(newLocation);
    when(locationRepository.save(newLocation)).thenReturn(newLocation);
    when(locationMapper.toDto(newLocation)).thenReturn(newLocationDTO);

    // Act
    LocationDTO result = locationService.createLocation(createLocationDTO);

    // Assert
    assertEquals(newLocationDTO, result);
    verify(locationMapper, times(1)).toEntity(createLocationDTO);
    verify(locationRepository, times(1)).save(newLocation);
    verify(locationMapper, times(1)).toDto(newLocation);
  }

  @Test
  void createLocation_withInvalidCity_shouldThrowException() {
    // Arrange
    CreateLocationDTO invalidDTO = new CreateLocationDTO();
    invalidDTO.setCity("");
    invalidDTO.setRegion("Trøndelag");
    invalidDTO.setLatitude(63.4305);
    invalidDTO.setLongitude(10.3951);

    // Act & Assert
    assertThrows(
        LocationValidationException.class,
        () -> {
          locationService.createLocation(invalidDTO);
        });
  }

  @Test
  void createLocation_withInvalidRegion_shouldThrowException() {
    // Arrange
    CreateLocationDTO invalidDTO = new CreateLocationDTO();
    invalidDTO.setCity("Trondheim");
    invalidDTO.setRegion("");
    invalidDTO.setLatitude(63.4305);
    invalidDTO.setLongitude(10.3951);

    // Act & Assert
    assertThrows(
        LocationValidationException.class,
        () -> {
          locationService.createLocation(invalidDTO);
        });
  }

  @Test
  void createLocation_withInvalidLatitude_shouldThrowException() {
    // Arrange
    CreateLocationDTO invalidDTO = new CreateLocationDTO();
    invalidDTO.setCity("Trondheim");
    invalidDTO.setRegion("Trøndelag");
    invalidDTO.setLatitude(95.0); // Invalid latitude
    invalidDTO.setLongitude(10.3951);

    // Act & Assert
    assertThrows(
        LocationValidationException.class,
        () -> {
          locationService.createLocation(invalidDTO);
        });
  }

  @Test
  void createLocation_withInvalidLongitude_shouldThrowException() {
    // Arrange
    CreateLocationDTO invalidDTO = new CreateLocationDTO();
    invalidDTO.setCity("Trondheim");
    invalidDTO.setRegion("Trøndelag");
    invalidDTO.setLatitude(63.4305);
    invalidDTO.setLongitude(190.0); // Invalid longitude

    // Act & Assert
    assertThrows(
        LocationValidationException.class,
        () -> {
          locationService.createLocation(invalidDTO);
        });
  }

  @Test
  void updateLocation_withValidId_shouldReturnUpdatedLocation() {
    // Arrange
    when(locationRepository.findById(1L)).thenReturn(Optional.of(location1));
    doNothing().when(locationMapper).updateLocationFromDto(createLocationDTO, location1);
    when(locationRepository.save(location1)).thenReturn(location1);
    when(locationMapper.toDto(location1)).thenReturn(locationDTO1);

    // Act
    LocationDTO result = locationService.updateLocation(1L, createLocationDTO);

    // Assert
    assertEquals(locationDTO1, result);
    verify(locationRepository, times(1)).findById(1L);
    verify(locationMapper, times(1)).updateLocationFromDto(createLocationDTO, location1);
    verify(locationRepository, times(1)).save(location1);
    verify(locationMapper, times(1)).toDto(location1);
  }

  @Test
  void updateLocation_withInvalidId_shouldThrowException() {
    // Arrange
    when(locationRepository.findById(99L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        LocationNotFoundException.class,
        () -> {
          locationService.updateLocation(99L, createLocationDTO);
        });
    verify(locationRepository, times(1)).findById(99L);
  }

  @Test
  void updateLocation_withInvalidData_shouldThrowException() {
    // Arrange
    when(locationRepository.findById(1L)).thenReturn(Optional.of(location1));

    CreateLocationDTO invalidDTO = new CreateLocationDTO();
    invalidDTO.setCity("");
    invalidDTO.setRegion("Oslo Region");
    invalidDTO.setLatitude(59.9139);
    invalidDTO.setLongitude(10.7522);

    // Act & Assert
    assertThrows(
        LocationValidationException.class,
        () -> {
          locationService.updateLocation(1L, invalidDTO);
        });
    verify(locationRepository, times(1)).findById(1L);
  }

  @Test
  void deleteLocation_withValidId_shouldDeleteLocation() {
    // Arrange
    when(locationRepository.existsById(1L)).thenReturn(true);
    doNothing().when(locationRepository).deleteById(1L);

    // Act
    locationService.deleteLocation(1L);

    // Assert
    verify(locationRepository, times(1)).existsById(1L);
    verify(locationRepository, times(1)).deleteById(1L);
  }

  @Test
  void deleteLocation_withInvalidId_shouldThrowException() {
    // Arrange
    when(locationRepository.existsById(99L)).thenReturn(false);

    // Act & Assert
    assertThrows(
        LocationNotFoundException.class,
        () -> {
          locationService.deleteLocation(99L);
        });
    verify(locationRepository, times(1)).existsById(99L);
    verify(locationRepository, never()).deleteById(anyLong());
  }
}
