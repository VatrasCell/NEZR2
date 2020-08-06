package start;

import application.Database;
import model.Questionnaire;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StartService extends Database {
	/**
	 * Gibt den aktivierten Fragebogen zurück.
	 * 
	 * @return FragebogenDialog
	 * @author Eric
	 */
	public static Questionnaire getActivFragebogen() {
		Questionnaire questionnaire;
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			ResultSet myRS = mySQL.executeQuery(
					"SELECT idFragebogen, Datum, Name, Ort.ort FROM fragebogen JOIN Ort ON Ort.idort = Fragebogen.idort WHERE aktiviert = TRUE");
			if (myRS.next()) {
				questionnaire = new Questionnaire();
				questionnaire.setName(unslashUnicode(myRS.getString("Name")));
				questionnaire.setDate(myRS.getString("Datum"));
				questionnaire.setId(myRS.getInt("idFragebogen"));
				questionnaire.setOrt(unslashUnicode(myRS.getString("ort")));
				questionnaire.setActive(true);
				myCon.close();
				return questionnaire;
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
