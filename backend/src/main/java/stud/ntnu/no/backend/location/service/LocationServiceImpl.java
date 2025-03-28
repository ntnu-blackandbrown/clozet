package stud.ntnu.no.backend.location.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.location.exception.LocationNotFoundException;
import stud.ntnu.no.backend.location.exception.LocationValidationException;
import stud.ntnu.no.backend.location.mapper.LocationMapper;
import stud.ntnu.no.backend.location.repository.LocationRepository;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    public List<LocationDTO> getAllLocations() {
        return locationMapper.toDtoList(locationRepository.findAll());
    }

    @Override
    public LocationDTO getLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDTO createLocation(CreateLocationDTO locationDTO) {
        validateLocation(locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        return locationMapper.toDto(location);
    }

    @Override
    @Transactional
    public LocationDTO updateLocation(Long id, CreateLocationDTO locationDTO) {
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
        if (!locationRepository.existsById(id)) {
            throw new LocationNotFoundException(id);
        }
        locationRepository.deleteById(id);
    }
    
    private void validateLocation(CreateLocationDTO locationDTO) {
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