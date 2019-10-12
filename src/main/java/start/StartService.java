package start;

import application.Datenbank;
import model.Fragebogen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StartService extends Datenbank {
	/**
	 * Gibt den aktivierten Fragebogen zurück.
	 * 
	 * @return FragebogenDialog
	 * @author Eric
	 */
	public static Fragebogen getActivFragebogen() {
		Fragebogen fragebogen;
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			ResultSet myRS = mySQL.executeQuery(
					"SELECT idFragebogen, Datum, Name, Ort.ort FROM fragebogen JOIN Ort ON Ort.idort = Fragebogen.idort WHERE aktiviert = TRUE");
			if (myRS.next()) {
				fragebogen = new Fragebogen();
				fragebogen.setName(unslashUnicode(myRS.getString("Name")));
				fragebogen.setDate(myRS.getString("Datum"));
				fragebogen.setId(myRS.getInt("idFragebogen"));
				fragebogen.setOrt(unslashUnicode(myRS.getString("ort")));
				fragebogen.setActiv(true);
				myCon.close();
				return fragebogen;
			} else {
				myCon.close();
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return null;
	}
}
