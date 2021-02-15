package location;

import application.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static application.SqlStatement.SQL_COLUMN_LOCATION_NAME;
import static application.SqlStatement.SQL_GET_LOCATION_NAMES;

public class LocationService extends Database {

    public static List<String> getLocations() {

        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_LOCATION_NAMES);
            ResultSet myRS = psSql.executeQuery();
            List<String> locations = new ArrayList<>();
            while (myRS.next()) {
                locations.add(myRS.getString(SQL_COLUMN_LOCATION_NAME));
            }
            return locations;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
