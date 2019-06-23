package question;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.control.Notifications;

import application.Datenbank;
import flag.FlagList;
import flag.Number;
import flag.NumberOperator;
import flag.Symbol;
import flag.SymbolType;
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
	public static FlagList getMoeglicheFlags(FlagList flags, boolean pflichtfrage, boolean liste, boolean multipleChoice,
			boolean textarea, boolean ja_nein, boolean isZahl, boolean isX, String zahlArt, int anzahlZeichen,
			String art) {
		if (pflichtfrage) {
			flags.add(new Symbol(SymbolType.REQUIRED));
		}
		if (liste) {
			flags.add(new Symbol(SymbolType.LIST));
		}
		if (multipleChoice) {
			flags.add(new Symbol(SymbolType.MC));
		}
		if (textarea) {
			flags.add(new Symbol(SymbolType.TEXT));
		}
		if (ja_nein) {
			flags.add(new Symbol(SymbolType.JN));
			if (isX) {
				flags.add(new Symbol(SymbolType.JNExcel));
			}
		}

		if (isZahl) {
			if (zahlArt.equals("Größer gleich Zahl")) {
				flags.add(new Number(NumberOperator.GTE, anzahlZeichen));
			}

			if (zahlArt.equals("Kleiner gleich Zahl")) {
				flags.add(new Number(NumberOperator.LTE, anzahlZeichen));
			}

			if (zahlArt.equals("Genau wie die Zahl")) {
				flags.add(new Number(NumberOperator.EQ, anzahlZeichen));
			}
		}

		//

		if (art.equals("bf")) {
			flags.add(new Symbol(SymbolType.B));
		}
		return flags;
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
	
	/**
	 * PrÃ¼ft, ob eine Frage doppelt vor kommt und gibt dem Duplikat einen
	 * Suffix.
	 * 
	 * @param frage
	 *            String: die Frage
	 * @return String: die Frage ggf. mit Suffix
	 * @author Eric
	 */
	public static String duplicateFrage(String frage) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			PreparedStatement psSql = null;
			String statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(slashUnicode(frage)));
			ResultSet myRS = psSql.executeQuery();

			if (!myRS.next()) {
				psSql = null;
				statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(slashUnicode(frage)));
				myRS = psSql.executeQuery();

				if (!myRS.next()) {
					return frage;
				}
			}
			int zahl;
			Pattern MY_PATTERN = Pattern.compile("#\\[[0-9]+\\]");
			Matcher m = MY_PATTERN.matcher(frage);
			if (m.find()) {
				String string = m.group(0);
				zahl = Integer.parseInt(string.substring(2, string.length() - 1));
				frage = frage.substring(0, m.start());
			} else {
				zahl = 1;
			}

			while (true) {
				String suffix = "#[" + ++zahl + "]";
				myCon = DriverManager.getConnection(url, user, pwd);
				psSql = null;
				statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(slashUnicode(frage + suffix)));
				myRS = psSql.executeQuery();

				if (!myRS.next()) {
					psSql = null;
					statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
					psSql = myCon.prepareStatement(statement);
					psSql.setString(1, slashUnicode(slashUnicode(frage + suffix)));
					myRS = psSql.executeQuery();

					if (!myRS.next()) {
						frage = frage + suffix;
						break;
					}
				}
			}

			return frage;

		} catch (SQLException e) {
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Speichert eine neue Freie Frage. Gibt bei Erfolg TRUE zurÃ¼ck.
	 * 
	 * @param selectedFB
	 *            FragebogenDialog
	 * @param frage
	 *            FrageErstellen
	 * @return boolean
	 * @author Anne
	 */
	public static boolean saveFreieFrage(Fragebogen selectedFB, Frage frage) {

		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			String statement;
			int idKategorie = -1;
			int idFreieFrage = -1;
			int idFragebogen = selectedFB.getId();

			if (frage.getFrageID() == 0) {
				statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
				PreparedStatement psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				ResultSet myRS = psSql.executeQuery();

				if (myRS.next()) {
					idFreieFrage = myRS.getInt("idFreieFragen");
				}
				psSql = null;
				myRS = null;
			} else {
				idFreieFrage = frage.getFrageID();
			}

			Statement mySQL = myCon.createStatement();
			statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(frage.getKategorie()));
			ResultSet myRS = psSql.executeQuery();

			if (myRS.next()) {
				idKategorie = myRS.getInt("idKategorie");
			} else {
				psSql = null;
				mySQL = null;

				mySQL = myCon.createStatement();
				statement = "INSERT INTO Kategorie VALUES(NULL, ?)";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getKategorie()));
				psSql.executeUpdate();

				psSql = null;
				mySQL = null;
				myRS = null;

				mySQL = myCon.createStatement();
				statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getKategorie()));
				myRS = psSql.executeQuery();

				if (myRS.next()) {
					idKategorie = myRS.getInt("idKategorie");
				}
			}
			mySQL = null;
			myRS = null;
			psSql = null;

			mySQL = myCon.createStatement();
			statement = "SELECT idKategorie, idFreieFragen FROM FreieFragen WHERE idFreieFragen=" + idFreieFrage;
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				mySQL = null;
				if (myRS.getInt("idKategorie") != idKategorie) {
					mySQL = myCon.createStatement();
					statement = "UPDATE FreieFragen SET idKategorie=" + idKategorie + " WHERE idFreieFragen="
							+ idFreieFrage;
					mySQL.execute(statement);
					mySQL = null;
				}
				mySQL = myCon.createStatement();
				statement = "UPDATE FreieFragen SET FrageFF=? WHERE idFreieFragen=" + idFreieFrage;
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();

			} else {
				psSql = null;
				mySQL.close();
				mySQL = null;

				mySQL = myCon.createStatement();
				statement = "INSERT INTO FreieFragen VALUES(NULL, ? ," + idKategorie + ")";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();

				mySQL = null;
				myRS = null;
				psSql = null;

				mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				myRS = psSql.executeQuery();

				if (myRS.next()) {
					idFreieFrage = myRS.getInt("idFreieFragen");
				}
			}

			mySQL = null;
			myRS = null;
			psSql = null;

			mySQL = myCon.createStatement();
			statement = "SELECT idRelFBFF FROM FB_HAS_FF WHERE idFragebogen=" + idFragebogen + " AND idFreieFragen="
					+ idFreieFrage;
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				int idRelFBFF = myRS.getInt("idRelFBFF");
				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_FF SET Flags='" + frage.getFlags().createFlagString() + "' WHERE idRelFBFF=" + idRelFBFF;
				mySQL.executeUpdate(statement);

				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_FF SET Position=" + frage.getPosition() + " WHERE idRelFBFF=" + idRelFBFF;
				mySQL.executeUpdate(statement);
			} else {
				mySQL.close();
				mySQL = myCon.createStatement();
				statement = "INSERT INTO Fb_has_FF VALUES (NULL," + idFragebogen + "," + idFreieFrage + ","
						+ frage.getPosition() + ",'" + frage.getFlags().createFlagString() + "')";
				mySQL.executeUpdate(statement);
				mySQL = null;
				myRS = null;
			}

			psSql = null;
			mySQL = null;
			myRS = null;
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
	 * Gibt die ID der Antwort zurÃ¼ck. Ist die Antwort noch nicht vorhanden,
	 * wird sie zuerst in der Datenbank gespeichert.
	 * 
	 * @param antwort
	 *            String
	 * @return int
	 * @author Eric
	 */
	public static int getAntwortID(String antwort) {
		int id = -1;
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			if (antwort == null) {
				antwort = "NULL";
			}
			String statement = "SELECT AntwortNr FROM Antworten WHERE Antwort=?";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(antwort));
			ResultSet myRS = psSql.executeQuery();
			if (myRS.next()) {

				id = myRS.getInt("AntwortNr");
			} else {
				myRS = null;
				statement = "INSERT INTO Antworten VALUES(NULL, ?)";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(antwort));
				psSql.executeUpdate();

				myRS = null;
				psSql = null;
				statement = "SELECT AntwortNr FROM Antworten WHERE Antwort=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(antwort));
				myRS = psSql.executeQuery();
				if (myRS.next()) {
					id = myRS.getInt("AntwortNr");
				} else {
					System.err.println("Missing ID");
				}
			}
		} catch (SQLException e) {
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return id;
	}
	
	/**
	 * Loescht die gegebene Antwort. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param antids
	 *            Vector<Integer>: ids der zu-lÃ¶schendenen Antworten
	 * @param frage
	 *            FrageErstellen: die zugehÃ¶rige Frage
	 * @return boolean
	 */
	// anneSehrNeu
	public static boolean deleteAntworten(Vector<Integer> antids, Frage frage) {
		String statement;

		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);

			for (int i = 0; i < antids.size(); i++) {
				Statement mySQL = myCon.createStatement();
				statement = "DELETE FROM MC_has_A WHERE AntwortNr=" + antids.get(i) + " AND idMultipleChoice="
						+ frage.getFrageID();
				mySQL.execute(statement);
				mySQL = null;
			}

			Statement mySQL = myCon.createStatement();
			statement = "DELETE FROM Antworten WHERE AntwortNr NOT IN (SELECT AntwortNr FROM MC_has_a)"	+ 
			" AND NOT(antwort='ja') AND NOT(antwort='nein') " + 
			"AND NOT(antwort='#####') AND NOT(antwort='0') AND NOT(antwort='1') AND NOT(antwort='2') AND NOT(antwort='3') AND NOT(antwort='4') " +
			"AND NOT(antwort='5') AND NOT(antwort='6') AND NOT(antwort='7') AND NOT(antwort='8') AND NOT(antwort='9') AND NOT(antwort='10')";
			 
			mySQL.execute(statement);
			mySQL = null;

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
	 * Speichert eine neue Multipe Choice Frage. Gibt bei Erfolg TRUE zurÃ¼ck.
	 * 
	 * @param neueFrageMc
	 *            Vector String
	 * @param selectedFB
	 *            FragebogenDialog
	 * @param spinnerValue
	 *            Object
	 * @return boolean
	 * @author Eric
	 */
	public static boolean saveMC(Fragebogen selectedFB, Frage frage, Vector<Integer> antIds) {

		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			int idMultipleChoice = frage.getFrageID();
			int idFragebogen = selectedFB.getId();
			int idKategorie = -1;
			String statement;

			if (frage.getFrageID() == 0) {
				statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
				PreparedStatement psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				ResultSet myRS = psSql.executeQuery();

				if (myRS.next()) {
					idMultipleChoice = myRS.getInt("idMultipleChoice");
				}
				psSql = null;
				myRS = null;
			} else {
				idMultipleChoice = frage.getFrageID();
			}

			// Kategorie
			statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(frage.getKategorie()));
			ResultSet myRS = psSql.executeQuery();

			if (myRS.next()) {
				idKategorie = myRS.getInt("idKategorie");
			} else {
				psSql = null;

				statement = "INSERT INTO Kategorie VALUES(NULL, ?)";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getKategorie()));
				psSql.executeUpdate();

				psSql = null;
				myRS = null;

				statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getKategorie()));
				myRS = psSql.executeQuery();

				if (myRS.next()) {
					idKategorie = myRS.getInt("idKategorie");
				}
			}
			myRS = null;
			psSql = null;

			// Frage
			Statement mySQL = myCon.createStatement();
			statement = "SELECT idMultipleChoice, idKategorie FROM MultipleChoice WHERE idMultipleChoice="
					+ idMultipleChoice;
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				if (myRS.getInt("idKategorie") != idKategorie) {
					mySQL = myCon.createStatement();
					statement = "UPDATE MultipleChoice SET idKategorie=" + idKategorie + " WHERE idMultipleChoice="
							+ idMultipleChoice;
					mySQL.execute(statement);
					mySQL = null;
				}
				statement = "UPDATE MultipleChoice SET FrageMC=? WHERE idMultipleChoice=" + idMultipleChoice;
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();
			} else {
				myRS = null;
				psSql = null;

				statement = "INSERT INTO MultipleChoice VALUES(NULL, ?, " + idKategorie + ")";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();
				psSql = null;

				statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				myRS = psSql.executeQuery();

				if (myRS.next()) {
					idMultipleChoice = myRS.getInt("idMultipleChoice");
				}
			}
			myRS = null;
			mySQL = null;
			psSql = null;

			Vector<Integer> relAlt = new Vector<Integer>();
			Vector<Integer> relNeu = new Vector<Integer>();
			mySQL = myCon.createStatement();
			statement = "SELECT idRelMCA FROM MC_has_A WHERE idMultipleChoice=" + idMultipleChoice;
			myRS = mySQL.executeQuery(statement);

			while (myRS.next()) {
				relAlt.add(myRS.getInt("idRelMCA"));
			}
			myRS = null;
			mySQL = null;

			for (int i = 0; i < antIds.size(); i++) {

				mySQL = myCon.createStatement();
				statement = "SELECT idRelMCA FROM MC_has_A WHERE idMultipleChoice=" + idMultipleChoice
						+ " AND AntwortNr=" + antIds.get(i);
				myRS = mySQL.executeQuery(statement);

				if (myRS.next()) {
					relNeu.add(myRS.getInt("idRelMCA"));
				} else {
					mySQL = null;
					mySQL = myCon.createStatement();
					statement = "INSERT INTO MC_has_A VALUES(NULL, " + idMultipleChoice + ", " + antIds.get(i) + ")";
					mySQL.executeUpdate(statement);
				}
				myRS = null;
				mySQL = null;
			}

			for (int i : relAlt) {
				if (!relNeu.contains(i)) {
					mySQL = null;
					mySQL = myCon.createStatement();
					statement = "DELETE FROM MC_has_A " + "WHERE idRelMCA =" + i;
					mySQL.executeUpdate(statement);
				}
			}

			myRS = null;
			mySQL = null;

			mySQL = myCon.createStatement();
			statement = "SELECT idRelFBMC FROM FB_HAS_MC WHERE idFragebogen=" + idFragebogen + " AND idMultipleChoice="
					+ idMultipleChoice;
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				int idRelFBMC = myRS.getInt("idRelFBMC");
				relNeu.add(idRelFBMC);
				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_MC SET Flags='" + frage.getFlags().createFlagString() + "' WHERE idRelFBMC=" + idRelFBMC;
				mySQL.executeUpdate(statement);

				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_MC SET Position=" + frage.getPosition() + " WHERE idRelFBMC=" + idRelFBMC;
				mySQL.executeUpdate(statement);
			} else {
				mySQL.close();
				mySQL = myCon.createStatement();
				statement = "INSERT INTO Fb_has_MC VALUES (NULL," + idFragebogen + "," + idMultipleChoice + ","
						+ frage.getPosition() + ",'" + frage.getFlags().createFlagString() + "')";
				mySQL.executeUpdate(statement);
				mySQL = null;
				myRS = null;
			}
			myRS = null;
			mySQL = null;
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
	 * Speichert eine neue Bewertungsfrage. Gibt bei Erfolg TRUE zurÃ¼ck.
	 * 
	 * @param selectedFB
	 *            FragebogenDialog
	 * @param spinnerValue
	 *            Object
	 * @param frage
	 *            String
	 * @param kat
	 *            String
	 * @return boolean
	 * @author Anne
	 */
	public static boolean saveBewertungsfrage(Fragebogen selectedFB, Frage frage) {

		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			int idKategorie = -1;
			int idFragebogen = selectedFB.getId();
			int idBewertungsFrage = -1;
			int countAntwort = 0;
			String statement;

			if (frage.getFrageID() == 0) {
				statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
				PreparedStatement psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				ResultSet myRS = psSql.executeQuery();

				if (myRS.next()) {
					idBewertungsFrage = myRS.getInt("idMultipleChoice");
				}
				psSql = null;
				myRS = null;
			} else {
				idBewertungsFrage = frage.getFrageID();
			}

			statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(frage.getKategorie()));
			ResultSet myRS = psSql.executeQuery();

			if (myRS.next()) {
				idKategorie = myRS.getInt("idKategorie");
			} else {
				psSql = null;

				statement = "INSERT INTO Kategorie VALUES(NULL, ?)";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getKategorie()));
				psSql.executeUpdate();

				psSql = null;
				myRS = null;

				statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getKategorie()));
				myRS = psSql.executeQuery();

				if (myRS.next()) {
					idKategorie = myRS.getInt("idKategorie");
				}
			}
			myRS = null;
			psSql = null;

			Statement mySQL = myCon.createStatement();
			statement = "SELECT idMultipleChoice, idKategorie FROM MultipleChoice WHERE idMultipleChoice="
					+ idBewertungsFrage;
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				if (myRS.getInt("idKategorie") != idKategorie) {
					mySQL = myCon.createStatement();
					statement = "UPDATE MultipleChoice SET idKategorie=" + idKategorie + " WHERE idMultipleChoice="
							+ idBewertungsFrage;
					mySQL.execute(statement);
					mySQL = null;
				}
				statement = "UPDATE MultipleChoice SET FrageMC=? WHERE idMultipleChoice=" + idBewertungsFrage;
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();

			} else {
				psSql = null;

				statement = "INSERT INTO MultipleChoice VALUES(NULL, ? ," + idKategorie + ")";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();
			}

			mySQL = null;
			myRS = null;
			psSql = null;

			mySQL = myCon.createStatement();
			statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(frage.getFrage()));
			myRS = psSql.executeQuery();

			if (myRS.next()) {
				idBewertungsFrage = myRS.getInt("idMultipleChoice");
			}
			mySQL = null;
			myRS = null;
			psSql = null;

			mySQL = myCon.createStatement();
			statement = "SELECT idRelFBMC FROM FB_HAS_MC WHERE idFragebogen=" + idFragebogen + " AND idMultipleChoice="
					+ idBewertungsFrage;
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				int idRelFBMC = myRS.getInt("idRelFBMC");
				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_MC SET Flags='" + frage.getFlags().createFlagString() + "' WHERE idRelFBMC=" + idRelFBMC;
				mySQL.executeUpdate(statement);

				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_MC SET Position=" + frage.getPosition() + " WHERE idRelFBMC=" + idRelFBMC;
				mySQL.executeUpdate(statement);
			} else {
				mySQL.close();
				mySQL = myCon.createStatement();
				statement = "INSERT INTO Fb_has_MC VALUES (NULL," + idFragebogen + "," + idBewertungsFrage + ","
						+ frage.getPosition() + ",'" + frage.getFlags().createFlagString() + "')";
				mySQL.executeUpdate(statement);
				mySQL = null;
				myRS = null;
			}

			mySQL = myCon.createStatement();
			statement = "SELECT idRelMCA FROM MC_HAS_A WHERE idMultipleChoice = " + idBewertungsFrage;
			myRS = mySQL.executeQuery(statement);

			while (myRS.next()) {
				countAntwort++;
			}

			mySQL = null;
			myRS = null;

			if (countAntwort == 11) {

			} else {
				for (int i = 0; i <= 10; i++) {
					int antwortNr = getAntwortID(String.valueOf(i));
					mySQL = myCon.createStatement();
					statement = "INSERT INTO MC_has_a VALUES (NULL, " + idBewertungsFrage + "," + antwortNr + ")";
					mySQL.executeUpdate(statement);
					mySQL = null;
				}
			}

			countAntwort = 0;

			mySQL = null;
			myRS = null;
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
	 * Fuegt eine neue Kategorie der Datenbank hinzu. Gibt bei Erfolg TRUE
	 * zurueck.
	 * 
	 * @param kat
	 *            String: die Kategorie
	 * @return boolean
	 */
	public static boolean createKategorie(String kat) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "SELECT Kategorie FROM kategorie WHERE Kategorie='" + kat + "'";
			ResultSet myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				Notifications.create().title("Kategorie anlegen").text("Die Kategorie existiert bereits!").showError();
			} else {
				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "INSERT INTO kategorie VALUES(NULL, '" + kat + "')";
				mySQL.execute(statement);
			}
			mySQL = null;
			myRS = null;
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
