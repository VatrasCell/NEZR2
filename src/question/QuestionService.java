package question;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import application.Datenbank;
import model.Frage;
import model.Fragebogen;

public class QuestionService extends Datenbank {
	/**
	 * Gibt alle Antworten einer Frage zurueck.
	 * 
	 * @param frage
	 *            FrageErstellen: die Frage
	 * @return Vector String aller Antworten
	 */
	public static Vector<String> getAntworten(Frage frage) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String text = slashUnicode(frage.getFrage()).replaceAll("\\\\", "\\\\\\\\");
			String statement = "SELECT Antwort FROM fragebogen JOIN "
					+ "fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN "
					+ "multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN "
					+ "kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN "
					+ "mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN "
					+ "antworten ON mc_has_a.AntwortNr=antworten.AntwortNr " + "WHERE mc1.FrageMC='" + text + "' "
					+ "UNION  " + "SELECT Antwort FROM fragebogen JOIN "
					+ "fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen JOIN "
					+ "freiefragen ff1 ON fb_has_ff.idFreieFragen=ff1.idFreieFragen JOIN "
					+ "kategorie ON ff1.idKategorie=kategorie.idKategorie JOIN freiefragen "
					+ "JOIN ff_has_a ON ff1.idFreieFragen=ff_has_a.idFreieFragen JOIN "
					+ "antworten ON ff_has_a.AntwortNr=antworten.AntwortNr " + "WHERE ff1.FrageFF='" + text + "'";
			ResultSet myRS = mySQL.executeQuery(statement);
			Vector<String> antwortenVec = new Vector<String>();

			while (myRS.next()) {
				antwortenVec.addElement(unslashUnicode(myRS.getString("Antwort")));
			}
			mySQL = null;
			myRS = null;
			myCon.close();

			return antwortenVec;
		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return null;
	}
	
	/**
	 * Gibt das Maximum an moeglicher Position im Fragebogen zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return int
	 */
	public static int getCountPosition(Fragebogen fb) {
		int positionCount = 0;
		int maxPosMc = 0;
		int maxPosFf = 0;
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "SELECT MAX(fb_has_mc.Position) AS position FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen WHERE fragebogen.idFragebogen="
					+ fb.getId();
			ResultSet myRS = mySQL.executeQuery(statement);
			if (myRS.next()) {
				maxPosMc = myRS.getInt("position");
			}

			myRS = null;
			mySQL = null;
			mySQL = myCon.createStatement();
			statement = "SELECT MAX(fb_has_ff.Position) AS position FROM fragebogen JOIN fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen WHERE fragebogen.idFragebogen="
					+ fb.getId();
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				maxPosFf = myRS.getInt("position");
			}
			if (maxPosFf > maxPosMc) {
				positionCount = maxPosFf;
			} else {
				positionCount = maxPosMc;
			}

			mySQL = null;
			myRS = null;
			myCon.close();
		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return positionCount;
	}
	
	/**
	 * Gibt alle Kategorien zurueck.
	 * 
	 * @return Vector String aller Kategorien
	 */
	public static Vector<String> getKategorie() {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "SELECT * FROM kategorie";
			ResultSet myRS = mySQL.executeQuery(statement);
			Vector<String> kategorien = new Vector<String>();

			while (myRS.next()) {
				kategorien.add(unslashUnicode(myRS.getString("Kategorie")));
			}
			myCon.close();
			return kategorien;
		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return null;
	}
}
