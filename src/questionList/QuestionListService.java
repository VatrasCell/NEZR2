package questionList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import application.Datenbank;
import model.Frage;
import model.Fragebogen;

public class QuestionListService extends Datenbank {
	/**
	 * Gibt alle Ueberschriften des gegebenen Fragebogen zurueck.
	 * 
	 * @param fb
	 *            FrageobgenDialog: der Fragebogen
	 * @return Vector FrageErstellen
	 * @author Eric
	 */
	public static Vector<Frage> getUeberschriften(Fragebogen fb) {
		Vector<Frage> ueberschriften = new Vector<>();
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statment = "SELECT mc1.FrageMC, mc1.idMultipleChoice, Fragebogen.Datum, fb_has_mc.Position, fb_has_mc.Flags, Kategorie, Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE Fragebogen.idFragebogen="
					+ fb.getId() + " AND antwort='#####'";
			ResultSet myRS = mySQL.executeQuery(statment);
			while (myRS.next()) {
				Frage frage = new Frage();
				frage.setFrage(unslashUnicode(myRS.getString("FrageMC")));
				frage.setFrageID(myRS.getInt("idMultipleChoice"));
				frage.setKategorie(unslashUnicode(myRS.getString("Kategorie")));
				frage.setDatum(myRS.getString("Datum"));
				frage.setFlags("");
				frage.setPosition(Integer.parseInt(myRS.getString("Position")));
				frage.setArt("MC");
				frage.setFragebogenID(fb.getId());
				frage.addAntwort_moeglichkeit(myRS.getString("Antwort"));
				ueberschriften.add(frage);
			}
			myCon.close();
		} catch (SQLException e) {
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
		}
		return ueberschriften;
	}
}
