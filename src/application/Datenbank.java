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
	 * Prüft, ob eine Frage doppelt vor kommt und gibt dem Duplikat einen
	 * Suffix.
	 * 
	 * @param frage
	 *            String: die Frage
	 * @return String: die Frage ggf. mit Suffix
	 * @author Eric
	 *//*
	public static String duplicateFrage(String frage) {
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			psSql = null;
			mySQL = myCon.createStatement();
			String statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(slashUnicode(frage)));
			myRS = psSql.executeQuery();

			if (!myRS.next()) {
				psSql = null;
				mySQL = myCon.createStatement();
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
				mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(slashUnicode(frage + suffix)));
				myRS = psSql.executeQuery();

				if (!myRS.next()) {
					psSql = null;
					mySQL = myCon.createStatement();
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
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	*//**
	 * Speichert eine neue Freie Frage. Gibt bei Erfolg TRUE zurück.
	 * 
	 * @param selectedFB
	 *            FragebogenDialog
	 * @param frage
	 *            FrageErstellen
	 * @return boolean
	 * @author Anne
	 *//*
	public static boolean saveFreieFrage(FragebogenDialog selectedFB, FrageErstellen frage) {

		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			String statement;
			int idKategorie = -1;
			int idFreieFrage = -1;
			int idFragebogen = selectedFB.getFragebogenId();

			if (frage.getFrageID() == 0) {
				mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				myRS = psSql.executeQuery();

				if (myRS.next()) {
					idFreieFrage = myRS.getInt("idFreieFragen");
				}
				psSql = null;
				mySQL = null;
				myRS = null;
			} else {
				idFreieFrage = frage.getFrageID();
			}

			mySQL = myCon.createStatement();
			statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(frage.getKategorie()));
			myRS = psSql.executeQuery();

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
				mySQL = null;

				mySQL = myCon.createStatement();
				statement = "INSERT INTO FreieFragen VALUE(NULL, ? ," + idKategorie + ")";
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
				statement = "UPDATE FB_HAS_FF SET Flags='" + frage.getFlags() + "' WHERE idRelFBFF=" + idRelFBFF;
				mySQL.executeUpdate(statement);

				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_FF SET Position=" + frage.getPosition() + " WHERE idRelFBFF=" + idRelFBFF;
				mySQL.executeUpdate(statement);
			} else {
				mySQL = myCon.createStatement();
				statement = "INSERT INTO Fb_has_FF VALUE (NULL," + idFragebogen + "," + idFreieFrage + ","
						+ frage.getPosition() + ",'" + frage.getFlags() + "')";
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
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}

	*//**
	 * Speichert eine neue Bewertungsfrage. Gibt bei Erfolg TRUE zurück.
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
	 *//*
	public static boolean saveBewertungsfrage(FragebogenDialog selectedFB, FrageErstellen frage) {

		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			int idKategorie = -1;
			int idFragebogen = selectedFB.getFragebogenId();
			int idBewertungsFrage = -1;
			int countAntwort = 0;
			String statement;

			if (frage.getFrageID() == 0) {
				mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				myRS = psSql.executeQuery();

				if (myRS.next()) {
					idBewertungsFrage = myRS.getInt("idMultipleChoice");
				}
				psSql = null;
				mySQL = null;
				myRS = null;
			} else {
				idBewertungsFrage = frage.getFrageID();
			}

			psSql = null;
			mySQL = myCon.createStatement();
			statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(frage.getKategorie()));
			myRS = psSql.executeQuery();

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
			myRS = null;
			mySQL = null;
			psSql = null;

			mySQL = myCon.createStatement();
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
				mySQL = myCon.createStatement();
				statement = "UPDATE MultipleChoice SET FrageMC=? WHERE idMultipleChoice=" + idBewertungsFrage;
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();

			} else {
				mySQL = null;
				psSql = null;

				mySQL = myCon.createStatement();
				statement = "INSERT INTO MultipleChoice VALUE(NULL, ? ," + idKategorie + ")";
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
				statement = "UPDATE FB_HAS_MC SET Flags='" + frage.getFlags() + "' WHERE idRelFBMC=" + idRelFBMC;
				mySQL.executeUpdate(statement);

				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_MC SET Position=" + frage.getPosition() + " WHERE idRelFBMC=" + idRelFBMC;
				mySQL.executeUpdate(statement);
			} else {
				mySQL = myCon.createStatement();
				statement = "INSERT INTO Fb_has_MC VALUE (NULL," + idFragebogen + "," + idBewertungsFrage + ","
						+ frage.getPosition() + ",'" + frage.getFlags() + "')";
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
					statement = "INSERT INTO MC_has_a VALUE (NULL, " + idBewertungsFrage + "," + antwortNr + ")";
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
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}

	*//**
	 * Speichert eine neue Multipe Choice Frage. Gibt bei Erfolg TRUE zurück.
	 * 
	 * @param neueFrageMc
	 *            Vector String
	 * @param selectedFB
	 *            FragebogenDialog
	 * @param spinnerValue
	 *            Object
	 * @return boolean
	 * @author Eric
	 *//*
	public static boolean saveMC(FragebogenDialog selectedFB, FrageErstellen frage, Vector<Integer> antIds) {

		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			int idMultipleChoice = frage.getFrageID();
			int idFragebogen = selectedFB.getFragebogenId();
			int idKategorie = -1;
			String statement;

			if (frage.getFrageID() == 0) {
				mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				myRS = psSql.executeQuery();

				if (myRS.next()) {
					idMultipleChoice = myRS.getInt("idMultipleChoice");
				}
				psSql = null;
				mySQL = null;
				myRS = null;
			} else {
				idMultipleChoice = frage.getFrageID();
			}

			// Kategorie
			psSql = null;
			mySQL = myCon.createStatement();
			statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(frage.getKategorie()));
			myRS = psSql.executeQuery();

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
			myRS = null;
			mySQL = null;
			psSql = null;

			// Frage
			mySQL = myCon.createStatement();
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
				mySQL = myCon.createStatement();
				statement = "UPDATE MultipleChoice SET FrageMC=? WHERE idMultipleChoice=" + idMultipleChoice;
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();
			} else {
				myRS = null;
				mySQL = null;
				psSql = null;

				mySQL = myCon.createStatement();
				statement = "INSERT INTO MultipleChoice VALUES(NULL, ?, " + idKategorie + ")";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(frage.getFrage()));
				psSql.executeUpdate();
				mySQL = null;
				psSql = null;

				mySQL = myCon.createStatement();
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
				statement = "UPDATE FB_HAS_MC SET Flags='" + frage.getFlags() + "' WHERE idRelFBMC=" + idRelFBMC;
				mySQL.executeUpdate(statement);

				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "UPDATE FB_HAS_MC SET Position=" + frage.getPosition() + " WHERE idRelFBMC=" + idRelFBMC;
				mySQL.executeUpdate(statement);
			} else {
				mySQL = myCon.createStatement();
				statement = "INSERT INTO Fb_has_MC VALUE (NULL," + idFragebogen + "," + idMultipleChoice + ","
						+ frage.getPosition() + ",'" + frage.getFlags() + "')";
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
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}

	*//**
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
	 *//*
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
			if (zahlArt.equals("Größer gleich Zahl")) {
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

	*//**
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
			// Bewertungsantworten hinzufügen
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
	 * Gibt alle Ueberschriften des gegebenen Fragebogen zurueck.
	 * 
	 * @param fb
	 *            FrageobgenDialog: der Fragebogen
	 * @return Vector FrageErstellen
	 * @author Eric
	 *//*
	public static Vector<FrageErstellen> getUeberschriften(FragebogenDialog fb) {
		Vector<FrageErstellen> ueberschriften = new Vector<FrageErstellen>();
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statment = "SELECT mc1.FrageMC, mc1.idMultipleChoice, Fragebogen.Datum, fb_has_mc.Position, fb_has_mc.Flags, Kategorie, Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE Fragebogen.idFragebogen="
					+ fb.getFragebogenId() + " AND antwort='#####'";
			myRS = mySQL.executeQuery(statment);
			while (myRS.next()) {
				FrageErstellen frage = new FrageErstellen();
				frage.setFrage(unslashUnicode(myRS.getString("FrageMC")));
				frage.setFrageID(myRS.getInt("idMultipleChoice"));
				frage.setKategorie(unslashUnicode(myRS.getString("Kategorie")));
				frage.setDatum(myRS.getString("Datum"));
				frage.setFlags("");
				frage.setPosition(Integer.parseInt(myRS.getString("Position")));
				frage.setArt("MC");
				frage.setFragebogenID(fb.getFragebogenId());
				frage.setAntwort_moeglichkeit(myRS.getString("Antwort"));
				ueberschriften.add(frage);
			}

		} catch (SQLException e) {
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
		}
		return ueberschriften;
	}

	*//**
	 * Speichert alle durch den Benutzer gegebenen Antworten in die Datenbank.
	 * 
	 * @param fragen
	 *            Vector Vector FrageErstellen: alle Fragen des Fragebogens
	 * @author Julian und Eric
	 *//*
	public static void saveUmfrage(Vector<Vector<FrageErstellen>> fragen) {
		try {
			myCon = DriverManager.getConnection(url, user, pwd);

			String statement = "INSERT INTO Befragung VALUE(NULL, CURDATE(), ?)";
			psSql = myCon.prepareStatement(statement);
			psSql.setInt(1, fragen.get(0).get(0).getFragebogenID());
			psSql.executeUpdate();

			int b_id = 0;

			myRS = null;
			mySQL = null;
			mySQL = myCon.createStatement();
			statement = "SELECT MAX(idBefragung) FROM Befragung";
			myRS = mySQL.executeQuery(statement);
			if (myRS.next()) {
				b_id = myRS.getInt("MAX(idBefragung)");

				for (int i = 0; i < fragen.size(); i++) {
					Vector<FrageErstellen> panel = fragen.get(i);
					for (FrageErstellen frage : panel) {
						if (frage.getArt() == "MC") {
							if (frage.getAntwort().size() > 0) {
								for (String antwort : frage.getAntwort()) {
									antwort = antwort.replaceAll("<.*>", "");
									myRS = null;
									mySQL = null;
									mySQL = myCon.createStatement();
									statement = "SELECT B_has_MCid FROM B_has_MC WHERE idBefragung=" + b_id
											+ " AND idMultipleChoice=" + frage.getFrageID() + " AND AntwortNr="
											+ getAntwortID(antwort);
									myRS = mySQL.executeQuery(statement);
									if (!myRS.next()) {
										myRS = null;
										mySQL = null;
										mySQL = myCon.createStatement();
										statement = "INSERT INTO B_has_MC VALUE(NULL," + b_id + ", "
												+ frage.getFrageID() + ", " + getAntwortID(antwort) + ")";
										mySQL.executeUpdate(statement);
									}
								}
							}
						} else if (frage.getAntwort().size() > 0) {
							for (String antwort : frage.getAntwort()) {
								antwort = antwort.replaceAll("<.*>", "");
								myRS = null;
								mySQL = null;
								mySQL = myCon.createStatement();
								statement = "SELECT B_has_FFid FROM B_has_FF WHERE idBefragung=" + b_id
										+ " AND idFreieFragen=" + frage.getFrageID() + " AND AntwortNr="
										+ getAntwortID(antwort);
								myRS = mySQL.executeQuery(statement);
								if (!myRS.next()) {
									myRS = null;
									mySQL = null;
									mySQL = myCon.createStatement();
									statement = "INSERT INTO B_has_FF VALUE(NULL," + b_id + ", " + frage.getFrageID()
											+ ", " + getAntwortID(antwort) + ")";
									mySQL.executeUpdate(statement);
								}
							}
						}
					}
				}
			}
			myCon.close();
		} catch (SQLException e) {
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
		}
		Main.resetFragebogen();
	}

	*//**
	 * Gibt die ID der Antwort zurück. Ist die Antwort noch nicht vorhanden,
	 * wird sie zuerst in der Datenbank gespeichert.
	 * 
	 * @param antwort
	 *            String
	 * @return int
	 * @author Eric
	 *//*
	public static int getAntwortID(String antwort) {
		int id = -1;
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			if (antwort == null) {
				antwort = "NULL";
			}
			myRS = null;
			mySQL = null;
			psSql = null;
			mySQL = myCon.createStatement();
			String statement = "SELECT AntwortNr FROM Antworten WHERE Antwort=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(antwort));
			myRS = psSql.executeQuery();
			if (myRS.next()) {

				id = myRS.getInt("AntwortNr");
			} else {
				myRS = null;
				mySQL = null;
				mySQL = myCon.createStatement();
				statement = "INSERT INTO Antworten VALUE(NULL, ?)";
				psSql = myCon.prepareStatement(statement);
				psSql.setString(1, slashUnicode(antwort));
				psSql.executeUpdate();

				myRS = null;
				mySQL = null;
				psSql = null;
				mySQL = myCon.createStatement();
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
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return id;
	}

	*//**
	 * Gibt die Anzahl an gespeicherten Befragungen zurück.
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
	 * Setzt den gegebenen Fragebogen auf aktiv und deaktiviert alle anderen
	 * Frageboegen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Anne
	 *//*
	public static boolean updateFragebogen(FragebogenDialog fb) {
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET aktiviert=TRUE WHERE idFragebogen=" + fb.getFragebogenId();
			mySQL.execute(statement);

			mySQL = null;
			mySQL = myCon.createStatement();
			statement = "UPDATE fragebogen SET aktiviert=FALSE WHERE NOT idFragebogen=" + fb.getFragebogenId();
			mySQL.execute(statement);

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
	 * Deaktiviert den gegebenen Fragebogen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 *//*
	public static boolean disableFragebogen(FragebogenDialog fb) {
		try {
			// anneSuperNeu
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET aktiviert=FALSE WHERE idFragebogen=" + fb.getFragebogenId();
			mySQL.execute(statement);

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
	 * Kopiert den gegebenen Fragebogen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 *//*
	public static boolean copyFragebogen(FragebogenDialog fb) {
		int oldID = fb.getFragebogenId();
		int newID = -1;
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			int ortID = -1;
			psSql = null;
			mySQL = myCon.createStatement();
			String statement = "SELECT idOrt FROM Ort WHERE ort=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(fb.getOrt()));
			myRS = psSql.executeQuery();
			if (myRS.next()) {
				ortID = myRS.getInt("idOrt");
			}
			mySQL = null;
			myRS = null;
			mySQL = myCon.createStatement();
			statement = "INSERT INTO fragebogen VALUE(NULL, '" + Main.getcurDate() + "', '"
					+ slashUnicode(fb.getFragebogenName()) + "', FALSE, " + ortID + ", FALSE)";
			mySQL.execute(statement);
			mySQL = null;
			myRS = null;
			mySQL = myCon.createStatement();
			statement = "SELECT MAX(idFragebogen) FROM fragebogen";
			myRS = mySQL.executeQuery(statement);
			if (myRS.next()) {
				newID = myRS.getInt("MAX(idFragebogen)");

				mySQL = null;
				myRS = null;

				mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice, Position, Flags FROM Fb_has_MC WHERE idFragebogen=" + oldID;
				myRS = mySQL.executeQuery(statement);

				Vector<Vector<String>> vecs = new Vector<Vector<String>>();
				while (myRS.next()) {
					Vector<String> vec = new Vector<String>();
					vec.add(myRS.getInt("idMultipleChoice") + "");
					vec.add(myRS.getInt("Position") + "");
					vec.add(myRS.getString("Flags"));
					vecs.add(vec);
				}

				Vector<FrageErstellen> mcFragen = new Vector<>();

				for (Vector<String> data : vecs) {
					FrageErstellen mcFrage = new FrageErstellen();
					int newFragenID = -1;
					int position = Integer.parseInt(data.get(1));
					String flags = data.get(2);
					mySQL = null;
					mySQL = myCon.createStatement();
					statement = "SELECT FrageMC, idKategorie FROM MultipleChoice WHERE idMultipleChoice="
							+ Integer.parseInt(data.get(0));
					ResultSet myRS2 = mySQL.executeQuery(statement);
					if (myRS2.next()) {
						int katID = myRS2.getInt("idKategorie");
						mySQL = null;
						mySQL = myCon.createStatement();
						String frage = slashUnicode(duplicateFrage(unslashUnicode(myRS2.getString("FrageMC"))));
						if (frage.contains("\\")) {
							frage = frage.replaceAll("\\\\", "\\\\\\\\");
						}
						statement = "INSERT INTO MultipleChoice VALUE(NULL, '" + frage + "', " + katID + ")";
						mcFrage.setFrage(frage);
						mySQL.execute(statement);

						mySQL = null;
						mySQL = myCon.createStatement();
						statement = "SELECT MAX(idMultipleChoice) FROM MultipleChoice";
						ResultSet myRS3 = mySQL.executeQuery(statement);
						if (myRS3.next()) {
							newFragenID = myRS3.getInt("MAX(idMultipleChoice)");
						}
					}

					mySQL = null;
					mySQL = myCon.createStatement();
					statement = "INSERT INTO Fb_has_MC VALUE(NULL, " + newID + ", " + newFragenID + ", " + position
							+ ", '" + flags + "')";
					mcFrage.setPosition(position);
					mcFrage.setFlags(flags);
					mcFrage.setFrageID(newFragenID);
					mcFrage.setFragebogenID(newID);
					mySQL.execute(statement);

					mySQL = null;
					mySQL = myCon.createStatement();
					statement = "SELECT Mc_has_a.AntwortNr FROM Antworten JOIN Mc_has_A ON Mc_has_A.AntwortNr=Antworten.AntwortNr WHERE idMultipleChoice="
							+ Integer.parseInt(data.get(0));
					ResultSet myRS4 = mySQL.executeQuery(statement);
					while (myRS4.next()) {
						mySQL = null;
						mySQL = myCon.createStatement();
						statement = "INSERT INTO Mc_has_A VALUE(NULL, " + newFragenID + ", " + myRS4.getInt("AntwortNr")
								+ ")";
						mySQL.execute(statement);
					}

					mcFragen.add(mcFrage);
				}

				mySQL = null;
				myRS = null;

				mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen, Position, Flags FROM Fb_has_FF WHERE idFragebogen=" + oldID;
				myRS = mySQL.executeQuery(statement);
				Vector<Vector<String>> vecs2 = new Vector<Vector<String>>();
				while (myRS.next()) {
					Vector<String> vec = new Vector<String>();
					vec.add(myRS.getInt("idFreieFragen") + "");
					vec.add(myRS.getInt("Position") + "");
					vec.add(myRS.getString("Flags"));
					vecs2.add(vec);
				}

				Vector<FrageErstellen> ffFragen = new Vector<>();
				for (Vector<String> data : vecs2) {
					FrageErstellen ffFrage = new FrageErstellen();
					int newFragenID = -1;
					int position = Integer.parseInt(data.get(1));
					String flags = data.get(2);
					mySQL = null;
					mySQL = myCon.createStatement();
					statement = "SELECT FrageFF, idKategorie FROM FreieFragen WHERE idFreieFragen="
							+ Integer.parseInt(data.get(0));
					ResultSet myRS2 = mySQL.executeQuery(statement);
					if (myRS2.next()) {
						int katID = myRS2.getInt("idKategorie");
						mySQL = null;
						mySQL = myCon.createStatement();
						String frage = slashUnicode(duplicateFrage(unslashUnicode(myRS2.getString("FrageFF"))));
						if (frage.contains("\\")) {
							System.out.println(frage);
							frage = frage.replaceAll("\\\\", "\\\\\\\\");
						}
						statement = "INSERT INTO FreieFragen VALUE(NULL, '" + frage + "', " + katID + ")";
						ffFrage.setFrage(frage);
						mySQL.execute(statement);

						mySQL = null;
						mySQL = myCon.createStatement();
						statement = "SELECT MAX(idFreieFragen) FROM FreieFragen";
						ResultSet myRS3 = mySQL.executeQuery(statement);
						if (myRS3.next()) {
							newFragenID = myRS3.getInt("MAX(idFreieFragen)");
						}
					}

					
					 * Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
					 * Matcher mges = MY_PATTERN.matcher(data.get(2));
					 * 
					 * if (mges.find()) { Pattern MY_PATTERN1 =
					 * Pattern.compile("MC[0-9]+"); Matcher m1 =
					 * MY_PATTERN1.matcher(mges.group(0)); Pattern MY_PATTERN2 =
					 * Pattern.compile("A[0-9]+"); Matcher m2 =
					 * MY_PATTERN2.matcher(mges.group(0)); if (m1.find() &&
					 * m2.find()) { int oldMcId =
					 * Integer.parseInt(m1.group(0).substring(2)); int diff =
					 * oldMcId - Integer.parseInt(data.get(0)); String flag2 =
					 * "MC" + (newFragenID + diff) + m2.group(0); flags =
					 * flags.replace(mges.group(0), flag2); } }
					 * 
					 * Pattern MY_PATTERNFF =
					 * Pattern.compile("FF[0-9]+A[0-9]+"); Matcher mgesFF =
					 * MY_PATTERNFF.matcher(data.get(2));
					 * 
					 * if (mgesFF.find()) { Pattern MY_PATTERN1 =
					 * Pattern.compile("FF[0-9]+"); Matcher m1 =
					 * MY_PATTERN1.matcher(mgesFF.group(0)); Pattern MY_PATTERN2
					 * = Pattern.compile("A[0-9]+"); Matcher m2 =
					 * MY_PATTERN2.matcher(mgesFF.group(0)); if (m1.find() &&
					 * m2.find()) { int oldMcId =
					 * Integer.parseInt(m1.group(0).substring(2)); int diff =
					 * oldMcId - Integer.parseInt(data.get(0)); String flag2 =
					 * "FF" + (newFragenID + diff) + m2.group(0); flags =
					 * flags.replace(mgesFF.group(0), flag2); } }
					 

					mySQL = null;
					mySQL = myCon.createStatement();
					statement = "INSERT INTO Fb_has_FF VALUE(NULL, " + newID + ", " + newFragenID + ", " + position
							+ ", '" + flags + "')";
					ffFrage.setFrageID(newFragenID);
					ffFrage.setFragebogenID(newID);
					ffFrage.setPosition(position);
					ffFrage.setFlags(flags);
					mySQL.execute(statement);

					ffFragen.add(ffFrage);
				}

				for (int i = 0; i < vecs.size(); ++i) {
					Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
					Matcher mges = MY_PATTERN.matcher(mcFragen.get(i).getFlags());

					if (mges.find()) {
						Pattern MY_PATTERN1 = Pattern.compile("MC[0-9]+");
						Matcher m1 = MY_PATTERN1.matcher(mges.group(0));
						Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
						Matcher m2 = MY_PATTERN2.matcher(mges.group(0));
						if (m1.find() && m2.find()) {
							int oldMcId = Integer.parseInt(m1.group(0).substring(2));
							int pos = -1;
							for (int j = 0; j < vecs.size(); ++j) {
								if (Integer.parseInt(vecs.get(j).get(0)) == oldMcId) {
									pos = j;
									break;
								}
							}

							int newMcId = mcFragen.get(pos).getFrageID();
							String flag = mcFragen.get(i).getFlags().replace(mges.group(0),
									"MC" + newMcId + m2.group(0));
							mySQL = null;
							mySQL = myCon.createStatement();
							statement = "UPDATE Fb_has_MC SET Flags='" + flag + "' WHERE idFragebogen="
									+ mcFragen.get(i).getFragebogenID() + " AND idMultipleChoice="
									+ mcFragen.get(i).getFrageID() + " AND Position=" + mcFragen.get(i).getPosition();
							mySQL.execute(statement);
						}
					}

					Pattern MY_PATTERNFF = Pattern.compile("FF[0-9]+A[0-9]+");
					Matcher mgesFF = MY_PATTERNFF.matcher(mcFragen.get(i).getFlags());

					if (mgesFF.find()) {
						Pattern MY_PATTERN1 = Pattern.compile("FF[0-9]+");
						Matcher m1 = MY_PATTERN1.matcher(mgesFF.group(0));
						Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
						Matcher m2 = MY_PATTERN2.matcher(mgesFF.group(0));
						if (m1.find() && m2.find()) {
							int oldMcId = Integer.parseInt(m1.group(0).substring(2));
							int pos = -1;
							for (int j = 0; j < vecs2.size(); ++j) {
								if (Integer.parseInt(vecs2.get(j).get(0)) == oldMcId) {
									pos = j;
									break;
								}
							}

							int newMcId = ffFragen.get(pos).getFrageID();
							String flag = mcFragen.get(i).getFlags().replace(mgesFF.group(0),
									"FF" + newMcId + m2.group(0));
							mySQL = null;
							mySQL = myCon.createStatement();
							statement = "UPDATE Fb_has_MC SET Flags='" + flag + "' WHERE idFragebogen="
									+ mcFragen.get(i).getFragebogenID() + " AND idMultipleChoice="
									+ mcFragen.get(i).getFrageID() + " AND Position=" + mcFragen.get(i).getPosition();
							mySQL.execute(statement);
						}
					}
				}

				for (int i = 0; i < vecs2.size(); ++i) {
					Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
					Matcher mges = MY_PATTERN.matcher(ffFragen.get(i).getFlags());

					if (mges.find()) {
						Pattern MY_PATTERN1 = Pattern.compile("MC[0-9]+");
						Matcher m1 = MY_PATTERN1.matcher(mges.group(0));
						Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
						Matcher m2 = MY_PATTERN2.matcher(mges.group(0));
						if (m1.find() && m2.find()) {
							int oldMcId = Integer.parseInt(m1.group(0).substring(2));
							int pos = -1;
							for (int j = 0; j < vecs.size(); ++j) {
								if (Integer.parseInt(vecs.get(j).get(0)) == oldMcId) {
									pos = j;
									break;
								}
							}

							int newMcId = mcFragen.get(pos).getFrageID();
							String flag = ffFragen.get(i).getFlags().replace(mges.group(0),
									"MC" + newMcId + m2.group(0));
							mySQL = null;
							mySQL = myCon.createStatement();
							statement = "UPDATE Fb_has_FF SET Flags='" + flag + "' WHERE idFragebogen="
									+ ffFragen.get(i).getFragebogenID() + " AND idFreieFragen="
									+ ffFragen.get(i).getFrageID() + " AND Position=" + ffFragen.get(i).getPosition();
							mySQL.execute(statement);
						}
					}

					Pattern MY_PATTERNFF = Pattern.compile("FF[0-9]+A[0-9]+");
					Matcher mgesFF = MY_PATTERNFF.matcher(ffFragen.get(i).getFlags());

					if (mgesFF.find()) {
						Pattern MY_PATTERN1 = Pattern.compile("FF[0-9]+");
						Matcher m1 = MY_PATTERN1.matcher(mgesFF.group(0));
						Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
						Matcher m2 = MY_PATTERN2.matcher(mgesFF.group(0));
						if (m1.find() && m2.find()) {
							int oldMcId = Integer.parseInt(m1.group(0).substring(2));
							int pos = -1;
							for (int j = 0; j < vecs2.size(); ++j) {
								if (Integer.parseInt(vecs2.get(j).get(0)) == oldMcId) {
									pos = j;
									break;
								}
							}

							int newMcId = ffFragen.get(pos).getFrageID();
							String flag = ffFragen.get(i).getFlags().replace(mgesFF.group(0),
									"FF" + newMcId + m2.group(0));
							mySQL = null;
							mySQL = myCon.createStatement();
							statement = "UPDATE Fb_has_FF SET Flags='" + flag + "' WHERE idFragebogen="
									+ ffFragen.get(i).getFragebogenID() + " AND idFreieFragen="
									+ ffFragen.get(i).getFrageID() + " AND Position=" + ffFragen.get(i).getPosition();
							mySQL.execute(statement);
						}
					}
				}
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
	 * Updated die Flags, beim Löschen einer Antwort, auf die reagiert wird.
	 * Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param frage
	 *            FrageErstellen: die Frage
	 * @return boolean
	 *//*
	// anneSehrNeu
	public static boolean updateFlags(FrageErstellen frage) {
		String statement;
		String flag = "";
		int start = -1;
		int end = -1;

		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			if (frage.getArt().equals("MC")) {
				mySQL = myCon.createStatement();
				statement = "SELECT flags, idMultipleChoice FROM FB_has_mc where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu löschende Frage
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

				// Flags updaten, wenn eine Frage auf die zu löschende Frage
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
				mySQL = myCon.createStatement();
				statement = "SELECT flags, idFreieFragen FROM FB_has_ff where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu löschende Frage
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

				// Flags updaten, wenn eine Frage auf die zu löschende Frage
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

			mySQL = null;
			myRS = null;
			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	*//**
	 * Benennt den gegebenen Fragebogen um. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 *//*
	public static boolean renameFragebogen(FragebogenDialog fb) {
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET Name='" + slashUnicode(fb.getFragebogenName())
					+ "' WHERE idFragebogen=" + fb.getFragebogenId();
			mySQL.execute(statement);

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
	 * Loescht den gegebenen Fragebogen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Anne
	 *//*
	public static boolean deleteFragebogen(FragebogenDialog fb) {
		Vector<Integer> idsmc = new Vector<Integer>(); // IDs der MC Fragen
		Vector<Integer> idsff = new Vector<Integer>(); // IDs der Freien Fragen
		Vector<Integer> antmcnr = new Vector<Integer>(); // IDs der Antworten
															// aus MC Fragen
		Vector<String> antwortenmc = new Vector<String>(); // Antworten zu MC
															// Fragen

		try {
			// Multiple Choice ids mit Antworten
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "SELECT fb_has_mc.idMultipleChoice, Antworten.AntwortNr, Antworten.Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN "
					+ "multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten "
					+ "ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE fragebogen.idFragebogen=" + fb.getFragebogenId();
			myRS = mySQL.executeQuery(statement);

			while (myRS.next()) {
				if (!antmcnr.isEmpty()) {
					for (int i = 0; i < antmcnr.size(); i++) {
						if (myRS.getInt("AntwortNr") != antmcnr.get(i)
								&& !myRS.getString("Antwort").equals(antwortenmc.get(i))) {
							antmcnr.add(myRS.getInt("AntwortNr"));
							antwortenmc.addElement(myRS.getString("Antwort"));
							break;
						}
					}
				} else {
					antmcnr.add(myRS.getInt("AntwortNr"));
					antwortenmc.addElement(myRS.getString("Antwort"));
				}
				idsmc.add(myRS.getInt("idMultipleChoice"));
			}
			myRS = null;
			mySQL = null;

			mySQL = myCon.createStatement();
			statement = "SELECT fb_has_mc.idMultipleChoice, Antworten.AntwortNr, Antworten.Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN "
					+ "multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON "
					+ "mc_has_a.AntwortNr=antworten.AntwortNr WHERE fragebogen.idFragebogen!=" + fb.getFragebogenId();
			myRS = mySQL.executeQuery(statement);

			// Antworten, die noch in einem anderen Fragebogen vorkommen, aus
			// dem Vector entfernen
			while (myRS.next()) {
				for (int i = 0; i < antmcnr.size(); i++) {
					if (myRS.getInt("AntwortNr") == antmcnr.get(i)) {
						antmcnr.remove(i);
					}
				}
			}
			myRS = null;
			mySQL = null;

			// Freie Fragen mit id
			mySQL = myCon.createStatement();
			statement = "SELECT fb_has_ff.idFreieFragen FROM fb_has_ff WHERE idFragebogen=" + fb.getFragebogenId();
			myRS = mySQL.executeQuery(statement);

			while (myRS.next()) {
				idsff.add(myRS.getInt("idFreieFragen"));
			}
			myRS = null;
			mySQL = null;

			// Löschen der Relationen von Fragebogen zu MultipleChoice
			for (short i = 0; i < idsmc.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Mc WHERE idMultipleChoice=" + idsmc.get(i) + " AND idFragebogen="
						+ fb.getFragebogenId();
				mySQL.execute(statement);
				mySQL = null;
			}

			// Löschen der Relationen von Fragebogen zu FreieFragen
			for (short i = 0; i < idsff.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Ff WHERE idFreieFragen=" + idsff.get(i) + " AND idFragebogen="
						+ fb.getFragebogenId();
				mySQL.execute(statement);
				mySQL = null;
			}

			for (short i = 0; i < idsmc.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice FROM FB_has_MC WHERE idMultipleChoice=" + idsmc.get(i);
				myRS = mySQL.executeQuery(statement);

				// steht ID der MC Frage nach dem Löschen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die MC Frage gelöscht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM MC_has_A WHERE idMultipleChoice=" + idsmc.get(i);
					mySQL.execute(statement);
					mySQL = null;

					mySQL = myCon.createStatement();
					statement = "DELETE FROM MultipleChoice WHERE idMultipleChoice=" + idsmc.get(i);
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			for (short i = 0; i < idsff.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen FROM Fb_has_ff WHERE idFreieFragen=" + idsff.get(i);
				myRS = mySQL.executeQuery(statement);
				// steht ID der FF Frage nach dem Löschen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die FF Frage gelöscht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM FreieFragen WHERE idFreieFragen=" + idsff.get(i);
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			// Antworten löschen, wenn nicht 0-10 / ja / nein / ##### (Multiple
			// Choice Edition)
			for (short j = 0; j < antmcnr.size(); j++) {
				if (!antwortenmc.get(j).equals("0") && !antwortenmc.get(j).equals("1")
						&& !antwortenmc.get(j).equals("2") && !antwortenmc.get(j).equals("3")
						&& !antwortenmc.get(j).equals("4") && !antwortenmc.get(j).equals("5")
						&& !antwortenmc.get(j).equals("6") && !antwortenmc.get(j).equals("7")
						&& !antwortenmc.get(j).equals("8") && !antwortenmc.get(j).equals("9")
						&& !antwortenmc.get(j).equals("10") && !antwortenmc.get(j).equals("ja")
						&& !antwortenmc.get(j).equals("nein") && !antwortenmc.get(j).equals("#####")) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM Antworten WHERE AntwortNr=" + antmcnr.get(j);
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			mySQL = null;

			mySQL = myCon.createStatement();
			statement = "DELETE FROM fragebogen WHERE idFragebogen=" + fb.getFragebogenId();
			mySQL.execute(statement);

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
	 * Setzt den gegebenen Fragebogen auf final. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 *//*
	public static boolean setFinal(FragebogenDialog fb) {
		try {
			// anneSuperNeu
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET final=TRUE WHERE idFragebogen=" + fb.getFragebogenId();
			mySQL.execute(statement);

			mySQL = null;
			myRS = null;
			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	*//**
	 * Setzt den gegebenen Fragebogen auf nicht final. Gibt bei Erfolg TRUE
	 * zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog
	 * @return boolean
	 * @author Eric
	 *//*
	public static boolean setUnFinal(FragebogenDialog fb) {
		try {
			// anneSuperNeu
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET final=FALSE WHERE idFragebogen=" + fb.getFragebogenId();
			mySQL.execute(statement);

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
	 * Prueft, ob der gegebenen Fragebogen final ist. Gibt bei Erfolg TRUE
	 * zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 *//*
	public static boolean isFinal(FragebogenDialog fb) {
		try {
			// anneSuperNeu
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "SELECT idFragebogen FROM fragebogen WHERE final=TRUE AND idFragebogen="
					+ fb.getFragebogenId();
			myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				mySQL = null;
				myRS = null;
				myCon.close();
				return true;
			} else {
				mySQL = null;
				myRS = null;
				myCon.close();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
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
	 * Loescht die gegebene Frage. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param frage
	 *            FrageErstellen: die Frage
	 * @return boolean
	 *//*
	// anneSehrNeu
	public static boolean deleteFrage(FrageErstellen frage) {
		Vector<Integer> antmcnr = new Vector<Integer>(); // IDs der Antworten
															// aus MC Fragen
		Vector<String> antwortenmc = new Vector<String>(); // Antworten zu MC
															// Fragen
		String statement;
		int start = -1;
		int end = -1;

		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			if (frage.getArt().equals("MC")) {
				mySQL = myCon.createStatement();
				statement = "SELECT flags, idMultipleChoice FROM FB_has_mc where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu löschende Frage
				// reagiert
				while (myRS.next()) {
					String flag = myRS.getString("flags");
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
				statement = "SELECT flags, idFreieFrage FROM FB_has_ff where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu löschende Frage
				// reagiert
				while (myRS.next()) {
					String flag = myRS.getString("flags");
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
				statement = "SELECT Antworten.AntwortNr, Antworten.Antwort FROM multiplechoice mc JOIN mc_has_a ON mc.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten "
						+ "ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE mc.idmultiplechoice=" + frage.getFrageID();
				myRS = mySQL.executeQuery(statement);

				while (myRS.next()) {
					if (!antmcnr.isEmpty()) {
						for (int i = 0; i < antmcnr.size(); i++) {
							if (myRS.getInt("AntwortNr") != antmcnr.get(i)
									&& !myRS.getString("Antwort").equals(antwortenmc.get(i))) {
								antmcnr.add(myRS.getInt("AntwortNr"));
								antwortenmc.addElement(myRS.getString("Antwort"));
							}
						}
					} else {
						antmcnr.add(myRS.getInt("AntwortNr"));
						antwortenmc.addElement(myRS.getString("Antwort"));
					}
				}

				// Antworten, die noch in einem anderen Fragebogen vorkommen,
				// aus dem Vector entfernen
				while (myRS.next()) {
					for (int i = 0; i < antmcnr.size(); i++) {
						if (myRS.getInt("AntwortNr") == antmcnr.get(i)) {
							antmcnr.remove(i);
						}
					}
				}

				myRS = null;
				mySQL = null;

				// Löschen der Relationen von Fragebogen zu MultipleChoice
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Mc WHERE idMultipleChoice=" + frage.getFrageID();
				mySQL.execute(statement);
				mySQL = null;
			} else if (frage.getArt().equals("FF")) {
				mySQL = myCon.createStatement();
				statement = "SELECT flags, idFreieFragen FROM FB_has_ff where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu löschende Frage
				// reagiert
				while (myRS.next()) {
					String flag = myRS.getString("flags");
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

				// Flags updaten, wenn eine Frage auf die zu löschende Frage
				// reagiert
				while (myRS.next()) {
					String flag = myRS.getString("flags");
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

				// Löschen der Relationen von Fragebogen zu FreieFragen
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Ff WHERE idFreieFragen=" + frage.getFrageID();
				mySQL.execute(statement);
				mySQL = null;
			}

			if (frage.getArt().equals("MC")) {
				mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice FROM FB_has_MC WHERE idMultipleChoice=" + frage.getFrageID();
				myRS = mySQL.executeQuery(statement);

				// steht ID der MC Frage nach dem Löschen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die MC Frage gelöscht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM MC_has_A WHERE idMultipleChoice=" + frage.getFrageID();
					mySQL.execute(statement);
					mySQL = null;

					mySQL = myCon.createStatement();
					statement = "DELETE FROM MultipleChoice WHERE idMultipleChoice=" + frage.getFrageID();
					mySQL.execute(statement);
					mySQL = null;
				}
			} else if (frage.getArt().equals("FF")) {
				mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen FROM Fb_has_ff WHERE idFreieFragen=" + frage.getFrageID();
				myRS = mySQL.executeQuery(statement);

				// steht ID der FF Frage nach dem Löschen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die FF Frage gelöscht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM FreieFragen WHERE idFreieFragen=" + frage.getFrageID();
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			// Antworten löschen, wenn nicht 0-10 / ja / nein / ##### (Multiple
			// Choice Edition)
			for (short j = 0; j < antmcnr.size(); j++) {
				if (!antwortenmc.get(j).equals("0") && !antwortenmc.get(j).equals("1")
						&& !antwortenmc.get(j).equals("2") && !antwortenmc.get(j).equals("3")
						&& !antwortenmc.get(j).equals("4") && !antwortenmc.get(j).equals("5")
						&& !antwortenmc.get(j).equals("6") && !antwortenmc.get(j).equals("7")
						&& !antwortenmc.get(j).equals("8") && !antwortenmc.get(j).equals("9")
						&& !antwortenmc.get(j).equals("10") && !antwortenmc.get(j).equals("ja")
						&& !antwortenmc.get(j).equals("nein") && !antwortenmc.get(j).equals("#####")) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM Antworten WHERE AntwortNr=" + antmcnr.get(j);
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			mySQL = null;
			myRS = null;
			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	*//**
	 * Loescht die gegebene Antwort. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param antids
	 *            Vector<Integer>: ids der zu-löschendenen Antworten
	 * @param frage
	 *            FrageErstellen: die zugehörige Frage
	 * @return boolean
	 *//*
	// anneSehrNeu
	public static boolean deleteAntworten(Vector<Integer> antids, FrageErstellen frage) {
		String statement;

		try {
			myCon = DriverManager.getConnection(url, user, pwd);

			for (int i = 0; i < antids.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "DELETE FROM MC_has_A WHERE AntwortNr=" + antids.get(i) + " AND idMultipleChoice="
						+ frage.getFrageID();
				mySQL.execute(statement);
				mySQL = null;
			}

			mySQL = myCon.createStatement();
			statement = "DELETE FROM Antworten WHERE AntwortNr NOT IN (SELECT AntwortNr FROM MC_has_a)";
			
			 * + " AND NOT(antwort='ja') AND NOT(antwort='nein') " +
			 * "AND NOT(antwort='#####') AND NOT(antwort='0') AND NOT(antwort='1') AND NOT(antwort='2') AND NOT(antwort='3') AND NOT(antwort='4') "
			 * +
			 * "AND NOT(antwort='5') AND NOT(antwort='6') AND NOT(antwort='7') AND NOT(antwort='8') AND NOT(antwort='9') AND NOT(antwort='10')"
			 * ;
			 
			mySQL.execute(statement);
			mySQL = null;

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
	 * Gibt alle Kategorien zurueck.
	 * 
	 * @return Vector String aller Kategorien
	 *//*
	public static Vector<String> getKategorie() {
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "SELECT * FROM kategorie";
			myRS = mySQL.executeQuery(statement);
			Vector<String> kategorien = new Vector<String>();

			while (myRS.next()) {
				kategorien.add(unslashUnicode(myRS.getString("Kategorie")));
			}
			mySQL = null;
			myRS = null;
			myCon.close();
			return kategorien;
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
	 * Gibt alle Antworten einer Frage zurueck.
	 * 
	 * @param frage
	 *            FrageErstellen: die Frage
	 * @return Vector String aller Antworten
	 *//*
	public static Vector<String> getAntworten(FrageErstellen frage) {
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
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
			myRS = mySQL.executeQuery(statement);
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
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return null;
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

	*//**
	 * Updatet die Flags. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @param art
	 *            String: Art der Frage
	 * @param id
	 *            int: ID der Frage
	 * @return boolean
	 *//*
	public static boolean updateFlags(FragebogenDialog fb, String art, int id) {
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
						String flags = myRS.getString("Flags") + " +";

						mySQL = myCon.createStatement();
						statement = "UPDATE Fb_has_FF SET Flags='" + flags + "'  WHERE idFragebogen="
								+ fb.getFragebogenId() + " AND idFreieFragen=" + id;
						mySQL.executeUpdate(statement);
						return true;
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
				String statement = "SELECT Flags FROM Fb_has_MC WHERE idFragebogen=" + fb.getFragebogenId()
						+ " AND idMultipleChoice=" + id;
				myRS = mySQL.executeQuery(statement);

				if (myRS.next()) {
					if (myRS.getString("Flags").indexOf("+") >= 0) {
						return true;
					} else {
						String flags = myRS.getString("Flags") + " +";

						mySQL = myCon.createStatement();
						statement = "UPDATE Fb_has_MC SET Flags='" + flags + "'  WHERE idFragebogen="
								+ fb.getFragebogenId() + " AND idMultipleChoice=" + id;
						mySQL.executeUpdate(statement);
						return true;
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

	// Maximum an möglicher Position im Fragebogen herausfinden; für den Spinner
	// beim Fragen Erstellen/bearbeiten
	*//**
	 * Gibt das Maximum an moeglicher Position im Fragebogen zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return int
	 *//*
	public static int getCountPosition(FragebogenDialog fb) {
		int positionCount = 0;
		int maxPosMc = 0;
		int maxPosFf = 0;
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			mySQL = myCon.createStatement();
			String statement = "SELECT MAX(fb_has_mc.Position) AS position FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen WHERE fragebogen.idFragebogen="
					+ fb.getFragebogenId();
			myRS = mySQL.executeQuery(statement);
			if (myRS.next()) {
				maxPosMc = myRS.getInt("position");
			}

			myRS = null;
			mySQL = null;
			mySQL = myCon.createStatement();
			statement = "SELECT MAX(fb_has_ff.Position) AS position FROM fragebogen JOIN fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen WHERE fragebogen.idFragebogen="
					+ fb.getFragebogenId();
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
			ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return positionCount;
	}

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
			unSlashed = unSlashed.replaceAll("Ä", "\\\\\\u00c4");
		}
		if (unSlashed.indexOf("ä") >= 0) {
			unSlashed = unSlashed.replaceAll("ä", "\\\\\\u00e4");
		}
		if (unSlashed.indexOf("Ö") >= 0) {
			unSlashed = unSlashed.replaceAll("Ö", "\\\\\\u00d6");
		}
		if (unSlashed.indexOf("ö") >= 0) {
			unSlashed = unSlashed.replaceAll("ö", "\\\\\\u00f6");
		}
		if (unSlashed.indexOf("Ü") >= 0) {
			unSlashed = unSlashed.replaceAll("Ü", "\\\\\\u00dc");
		}
		if (unSlashed.indexOf("ü") >= 0) {
			unSlashed = unSlashed.replaceAll("ü", "\\\\\\u00fc");
		}
		if (unSlashed.indexOf("ß") >= 0) {
			unSlashed = unSlashed.replaceAll("ß", "\\\\\\u00df");
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
