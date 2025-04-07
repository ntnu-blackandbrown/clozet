package stud.ntnu.no.backend.location.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import stud.ntnu.no.backend.location.entity.Location;

/**
 * Helper class for providing test entities for Location repository tests.
 */
public class TestHelper {

    /**
     * Creates a basic location entity and persists it.
     * 
     * @param entityManager test entity manager
     * @param name name of the location
     * @return the created and persisted location
     */
    public static Location createLocation(TestEntityManager entityManager, String city) {
        Location location = new Location();
        location.setCity(city);
        location.setRegion("Oslo Region");
        location.setLatitude(59.9139);
        location.setLongitude(10.7522);
        entityManager.persist(location);
        return location;
    }
    
    /**
     * Creates a location with specific coordinates and persists it.
     * 
     * @param entityManager test entity manager
     * @param city name of the city
     * @param region name of the region
     * @param latitude latitude coordinate
     * @param longitude longitude coordinate
     * @return the created and persisted location
     */
    public static Location createLocationWithCoordinates(TestEntityManager entityManager, 
                                                      String city, 
                                                      String region, 
                                                      double latitude, 
                                                      double longitude) {
        Location location = new Location();
        location.setCity(city);
        location.setRegion(region);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        entityManager.persist(location);
        return location;
    }
} 