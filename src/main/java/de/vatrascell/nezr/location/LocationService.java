package de.vatrascell.nezr.location;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.application.controller.LocationLogoController;
import de.vatrascell.nezr.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_LOCATION_NAME;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_LOCATION_NAMES;

@Service
@RequiredArgsConstructor
public class LocationService extends Database {

    private final LocationLogoController locationLogoController;

    public List<Location> getLocations() {

        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_LOCATION_NAMES);
            ResultSet myRS = psSql.executeQuery();
            List<Location> locations = new ArrayList<>();
            while (myRS.next()) {
                String locationName = myRS.getString(SQL_COLUMN_LOCATION_NAME);
                locations.add(
                        new Location(locationName,
                                locationLogoController.getLocationLogoPath(locationName),
                                locationLogoController.getLocationsCoordinates(locationName))
                );
            }
            return locations;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
