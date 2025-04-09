package stud.ntnu.no.backend.location.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.service.LocationService;

/** REST controller for managing locations. Provides endpoints for CRUD operations on locations. */
@RestController
@RequestMapping("/api/locations")
public class LocationController {

  private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

  private final LocationService locationService;

  public LocationController(LocationService locationService) {
    this.locationService = locationService;
  }

  /**
   * Retrieves all locations.
   *
   * @return A ResponseEntity containing a list of LocationDTOs
   */
  @GetMapping
  public ResponseEntity<List<LocationDTO>> getAllLocations() {
    logger.info("Received request to fetch all locations");
    return ResponseEntity.ok(locationService.getAllLocations());
  }

  /**
   * Retrieves a location by its ID.
   *
   * @param id The ID of the location to retrieve
   * @return A ResponseEntity containing the LocationDTO
   */
  @GetMapping("/{id}")
  public ResponseEntity<LocationDTO> getLocation(@PathVariable Long id) {
    logger.info("Received request to fetch location with id: {}", id);
    return ResponseEntity.ok(locationService.getLocation(id));
  }

  /**
   * Creates a new location.
   *
   * @param locationDTO The DTO containing location details
   * @return A ResponseEntity containing the created LocationDTO
   */
  @PostMapping
  public ResponseEntity<LocationDTO> createLocation(@RequestBody CreateLocationDTO locationDTO) {
    logger.info("Received request to create a new location with details: {}", locationDTO);
    return new ResponseEntity<>(locationService.createLocation(locationDTO), HttpStatus.CREATED);
  }

  /**
   * Updates an existing location.
   *
   * @param id The ID of the location to update
   * @param locationDTO The DTO containing updated location details
   * @return A ResponseEntity containing the updated LocationDTO
   */
  @PutMapping("/{id}")
  public ResponseEntity<LocationDTO> updateLocation(
      @PathVariable Long id, @RequestBody CreateLocationDTO locationDTO) {
    logger.info("Received request to update location with id: {}", id);
    return ResponseEntity.ok(locationService.updateLocation(id, locationDTO));
  }

  /**
   * Deletes a location by its ID.
   *
   * @param id The ID of the location to delete
   * @return A ResponseEntity with no content
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
    logger.info("Received request to delete location with id: {}", id);
    locationService.deleteLocation(id);
    return ResponseEntity.noContent().build();
  }
}
