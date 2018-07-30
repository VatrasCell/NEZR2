package export;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import application.Datenbank;
import model.Frage;

public class ExportService extends Datenbank {
	/**
	 * Gibt die Anzahl an gespeicherten Befragungen zurÃ¼ck.
	 * 
	 * @return int
	 * @author Eric
	 */
	public static int getAnzahlBefragung() {
		int re = -1;
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			ResultSet myRS = mySQL.executeQuery("SELECT COUNT(idBefragung) FROM Befragung");
			if (myRS.next()) {
				re = myRS.getInt("COUNT(idBefragung)");
			}
			myCon.close();
		} catch (SQLException e) {
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return re;
	}
	
	/**
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
	 */
	public static Vector<ExcelCell> getAntwortenPosition(Frage frage, String von, String bis) {
		Vector<ExcelCell> re = new Vector<ExcelCell>();
		if (((frage.getArt().equals("MC")) && (frage.getFlags().indexOf("B") >= 0))
				|| (frage.getFlags().indexOf("LIST") >= 0) || (frage.getFlags().indexOf("JN") >= 0)) {
			try {
				Connection myCon = DriverManager.getConnection(url, user, pwd);
				Statement mySQL = myCon.createStatement();
				ResultSet myRS = mySQL.executeQuery(
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
				//ErrorLog.fehlerBerichtB("ERROR",
				//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
				//		e.getMessage());
			}
		} else if (frage.getArt().equals("FF")) {
			try {
				Connection myCon = DriverManager.getConnection(url, user, pwd);
				Statement mySQL = myCon.createStatement();
				ResultSet myRS = mySQL.executeQuery(
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
				//ErrorLog.fehlerBerichtB("ERROR",
				//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
				//		e.getMessage());
			}
		}
		return re;
	}

	/**
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
	 */
	public static Vector<ExcelCell> getAntwortenPosition(Frage frage, String antwort, String von, String bis) {
		Vector<ExcelCell> re = new Vector<ExcelCell>();
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			String statement = "SELECT b2.idBefragung FROM Antworten a JOIN B_has_MC b ON a.AntwortNr = b.AntwortNr JOIN Befragung b2 ON b.idBefragung = b2.idBefragung WHERE idFragebogen=? AND idMultipleChoice=? AND antwort=? AND (b2.Datum BETWEEN ? AND ?)";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setInt(1, frage.getFragebogenID());
			psSql.setInt(2, frage.getFrageID());
			psSql.setString(3, slashUnicode(antwort));
			psSql.setString(4, von);
			psSql.setString(5, bis);
			ResultSet myRS = psSql.executeQuery();

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
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return re;
	}
}
