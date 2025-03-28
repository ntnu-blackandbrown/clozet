package stud.ntnu.no.backend.location.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.service.LocationService;


import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocation(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocation(id));
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody CreateLocationDTO locationDTO) {
        return new ResponseEntity<>(locationService.createLocation(locationDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(
            @PathVariable Long id,
            @RequestBody CreateLocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}