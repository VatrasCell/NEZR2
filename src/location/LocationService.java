package location;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import application.Datenbank;

public class LocationService extends Datenbank {
	/**
	 * Gibt alle Standorte zurueck
	 * 
	 * @return Vector String aller Standorte
	 * @author Florian und Elias
	 */
	public static Vector<String> getStandort() {

		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "SELECT * FROM ort";
			ResultSet myRS = mySQL.executeQuery(statement);
			Vector<String> ort = new Vector<String>();
			while (myRS.next()) {
				ort.add(myRS.getString("Ort"));
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
