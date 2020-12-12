package export;

import application.Database;
import flag.SymbolType;
import model.Question;
import model.QuestionType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ExportService extends Database {
	/**
	 * Gibt die Anzahl an gespeicherten Befragungen zurÃ¼ck.
	 * 
	 * @return int
	 * @author Eric
	 */
	public static int getAnzahlBefragung() {
		int re = -1;
		try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
			Statement mySQL = myCon.createStatement();
			ResultSet myRS = mySQL.executeQuery("SELECT COUNT(idBefragung) FROM Befragung");
			if (myRS.next()) {
				re = myRS.getInt("COUNT(idBefragung)");
			}
		} catch (SQLException e) {
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return re;
	}
	
	/**
	 * Erstellt einen ArrayList aus Excel Zellen anhand des FrageErstellen
	 * Objektes.
	 * 
	 * @param question
	 *            FrageErstellen
	 * @param von
	 *            String: Datum
	 * @param bis
	 *            Srring: Datum
	 * @return ArrayList
	 */
	public static ArrayList<ExcelCell> getAntwortenPosition(Question question, String von, String bis) {
		ArrayList<ExcelCell> re = new ArrayList<ExcelCell>();
		if (((question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) && (question.getFlags().is(SymbolType.B)))
				|| (question.getFlags().is(SymbolType.LIST)) || (question.getFlags().is(SymbolType.JN))) {
			try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
				Statement mySQL = myCon.createStatement();
				ResultSet myRS = mySQL.executeQuery(
						"SELECT b2.idBefragung, antwort FROM Antworten a JOIN B_has_MC b ON a.AntwortNr = b.AntwortNr JOIN Befragung b2 ON b.idBefragung = b2.idBefragung WHERE idFragebogen="
								+ question.getQuestionnaireId() + " AND idMultipleChoice=" + question.getQuestionId()
								+ " AND (b2.Datum BETWEEN '" + von + "' AND '" + bis + "')");

				int old = -1;
				ArrayList<String> strings = new ArrayList<String>();
				while (myRS.next()) {
					if (old != myRS.getInt("idBefragung")) {
						strings = new ArrayList<String>();
						strings.add(myRS.getString("antwort"));
						re.add(new ExcelCell(myRS.getInt("idBefragung"), strings));
						old = myRS.getInt("idBefragung");
					} else {
						strings.add(myRS.getString("antwort"));
					}
				}
				myCon.close();
			} catch (SQLException e) {
				//ErrorLog.fehlerBerichtB("ERROR",
				//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
				//		e.getMessage());
			}
		} else if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
			try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
				Statement mySQL = myCon.createStatement();
				ResultSet myRS = mySQL.executeQuery(
						"SELECT b2.idBefragung, antwort FROM Antworten a JOIN B_has_FF b ON a.AntwortNr = b.AntwortNr JOIN Befragung b2 ON b.idBefragung = b2.idBefragung WHERE idFragebogen="
								+ question.getQuestionnaireId() + " AND idFreieFragen=" + question.getQuestionId()
								+ " AND (b2.Datum BETWEEN '" + von + "' AND '" + bis + "')");

				int old = -1;
				ArrayList<String> strings = new ArrayList<String>();
				while (myRS.next()) {
					if (old != myRS.getInt("idBefragung")) {
						strings = new ArrayList<String>();
						strings.add(myRS.getString("antwort"));
						re.add(new ExcelCell(myRS.getInt("idBefragung"), strings));
						old = myRS.getInt("idBefragung");
					} else {
						strings.add(myRS.getString("antwort"));
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
	 * Erstellt einen ArrayList aus Excel Zellen anhand des FrageErstellen Objektes
	 * und der Antwort.
	 * 
	 * @param question
	 *            FrageErstellen
	 * @param antwort
	 *            String
	 * @param von
	 *            String: Datum
	 * @param bis
	 *            Srring: Datum
	 * @return ArrayList
	 */
	public static ArrayList<ExcelCell> getAntwortenPosition(Question question, String antwort, String von, String bis) {
		ArrayList<ExcelCell> re = new ArrayList<ExcelCell>();
		try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
			String statement = "SELECT b2.idBefragung FROM Antworten a JOIN B_has_MC b ON a.AntwortNr = b.AntwortNr JOIN Befragung b2 ON b.idBefragung = b2.idBefragung WHERE idFragebogen=? AND idMultipleChoice=? AND antwort=? AND (b2.Datum BETWEEN ? AND ?)";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setInt(1, question.getQuestionnaireId());
			psSql.setInt(2, question.getQuestionId());
			psSql.setString(3, antwort);
			psSql.setString(4, von);
			psSql.setString(5, bis);
			ResultSet myRS = psSql.executeQuery();

			int old = -1;
			ArrayList<String> strings = new ArrayList<String>();
			while (myRS.next()) {
				if (old != myRS.getInt("idBefragung")) {
					strings = new ArrayList<String>();
					strings.add("1");
					re.add(new ExcelCell(myRS.getInt("idBefragung"), strings));
					old = myRS.getInt("idBefragung");
				} else {
					strings.add("1");
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
