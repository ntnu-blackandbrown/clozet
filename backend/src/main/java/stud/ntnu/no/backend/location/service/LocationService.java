package stud.ntnu.no.backend.location.service;

import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;

import java.util.List;

/**
 * Service interface for managing locations.
 * <p>
 * This interface defines methods for CRUD operations on locations.
 */
public interface LocationService {
    /**
     * Retrieves all locations.
     *
     * @return a list of LocationDTOs
     */
    List<LocationDTO> getAllLocations();

    /**
     * Retrieves a location by its ID.
     *
     * @param id the ID of the location
     * @return the LocationDTO
     */
    LocationDTO getLocation(Long id);

    /**
     * Creates a new location.
     *
     * @param locationDTO the CreateLocationDTO
     * @return the created LocationDTO
     */
    LocationDTO createLocation(CreateLocationDTO locationDTO);

    /**
     * Updates an existing location.
     *
     * @param id the ID of the location to update
     * @param locationDTO the CreateLocationDTO with updated information
     * @return the updated LocationDTO
     */
    LocationDTO updateLocation(Long id, CreateLocationDTO locationDTO);

    /**
     * Deletes a location by its ID.
     *
     * @param id the ID of the location to delete
     */
    void deleteLocation(Long id);
}