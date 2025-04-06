package stud.ntnu.no.backend.location.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.location.controller.LocationController;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.location.exception.LocationNotFoundException;
import stud.ntnu.no.backend.location.exception.LocationValidationException;
import stud.ntnu.no.backend.location.mapper.LocationMapper;
import stud.ntnu.no.backend.location.repository.LocationRepository;


import java.util.List;

/**
 * Implementation of the LocationService interface.
 * <p>
 * This class provides methods for managing locations, including CRUD operations.
 */
@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    /**
     * Constructs a new LocationServiceImpl with the specified repository and mapper.
     *
     * @param locationRepository the LocationRepository
     * @param locationMapper the LocationMapper
     */
    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    public List<LocationDTO> getAllLocations() {
        logger.info("Fetching all locations");
        return locationMapper.toDtoList(locationRepository.findAll());
    }

    @Override
    public LocationDTO getLocation(Long id) {
        logger.info("Fetching location with id: {}", id);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDTO createLocation(CreateLocationDTO locationDTO) {
        logger.info("Creating new location");
        validateLocation(locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDTO updateLocation(Long id, CreateLocationDTO locationDTO) {
        logger.info("Updating location with id: {}", id);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));

        validateLocation(locationDTO);
        locationMapper.updateLocationFromDto(locationDTO, location);
        location = locationRepository.save(location);
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        logger.info("Deleting location with id: {}", id);
        if (!locationRepository.existsById(id)) {
            throw new LocationNotFoundException(id);
        }
        locationRepository.deleteById(id);
    }

    /**
     * Validates the given CreateLocationDTO.
     * <p>
     * This method checks that the city and region are not empty and that the latitude
     * and longitude are within valid ranges.
     *
     * @param locationDTO the CreateLocationDTO to validate
     * @throws LocationValidationException if validation fails
     */
    private void validateLocation(CreateLocationDTO locationDTO) {
        logger.info("Validating location: {}", locationDTO);
        if (locationDTO.getCity() == null || locationDTO.getCity().trim().isEmpty()) {
            throw new LocationValidationException("City cannot be empty");
        }

        if (locationDTO.getRegion() == null || locationDTO.getRegion().trim().isEmpty()) {
            throw new LocationValidationException("Region cannot be empty");
        }

        // Validate latitude is between -90 and 90
        if (locationDTO.getLatitude() < -90 || locationDTO.getLatitude() > 90) {
            throw new LocationValidationException("Latitude must be between -90 and 90");
        }

        // Validate longitude is between -180 and 180
        if (locationDTO.getLongitude() < -180 || locationDTO.getLongitude() > 180) {
            throw new LocationValidationException("Longitude must be between -180 and 180");
        }
    }
}