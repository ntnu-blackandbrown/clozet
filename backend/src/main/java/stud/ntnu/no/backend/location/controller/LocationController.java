package stud.ntnu.no.backend.location.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.service.LocationService;

import java.util.List;

/**
 * REST controller for managing locations.
 * Provides endpoints for CRUD operations on locations.
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {

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
            @PathVariable Long id,
            @RequestBody CreateLocationDTO locationDTO) {
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
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}