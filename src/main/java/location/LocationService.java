package location;

import application.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class LocationService extends Database {
	/**
	 * Gibt alle Standorte zurueck
	 * 
	 * @return ArrayList String aller Standorte
	 * @author Florian und Elias
	 */
	public static ArrayList<String> getStandort() {

		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "SELECT * FROM ort";
			ResultSet myRS = mySQL.executeQuery(statement);
			ArrayList<String> ort = new ArrayList<String>();
			while (myRS.next()) {
				ort.add(unslashUnicode(myRS.getString("Ort")));
			}
			mySQL = null;
			myRS = null;
			myCon.close();
			return ort;
		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return null;
	}
}
