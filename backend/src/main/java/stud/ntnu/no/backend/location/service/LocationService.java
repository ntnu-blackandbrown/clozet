package stud.ntnu.no.backend.location.service;

import stud.ntnu.no.backend.location.dto.CreateLocationDTO;
import stud.ntnu.no.backend.location.dto.LocationDTO;

import java.util.List;

public interface LocationService {
    List<LocationDTO> getAllLocations();
    LocationDTO getLocation(Long id);
    LocationDTO createLocation(CreateLocationDTO locationDTO);
    LocationDTO updateLocation(Long id, CreateLocationDTO locationDTO);
    void deleteLocation(Long id);
}