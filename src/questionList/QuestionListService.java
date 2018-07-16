package questionList;

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
	
	/**
	 * Loescht die gegebene Frage. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param frage
	 *            FrageErstellen: die Frage
	 * @return boolean
	 */
	// anneSehrNeu
	public static boolean deleteFrage(Frage frage) {
		Vector<Integer> antmcnr = new Vector<Integer>(); // IDs der Antworten
															// aus MC Fragen
		Vector<String> antwortenmc = new Vector<String>(); // Antworten zu MC
															// Fragen
		String statement;
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
				statement = "SELECT flags, idFreieFragen FROM FB_has_ff WHERE flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
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

				// LÃ¶schen der Relationen von Fragebogen zu MultipleChoice
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Mc WHERE idMultipleChoice=" + frage.getFrageID();
				mySQL.execute(statement);
				mySQL = null;
			} else if (frage.getArt().equals("FF")) {
				Statement mySQL = myCon.createStatement();
				statement = "SELECT flags, idFreieFragen FROM FB_has_ff where flags LIKE '%__" + frage.getFrageID()
						+ "A%'";
				ResultSet myRS = mySQL.executeQuery(statement);

				// Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
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

				// Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
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

				// LÃ¶schen der Relationen von Fragebogen zu FreieFragen
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Ff WHERE idFreieFragen=" + frage.getFrageID();
				mySQL.execute(statement);
				mySQL = null;
			}

			if (frage.getArt().equals("MC")) {
				Statement mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice FROM FB_has_MC WHERE idMultipleChoice=" + frage.getFrageID();
				ResultSet myRS = mySQL.executeQuery(statement);

				// steht ID der MC Frage nach dem LÃ¶schen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die MC Frage gelÃ¶scht werden, denn sie kommt
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
				Statement mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen FROM Fb_has_ff WHERE idFreieFragen=" + frage.getFrageID();
				ResultSet myRS = mySQL.executeQuery(statement);

				// steht ID der FF Frage nach dem LÃ¶schen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die FF Frage gelÃ¶scht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM FreieFragen WHERE idFreieFragen=" + frage.getFrageID();
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			// Antworten lÃ¶schen, wenn nicht 0-10 / ja / nein / ##### (Multiple
			// Choice Edition)
			for (short j = 0; j < antmcnr.size(); j++) {
				if (!antwortenmc.get(j).equals("0") && !antwortenmc.get(j).equals("1")
						&& !antwortenmc.get(j).equals("2") && !antwortenmc.get(j).equals("3")
						&& !antwortenmc.get(j).equals("4") && !antwortenmc.get(j).equals("5")
						&& !antwortenmc.get(j).equals("6") && !antwortenmc.get(j).equals("7")
						&& !antwortenmc.get(j).equals("8") && !antwortenmc.get(j).equals("9")
						&& !antwortenmc.get(j).equals("10") && !antwortenmc.get(j).equals("ja")
						&& !antwortenmc.get(j).equals("nein") && !antwortenmc.get(j).equals("#####")) {
					Statement mySQL = myCon.createStatement();
					statement = "DELETE FROM Antworten WHERE AntwortNr=" + antmcnr.get(j);
					mySQL.execute(statement);
					mySQL = null;
				}
			}
			
			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
