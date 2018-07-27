package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;

import org.h2.tools.RunScript;

// import export.ExcelCell;
// import fragebogen.*;

/**
 * @author Alle
 * @version 0.5.5
 *          <p>
 *          Klasse fuer alle Datenbankzugriffe.
 */
public class Datenbank {
	
	private static Connection myCon = null; // Verbindung zur DB
	private static Statement mySQL = null; // Zum Statement erstellen
	private static PreparedStatement psSql = null; // Zum PreparedStatement
													// erstellen
	private static ResultSet myRS = null; // Ergebnisse der Abfrage

	protected static String db = "nezr_v5"; // Datenbankname
	protected static String url = ""; // URL der Datenbank useSSL=false
	protected static String user = ""; // Login-ID
	protected static String pwd = ""; // Passwort

	private static int fehler = 0;
	private static int idstandort = 0;

	/**
	 * Gibt die flags fuer das "reagiert auf" zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @param index
	 *            int: Position im Vector
	 * @param positionVec
	 *            int: Postition der Antwort im Vector
	 * @param frageart
	 *            String
	 * @return String flag
	 * @author Florian
	 *//*
	public static String getFlags(FragebogenDialog fb, int index, int positionVec, String frageart) {
		Vector<FrageErstellen> fragen = getFragen(fb);
		String flag = "";
		if (frageart == "FF") {
			// flag = flag + ""; // flag + art_der_frage + IdderReagierenden
			// Frage + A + Position im Vektor
			String frageArt = String.valueOf(fragen.get(index).getArt());
			int reactFrageID = fragen.get(index).getFrageID();
			// flag += combiFlag + frageArt + reactFrageID + "A" + positionVec;
			flag += frageArt + reactFrageID + "A" + positionVec;
		} else if (frageart == "MC") {
			String frageArt = String.valueOf(fragen.get(index).getArt());
			int reactFrageID = fragen.get(index).getFrageID();
			flag += frageArt + reactFrageID + "A" + positionVec;
		} else if (frageart == "BF") {
			// Bewertungsantworten hinzufÃ¼gen
			String frageArt = String.valueOf(fragen.get(index).getArt());
			int reactFrageID = fragen.get(index).getFrageID();
			flag += frageArt + reactFrageID + "A" + positionVec;
		}
		return flag;

	}

	*//**
	 * Erzeugt aus dem Vector FrageErstellen eine DefaultListModel der Fragen.
	 * 
	 * @return DefaultListModel String
	 * @author Anne und Florian
	 *//*
	public static DefaultListModel<String> getFragen_react(FragebogenDialog fb, FrageErstellen fr) {
		Vector<FrageErstellen> fragen = getFragen(fb);
		DefaultListModel<String> listFragen = new DefaultListModel<>();

		String frage = "";

		for (int i = 0; i < fragen.size(); i++) {
			frage = fragen.get(i).getFrage();
			if (fragen.get(i).getFrageID() != fr.getFrageID()) {
				listFragen.addElement(frage);
			}
		}
		return listFragen;
	}

	*//**
	 * Erzeugt aus dem Vector FrageErstellen eine DefaultListModel der Fragen.
	 * 
	 * @return DefaultListModel String
	 * @author Anne und Florian
	 *//*
	public static DefaultListModel<String> getFragen_react(FragebogenDialog fb) {
		Vector<FrageErstellen> fragen = getFragen(fb);
		DefaultListModel<String> listFragen = new DefaultListModel<>();

		String frage = "";

		for (int i = 0; i < fragen.size(); i++) {
			frage = fragen.get(i).getFrage();
			listFragen.addElement(frage);
		}
		return listFragen;
	}

	*//**
	 * Erzeugt ein DefaultListModel String aus den Antworten der Frage mit dem
	 * Index.
	 * 
	 * @param index
	 *            int
	 * @return DefaultListModel String
	 * @author Anne und Florian
	 *//*
	public static DefaultListModel<String> getAntworten_react(FragebogenDialog fb, int index) {
		Vector<FrageErstellen> fragen = getFragen(fb);
		DefaultListModel<String> listAntworten = new DefaultListModel<>();

		for (int i = 0; i < fragen.get(index).getAntwort_moeglichkeit().size(); i++) {
			if (fragen.get(index).getAntwort_moeglichkeit().get(i).equals("")) {
				if (fragen.get(index).getFlags().indexOf("TEXT") >= 0) {
					listAntworten.addElement("Textarea");
				} else {
					listAntworten.addElement("Textfeld");
				}
			} else {
				listAntworten.addElement(fragen.get(index).getAntwort_moeglichkeit().get(i));
			}
		}
		return listAntworten;
	}

	*//**
	 * Gibt die Anzahl an gespeicherten Befragungen zurÃ¼ck.
	 * 
	 * @return int
	 * @author Eric
	 *//*
	public static int getAnzahlBefragung() {
		int re = -1;
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			myRS = mySQL.executeQuery("SELECT COUNT(idBefragung) FROM Befragung");
			if (myRS.next()) {
				re = myRS.getInt("COUNT(idBefragung)");
			}
			myCon.close();
		} catch (SQLException e) {
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return re;
	}

	*//**
	 * Gibt die Fragebogen ID des gegebenen Fragebogens zurueck
	 * 
	 * @param selectedFB
	 *            FragebogenDialog: der Fragebogen
	 * @return idFragebogen int
	 * @author Anne und Florian
	 *//*
	public static int getFragebogenId(FragebogenDialog selectedFB) {
		int idFB = -1;
		try {
			myCon = DriverManager.getConnection(url, user, pwd);

			mySQL = myCon.createStatement();
			String statement = "SELECT idFragebogen FROM Fragebogen WHERE Datum='" + selectedFB.getFragebogenDatum()
					+ "'AND Name='" + selectedFB.getFragebogenName() + "' AND idOrt=" + idstandort;
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				idFB = myRS.getInt("idFragebogen");
			}

			mySQL = null;
			myRS = null;
			myCon.close();
			return idFB;
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return idFB;
	}

	*//**
	 * Fuegt einen neuen Fragebogen in die Datenbank ein. Gibt bei Erfolg TRUE
	 * zurueck.
	 * 
	 * @param fbName
	 *            String: Name des Fragebogens
	 * @return boolean
	 * @author Anne
	 *//*
	public static boolean setFragebogen(String fbName) {
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "SELECT Name FROM fragebogen WHERE Name='" + fbName + "'";
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {

			} else {
				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "INSERT INTO fragebogen VALUE(NULL, '" + Main.getcurDate() + "', '" + fbName + "', FALSE, "
						+ idstandort + ", FALSE)";
				mySQL.execute(statement);
			}
			mySQL = null;
			myRS = null;
			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}

	*//**
	 * Prueft, ob ein Fragebogen "fehlerfrei" fuer die Befragung ist. Gibt bei
	 * Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 *//*
	public static boolean isFbValid(FragebogenDialog fb) {
		try {
			Vector<FrageErstellen> fragen = getFragen(fb);
			if (fragen.size() == 0) {
				return false;
			}
			int vor = 0;
			for (FrageErstellen frage : fragen) {
				if (frage.getPosition() > (vor + 1)) {
					return false;
				} else {
					vor = frage.getPosition();
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			return false;
		}
	}

	*//**
	 * Befuellt die Frage mit den fehlenden Parametern.
	 * 
	 * @param frage
	 *            FrageErstellen: die Frage
	 * @return FrageErstellen
	 * @author Eric
	 * 
	 *//*
	public static FrageErstellen getSelectedFrage(FrageErstellen frage, FragebogenDialog fb) {
		FrageErstellen selectedFrage = new FrageErstellen();
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			if (frage.getFrage().contains("\\")) {
				frage.setFrage(frage.getFrage().replaceAll("\\\\", "\\\\\\\\"));
			}
			String statement = "SELECT mc.idMultipleChoice AS id, mc.Fragemc AS frage, kategorie, fb_has_mc.Flags AS flags, fb_has_mc.position AS position, antwort FROM "
					+ "fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN MultipleChoice mc ON fb_has_mc.idMultipleChoice=mc.idMultipleChoice JOIN "
					+ "kategorie ON mc.idKategorie=kategorie.idKategorie JOIN mc_has_a ON mc.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE "
					+ "mc.Fragemc='" + frage.getFrage() + "' AND Fragebogen.idFragebogen=" + fb.getFragebogenId();
			myRS = mySQL.executeQuery(statement);

			// anneSehrNeu
			if (myRS.next()) {
				selectedFrage.setArt("MC");
				selectedFrage.setFrage(unslashUnicode(myRS.getString("frage")));
				selectedFrage.setKategorie(unslashUnicode(myRS.getString("kategorie")));
				selectedFrage.setPosition(myRS.getInt("position"));
				selectedFrage.setFrageID(myRS.getInt("id"));
				selectedFrage.setFlags(myRS.getString("flags"));
				selectedFrage.setAntwort_moeglichkeit(myRS.getString("antwort"));
			} else {
				myRS = null;
				mySQL = null;
				mySQL = myCon.createStatement();
				statement = "SELECT ff.idFreieFragen AS id, ff.FrageFF AS frage, kategorie, fb_has_ff.Flags AS flags, "
						+ "fb_has_ff.position AS position FROM fragebogen JOIN "
						+ "fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen JOIN "
						+ "freiefragen ff ON fb_has_ff.idFreieFragen=ff.idFreieFragen JOIN "
						+ "kategorie ON ff.idKategorie=kategorie.idKategorie " + "WHERE ff.FrageFF='"
						+ slashUnicode(frage.getFrage()) + "'";
				myRS = mySQL.executeQuery(statement);

				if (myRS.next()) {
					selectedFrage.setArt("FF"); // anneSehrNeu
					selectedFrage.setFrage(unslashUnicode(myRS.getString("frage")));
					selectedFrage.setKategorie(unslashUnicode(myRS.getString("kategorie")));
					selectedFrage.setPosition(myRS.getInt("position"));
					selectedFrage.setFrageID(myRS.getInt("id"));
					selectedFrage.setFlags(myRS.getString("flags"));
				}
			}

			
			 * if(myRS.next()) { String flags = myRS.getString("flags");
			 * if(flags.contains("B")) {
			 * selectedFrage.setArt("Bewertungsfrage"); } else {
			 * selectedFrage.setArt("Multiple Choice"); }
			 * selectedFrage.setFrage(unslashUnicode(myRS.getString("frage")));
			 * selectedFrage.setKategorie(unslashUnicode(myRS.getString(
			 * "kategorie")));
			 * selectedFrage.setPosition(myRS.getInt("position"));
			 * selectedFrage.setFrageID(myRS.getInt("id"));
			 * selectedFrage.setFlags(flags);
			 * selectedFrage.setAntwort_moeglichkeit(myRS.getString("antwort"));
			 * } else { myRS = null; mySQL = null;
			 * mySQL=myCon.createStatement(); statement
			 * ="SELECT ff.idFreieFragen AS id, ff.FrageFF AS frage, kategorie, fb_has_ff.Flags AS flags, "
			 * + "fb_has_ff.position AS position FROM fragebogen JOIN " +
			 * "fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen JOIN "
			 * +
			 * "freiefragen ff ON fb_has_ff.idFreieFragen=ff.idFreieFragen JOIN "
			 * + "kategorie ON ff.idKategorie=kategorie.idKategorie " +
			 * "WHERE ff.FrageFF='" + slashUnicode(frage.getFrage()) + "'";
			 * myRS=mySQL.executeQuery(statement);
			 * 
			 * if (myRS.next()) { selectedFrage.setArt("Freie Frage");
			 * selectedFrage.setFrage(unslashUnicode(myRS.getString("frage")));
			 * selectedFrage.setKategorie(unslashUnicode(myRS.getString(
			 * "kategorie")));
			 * selectedFrage.setPosition(myRS.getInt("position"));
			 * selectedFrage.setFrageID(myRS.getInt("id"));
			 * selectedFrage.setFlags(myRS.getString("flags")); } }
			 
			mySQL = null;
			myRS = null;
			myCon.close();

			return selectedFrage;
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());

		}
		return null;
	}

	*//**
	 * Fuegt eine neue Kategorie der Datenbank hinzu. Gibt bei Erfolg TRUE
	 * zurueck.
	 * 
	 * @param kat
	 *            String: die Kategorie
	 * @return boolean
	 *//*
	public static boolean setKategorie(String kat) {
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "SELECT Kategorie FROM kategorie WHERE Kategorie='" + kat + "'";
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {

			} else {
				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "INSERT INTO kategorie VALUE(NULL, '" + kat + "')";
				mySQL.execute(statement);
			}
			mySQL = null;
			myRS = null;
			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}

	*//**
	 * Prueft, ob eine Frage Pflichtfrage ist. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @param art
	 *            String: Art der Frage
	 * @param id
	 *            int: ID der Frage
	 * @return boolean
	 *//*
	public static boolean isPflichtfrage(FragebogenDialog fb, String art, int id) {
		if (art.equals("FF")) {
			try {
				myCon = DriverManager.getConnection(url, user, pwd);
				mySQL = myCon.createStatement();
				String statement = "SELECT Flags FROM Fb_has_FF WHERE idFragebogen=" + fb.getFragebogenId()
						+ " AND idFreieFragen=" + id;
				myRS = mySQL.executeQuery(statement);

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
				ErrorLog.fehlerBerichtB("ERROR",
						Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
						e.getMessage());
				return false;
			}

		} else {
			try {
				myCon = DriverManager.getConnection(url, user, pwd);
				mySQL = myCon.createStatement();
				String statement = "SELECT Flags FROM Fb_has_MC WHERE idMultipleChoice=" + fb.getFragebogenId()
						+ " AND idMultipleChoice=" + id;
				myRS = mySQL.executeQuery(statement);

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
				ErrorLog.fehlerBerichtB("ERROR",
						Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
						e.getMessage());
				return false;
			}
		}
	}

	// Maximum an mÃ¶glicher Position im Fragebogen herausfinden; fÃ¼r den Spinner
	// beim Fragen Erstellen/bearbeiten
	*//**
	 * ka
	 * 
	 * @param date
	 * @param typ
	 * @return
	 *//*
	private static String date_de_sql(String date, String typ) {
		// type ist die zu konfiguerierende masse (sql oder de)
		String ret;
		String trennung = null;
		String[] parts = null;

		switch (typ) {
		case "sql":
			parts = date.split(".");
			trennung = "-";
			break;
		case "de":
			parts = date.split("-");
			trennung = ".";
			break;
		}
		ret = parts[2] + trennung + parts[1] + trennung + parts[0];
		return ret;
	}

	*//**
	 * ka
	 * 
	 * @param art
	 * @param name
	 * @return
	 * @throws IOException
	 */
	protected static String exp_cmd(int art, String name) throws IOException {
		String ret = "false";

		try {
			File theDir = new File("exportSQL");

			if (!theDir.exists()) {
				try {
					theDir.mkdir();
				} catch (SecurityException se) {
				}
			}

			String statement = "";
			switch (art) {
			case 0:
				statement = "cmd /c .\\mysqldump -u " + user + " -p" + pwd + " --no-data " + db + " > exportSQL\\"
						+ name;
				break;
			case 1:
				statement = "cmd /c .\\mysqldump -u " + user + " -p" + pwd + " --no-create-info " + db
						+ " > exportSQL\\" + name;
				break;
			case 2:
				statement = "cmd /c .\\mysqldump -u " + user + " -p" + pwd + "" + db
						+ " > exportSQL\\" + name;
				break;
			}

			Runtime.getRuntime().exec(statement);

			ret = "true";
		} catch (Exception e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return ret;
	}

	/**
	 * ka
	 * 
	 * @return
	 *//*
	public static Vector<String> exp_formular_namen() {

		Vector<String> ret = new Vector<String>();
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statment = "SELECT Name, Datum FROM Fragebogen";
			myRS = mySQL.executeQuery(statment);

			while (myRS.next()) {
				String name = myRS.getString("Name");
				String datum = date_de_sql(myRS.getString("Datum"), "de");
				String ausgabe = name + " | " + datum;
				ret.add(ausgabe);
			}
			myRS = null;
			mySQL = null;
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return ret;
	}

	*//**
	 * ka
	 * 
	 * @param art
	 * @param name
	 * @return
	 *//*
	public static String exp_sql(int art, String name) {
		String ret = "false";

		try {
			// erstellt datei
			File theDir = new File("exportSQL");

			if (!theDir.exists()) {
				try {
					theDir.mkdir();
				} catch (SecurityException se) {
				}
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("exportSQL\\" + name)));

			if (art == 3) {
				// Formular Exportieren
				bw.write("Formular Exportieren");
			} else if (art == 4) {
				// Umfrage ergebnis exportieren
				bw.write("Umfrage ergebnis exportieren");
			} else {
				bw.write("ERROR");
			}

			// Spreibt in datei

			bw.write("\n" + art);

			bw.close();
			ret = "true";
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return ret;
	}

	----------------------------------------------------------------------------------------

	*//**
	 * Erstellt einen Vector aus Excel Zellen anhand des FrageErstellen
	 * Objektes.
	 * 
	 * @param frage
	 *            FrageErstellen
	 * @param von
	 *            String: Datum
	 * @param bis
	 *            Srring: Datum
	 * @return Vector
	 *//*
	public static Vector<ExcelCell> getAntwortenPosition(FrageErstellen frage, String von, String bis) {
		Vector<ExcelCell> re = new Vector<ExcelCell>();
		if (((frage.getArt().equals("MC")) && (frage.getFlags().indexOf("B") >= 0))
				|| (frage.getFlags().indexOf("LIST") >= 0) || (frage.getFlags().indexOf("JN") >= 0)) {
			try {
				myCon = DriverManager.getConnection(url, user, pwd);
				mySQL = myCon.createStatement();
				myRS = mySQL.executeQuery(
						"SELECT b2.idBefragung, antwort FROM Antworten a JOIN B_has_MC b ON a.AntwortNr = b.AntwortNr JOIN Befragung b2 ON b.idBefragung = b2.idBefragung WHERE idFragebogen="
								+ frage.getFragebogenID() + " AND idMultipleChoice=" + frage.getFrageID()
								+ " AND (b2.Datum BETWEEN '" + von + "' AND '" + bis + "')");

				int old = -1;
				Vector<String> strings = new Vector<String>();
				while (myRS.next()) {
					if (old != myRS.getInt("idBefragung")) {
						strings = new Vector<String>();
						strings.addElement(unslashUnicode(myRS.getString("antwort")));
						re.addElement(new ExcelCell(myRS.getInt("idBefragung"), strings));
						old = myRS.getInt("idBefragung");
					} else {
						strings.addElement(unslashUnicode(myRS.getString("antwort")));
					}
				}
				myCon.close();
			} catch (SQLException e) {
				ErrorLog.fehlerBerichtB("ERROR",
						Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
						e.getMessage());
			}
		} else if (frage.getArt().equals("FF")) {
			try {
				myCon = DriverManager.getConnection(url, user, pwd);
				mySQL = myCon.createStatement();
				myRS = mySQL.executeQuery(
						"SELECT b2.idBefragung, antwort FROM Antworten a JOIN B_has_FF b ON a.AntwortNr = b.AntwortNr JOIN Befragung b2 ON b.idBefragung = b2.idBefragung WHERE idFragebogen="
								+ frage.getFragebogenID() + " AND idFreieFragen=" + frage.getFrageID()
								+ " AND (b2.Datum BETWEEN '" + von + "' AND '" + bis + "')");

				int old = -1;
				Vector<String> strings = new Vector<String>();
				while (myRS.next()) {
					if (old != myRS.getInt("idBefragung")) {
						strings = new Vector<String>();
						strings.addElement(unslashUnicode(myRS.getString("antwort")));
						re.addElement(new ExcelCell(myRS.getInt("idBefragung"), strings));
						old = myRS.getInt("idBefragung");
					} else {
						strings.addElement(unslashUnicode(myRS.getString("antwort")));
					}
				}
				myCon.close();
			} catch (SQLException e) {
				ErrorLog.fehlerBerichtB("ERROR",
						Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
						e.getMessage());
			}
		}
		return re;
	}

	*//**
	 * Erstellt einen Vector aus Excel Zellen anhand des FrageErstellen Objektes
	 * und der Antwort.
	 * 
	 * @param frage
	 *            FrageErstellen
	 * @param antwort
	 *            String
	 * @param von
	 *            String: Datum
	 * @param bis
	 *            Srring: Datum
	 * @return Vector
	 *//*
	public static Vector<ExcelCell> getAntwortenPosition(FrageErstellen frage, String antwort, String von, String bis) {
		Vector<ExcelCell> re = new Vector<ExcelCell>();
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			myRS = null;
			mySQL = null;
			psSql = null;
			mySQL = myCon.createStatement();
			String statement = "SELECT b2.idBefragung FROM Antworten a JOIN B_has_MC b ON a.AntwortNr = b.AntwortNr JOIN Befragung b2 ON b.idBefragung = b2.idBefragung WHERE idFragebogen=? AND idMultipleChoice=? AND antwort=? AND (b2.Datum BETWEEN ? AND ?)";
			psSql = myCon.prepareStatement(statement);
			psSql.setInt(1, frage.getFragebogenID());
			psSql.setInt(2, frage.getFrageID());
			psSql.setString(3, slashUnicode(antwort));
			psSql.setString(4, von);
			psSql.setString(5, bis);
			myRS = psSql.executeQuery();

			int old = -1;
			Vector<String> strings = new Vector<String>();
			while (myRS.next()) {
				if (old != myRS.getInt("idBefragung")) {
					strings = new Vector<String>();
					strings.addElement("1");
					re.addElement(new ExcelCell(myRS.getInt("idBefragung"), strings));
					old = myRS.getInt("idBefragung");
				} else {
					strings.addElement("1");
				}
			}
			myCon.close();
		} catch (SQLException e) {
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return re;
	}

	*//**
	 * Wandelt Umlaute in einem String zu Unicode um.
	 * 
	 * @param unSlashed
	 *            String: mit Umlauten
	 * @return String mit Unicode
	 */
	public static String slashUnicode(String unSlashed) {
		if (unSlashed.indexOf("Ä") >= 0) {
			unSlashed = unSlashed.replaceAll("Ä", "\\\\\\\\u00c4");
		}
		if (unSlashed.indexOf("ä") >= 0) {
			unSlashed = unSlashed.replaceAll("ä", "\\\\\\\\u00e4");
		}
		if (unSlashed.indexOf("Ö") >= 0) {
			unSlashed = unSlashed.replaceAll("Ö", "\\\\\\\\u00d6");
		}
		if (unSlashed.indexOf("ö") >= 0) {
			unSlashed = unSlashed.replaceAll("ö", "\\\\\\\\u00f6");
		}
		if (unSlashed.indexOf("Ü") >= 0) {
			unSlashed = unSlashed.replaceAll("Ü", "\\\\\\\\u00dc");
		}
		if (unSlashed.indexOf("ü") >= 0) {
			unSlashed = unSlashed.replaceAll("ü", "\\\\\\\\u00fc");
		}
		if (unSlashed.indexOf("ß") >= 0) {
			unSlashed = unSlashed.replaceAll("ß", "\\\\\\\\u00df");
		}
		if (unSlashed.indexOf("ẞ") >= 0) {
			unSlashed = unSlashed.replaceAll("ẞ", "\\\\\\\\x1e9e");
		}
		return unSlashed;

	}

	/**
	 * Wandelt Unicode Zeichen in einem String zu Umlauten um.
	 * 
	 * @param slashed
	 *            String: mit Unicode
	 * @return String mit Umlauten
	 */
	public static String unslashUnicode(String slashed) {

		ArrayList<String> pieces = new ArrayList<String>();

		while (true) {// while there is /uXXXX in the string

			if (slashed.contains("\\u")) {

				pieces.add(slashed.substring(0, slashed.indexOf("\\u")));// add
																			// the
																			// bit
																			// before
																			// the
																			// /uXXXX

				char c = (char) Integer
						.parseInt(slashed.substring(slashed.indexOf("\\u") + 2, slashed.indexOf("\\u") + 6), 16);

				slashed = slashed.substring(slashed.indexOf("\\u") + 6, slashed.length());

				pieces.add(c + "");// add the unicode
			} else {
				break;
			}
		}
		String temp = "";

		for (String s : pieces) {
			temp = temp + s;// put humpty dumpty back together again
		}
		slashed = temp + slashed;

		return slashed.replaceAll("\\\\", "");
	}
}
