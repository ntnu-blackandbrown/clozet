package stud.ntnu.no.backend.Location.Service;

import stud.ntnu.no.backend.Location.DTOs.CreateLocationDTO;
import stud.ntnu.no.backend.Location.DTOs.LocationDTO;

import java.util.List;

public interface LocationService {
    List<LocationDTO> getAllLocations();
    LocationDTO getLocation(Long id);
    LocationDTO createLocation(CreateLocationDTO locationDTO);
    LocationDTO updateLocation(Long id, CreateLocationDTO locationDTO);
    void deleteLocation(Long id);
}