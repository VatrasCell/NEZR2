package admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import application.Datenbank;
import model.Fragebogen;

public class AdminService extends Datenbank {
	
	/**
	 * Gibt alle Frageboegen eines Standortes zurueck
	 * 
	 * @param standort
	 *            String: der Standort
	 * @return Vector FragebogenDialog aller Frageboegen des Standortes
	 * @author Eric
	 */
	public static Vector<Fragebogen> getFragebogen(String standort) {
		try {
			int idstandort = -1;
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			PreparedStatement psSql = null;
			mySQL = myCon.createStatement();
			String statement = "SELECT idOrt FROM ort WHERE Ort=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(standort));
			ResultSet myRS = psSql.executeQuery();
			Vector<Fragebogen> fragebogen = new Vector<>();

			while (myRS.next()) {
				idstandort = myRS.getInt("idOrt");
			}

			mySQL = null;
			myRS = null;

			mySQL = myCon.createStatement();
			statement = "SELECT * FROM Fragebogen WHERE idOrt='" + idstandort + "'";
			myRS = mySQL.executeQuery(statement);
			System.out.println(idstandort);
			System.out.println(myRS.toString());
			while (myRS.next()) {
				Fragebogen fb = new Fragebogen();
				fb.setName(unslashUnicode(myRS.getString("name")));
				fb.setDate(myRS.getString("datum"));
				fb.setActiv(myRS.getBoolean("aktiviert")); // anneNeu
				fb.setId(myRS.getInt("idFragebogen")); // anneNeuFlorian
				fb.setFinal(myRS.getBoolean("final"));
				fragebogen.add(fb);
			}
			myCon.close();
			return fragebogen;
		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return null;
	}
	
	/**
	 * Setzt den gegebenen Fragebogen auf aktiv und deaktiviert alle anderen
	 * Frageboegen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Anne
	 */
	public static boolean updateFragebogen(Fragebogen fb) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET aktiviert=TRUE WHERE idFragebogen=" + fb.getId();
			mySQL.execute(statement);

			mySQL = null;
			mySQL = myCon.createStatement();
			statement = "UPDATE fragebogen SET aktiviert=FALSE WHERE NOT idFragebogen=" + fb.getId();
			mySQL.execute(statement);

			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}

	/**
	 * Deaktiviert den gegebenen Fragebogen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 */
	public static boolean disableFragebogen(Fragebogen fb) {
		try {
			// anneSuperNeu
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET aktiviert=FALSE WHERE idFragebogen=" + fb.getId();
			mySQL.execute(statement);

			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}
}
