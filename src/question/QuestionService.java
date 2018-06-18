package question;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	/**
	 * Erstellt String mit den angegebenen Flags.
	 * 
	 * @param pflichtfrage
	 *            boolean: ist es eine Pflichtfrage?
	 * @param liste
	 *            boolean: ist es eine Liste?
	 * @param multipleChoice
	 *            boolean: sind mehrere Antworten moeglich?
	 * @param textarea
	 *            boolean: ist es ein Textarea?
	 * @param ja_nein
	 *            boolean: ist es eine Ja- Nein- Frage?
	 * @param isZahl
	 *            boolean: verlangt das Textfeld eine Zahl?
	 * @param zahlArt
	 *            String: <=; ==; >=?
	 * @param anzahlZeichen
	 *            int: Anzahl der Stellen der Zahl?
	 * @param art
	 *            String: ist es eine Bewertungsfrage?
	 * @return String flag
	 * @author Florian
	 */
	public static String getMoeglicheFlags(boolean pflichtfrage, boolean liste, boolean multipleChoice,
			boolean textarea, boolean ja_nein, boolean isZahl, boolean isX, String zahlArt, int anzahlZeichen,
			String art) {
		String flag = "";
		if (pflichtfrage) {
			flag += " + ";
		}
		if (liste) {
			flag += " LIST ";
		}
		if (multipleChoice) {
			flag += " * ";
		}
		if (textarea) {
			flag += " TEXT ";
		}
		if (ja_nein) {
			flag += " JN ";
			if (isX) {
				flag += " X ";
			}
		}

		if (isZahl) {
			if (zahlArt.equals("GrÃ¶ÃŸer gleich Zahl")) {
				flag += " INT>=" + anzahlZeichen + " ";
			}

			if (zahlArt.equals("Kleiner gleich Zahl")) {
				flag += " INT<=" + anzahlZeichen + " ";
			}

			if (zahlArt.equals("Genau wie die Zahl")) {
				flag += " INT==" + anzahlZeichen + " ";
			}
		}

		//

		if (art.equals("bf")) {
			flag += " B ";
		}
		return flag;
	}
	
	/**
	 * Updated die Flags, beim LÃ¶schen einer Antwort, auf die reagiert wird.
	 * Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param frage
	 *            FrageErstellen: die Frage
	 * @return boolean
	 */
	// anneSehrNeu
	public static boolean updateFlags(Frage frage) {
		String statement;
		String flag = "";
		int start = -1;
		int end = -1;

		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			if (frage.getArt().equals("MC")) {
				Statement mySQL = myCon.createStatement();
				statement = "SELECT flags, idMultipleChoice FROM FB_has_mc where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				ResultSet myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
				// reagiert
				while (myRS.next()) {
					flag = myRS.getString("flags");
					Pattern MY_PATTERN = Pattern.compile("[MCFF][0-9]+A[0-9]+");
					Matcher mges = MY_PATTERN.matcher(flag);

					if (mges.find()) {
						start = mges.start() - 1;
						end = mges.end() + 1;

						StringBuilder sb = new StringBuilder(flag);
						StringBuilder afterRemove = sb.delete(start, end);
						flag = afterRemove.toString();

						mySQL = myCon.createStatement();
						statement = "UPDATE fb_has_mc SET flags='" + flag + "' WHERE idMultiplechoice="
								+ myRS.getInt("idMultipleChoice");
						mySQL.execute(statement);
					}
				}

				mySQL = myCon.createStatement();
				statement = "SELECT flags, idFreieFragen FROM FB_has_ff where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
				// reagiert
				while (myRS.next()) {
					flag = myRS.getString("flags");
					Pattern MY_PATTERN = Pattern.compile("[MCFF][0-9]+A[0-9]+");
					Matcher mges = MY_PATTERN.matcher(flag);

					if (mges.find()) {
						start = mges.start() - 1;
						end = mges.end() + 1;

						StringBuilder sb = new StringBuilder(flag);
						StringBuilder afterRemove = sb.delete(start, end);
						flag = afterRemove.toString();

						mySQL = myCon.createStatement();
						statement = "UPDATE fb_has_ff SET flags='" + flag + "' WHERE idFreieFragen="
								+ myRS.getInt("idFreieFragen");
						mySQL.execute(statement);
					}
				}
			} else {
				Statement mySQL = myCon.createStatement();
				statement = "SELECT flags, idFreieFragen FROM FB_has_ff where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				ResultSet myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
				// reagiert
				while (myRS.next()) {
					flag = myRS.getString("flags");
					Pattern MY_PATTERN = Pattern.compile("[MCFF][0-9]+A[0-9]+");
					Matcher mges = MY_PATTERN.matcher(flag);

					if (mges.find()) {
						start = mges.start() - 1;
						end = mges.end() + 1;

						StringBuilder sb = new StringBuilder(flag);
						StringBuilder afterRemove = sb.delete(start, end);
						flag = afterRemove.toString();

						mySQL = myCon.createStatement();
						statement = "UPDATE fb_has_ff SET flags='" + flag + "' WHERE idFreieFragen="
								+ myRS.getInt("idFreieFragen");
						mySQL.execute(statement);
					}
				}

				mySQL = myCon.createStatement();
				statement = "SELECT flags, idmultiplechoice FROM FB_has_mc where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
				// reagiert
				while (myRS.next()) {
					flag = myRS.getString("flags");
					Pattern MY_PATTERN = Pattern.compile("[MCFF][0-9]+A[0-9]+");
					Matcher mges = MY_PATTERN.matcher(flag);

					if (mges.find()) {
						start = mges.start() - 1;
						end = mges.end() + 1;

						StringBuilder sb = new StringBuilder(flag);
						StringBuilder afterRemove = sb.delete(start, end);
						flag = afterRemove.toString();

						mySQL = myCon.createStatement();
						statement = "UPDATE fb_has_mc SET flags='" + flag + "' WHERE idmultiplechoice="
								+ myRS.getInt("idmultiplechoice");
						mySQL.execute(statement);
					}
				}
			}
			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Updatet die Flags. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @param art
	 *            String: Art der Frage
	 * @param id
	 *            int: ID der Frage
	 * @return boolean
	 */
	public static boolean updateFlags(Fragebogen fb, String art, int id) {
		if (art.equals("FF")) {
			try {
				Connection myCon = DriverManager.getConnection(url, user, pwd);
				Statement mySQL = myCon.createStatement();
				String statement = "SELECT Flags FROM Fb_has_FF WHERE idFragebogen=" + fb.getId()
						+ " AND idFreieFragen=" + id;
				ResultSet myRS = mySQL.executeQuery(statement);

				if (myRS.next()) {
					if (myRS.getString("Flags").indexOf("+") >= 0) {
						return true;
					} else {
						String flags = myRS.getString("Flags") + " +";

						mySQL = myCon.createStatement();
						statement = "UPDATE Fb_has_FF SET Flags='" + flags + "'  WHERE idFragebogen="
								+ fb.getId() + " AND idFreieFragen=" + id;
						mySQL.executeUpdate(statement);
						return true;
					}

				} else {
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				//ErrorLog.fehlerBerichtB("ERROR",
				//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
				//		e.getMessage());
				return false;
			}

		} else {
			try {
				Connection myCon = DriverManager.getConnection(url, user, pwd);
				Statement mySQL = myCon.createStatement();
				String statement = "SELECT Flags FROM Fb_has_MC WHERE idFragebogen=" + fb.getId()
						+ " AND idMultipleChoice=" + id;
				ResultSet myRS = mySQL.executeQuery(statement);

				if (myRS.next()) {
					if (myRS.getString("Flags").indexOf("+") >= 0) {
						return true;
					} else {
						String flags = myRS.getString("Flags") + " +";

						mySQL = myCon.createStatement();
						statement = "UPDATE Fb_has_MC SET Flags='" + flags + "'  WHERE idFragebogen="
								+ fb.getId() + " AND idMultipleChoice=" + id;
						mySQL.executeUpdate(statement);
						return true;
					}
				} else {
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				//ErrorLog.fehlerBerichtB("ERROR",
				//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
				//		e.getMessage());
				return false;
			}
		}
	}
	
	/**
	 * Prueft, ob eine Frage Pflichtfrage ist. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @param art
	 *            String: Art der Frage
	 * @param id
	 *            int: ID der Frage
	 * @return boolean
	 */
	public static boolean isPflichtfrage(Fragebogen fb, String art, int id) {
		if (art.equals("FF")) {
			try {
				Connection myCon = DriverManager.getConnection(url, user, pwd);
				Statement mySQL = myCon.createStatement();
				String statement = "SELECT Flags FROM Fb_has_FF WHERE idFragebogen=" + fb.getId()
						+ " AND idFreieFragen=" + id;
				ResultSet myRS = mySQL.executeQuery(statement);

				if (myRS.next()) {
					if (myRS.getString("Flags").indexOf("+") >= 0) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				//rrorLog.fehlerBerichtB("ERROR",
				//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
				//		e.getMessage());
				return false;
			}

		} else {
			try {
				Connection myCon = DriverManager.getConnection(url, user, pwd);
				Statement mySQL = myCon.createStatement();
				String statement = "SELECT Flags FROM Fb_has_MC WHERE idMultipleChoice=" + fb.getId()
						+ " AND idMultipleChoice=" + id;
				ResultSet myRS = mySQL.executeQuery(statement);

				if (myRS.next()) {
					if (myRS.getString("Flags").indexOf("+") >= 0) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				//ErrorLog.fehlerBerichtB("ERROR",
				//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
				//		e.getMessage());
				return false;
			}
		}
	}
}
