package de.vatrascell.nezr.location;

import de.vatrascell.nezr.application.controller.LocationLogoController;
import de.vatrascell.nezr.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationLogoController locationLogoController;
    private final LocationRepository locationRepository;

    public List<Location> getLocations() {

        return locationRepository.findAll()
                .stream()
                .map(location -> createLocation(location.getName()))
                .toList();

    }

    private Location createLocation(String locationName) {
        Location location = new Location(locationName);
        location.setLogoPath(locationLogoController.getLocationLogoPath(locationName));
        location.setCoordinates(locationLogoController.getLocationsCoordinates(locationName));
        return location;
    }
}
