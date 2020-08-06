package questionList;

import application.Database;
import flag.FlagList;
import model.Question;
import model.Questionnaire;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionListService extends Database {
	/**
	 * Gibt alle Ueberschriften des gegebenen Fragebogen zurueck.
	 * 
	 * @param fb
	 *            FrageobgenDialog: der Fragebogen
	 * @return ArrayList FrageErstellen
	 * @author Eric
	 */
	public static ArrayList<Question> getUeberschriften(Questionnaire fb) {
		ArrayList<Question> ueberschriften = new ArrayList<>();
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statment = "SELECT mc1.FrageMC, mc1.idMultipleChoice, Fragebogen.Datum, fb_has_mc.Position, fb_has_mc.Flags, Kategorie, Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE Fragebogen.idFragebogen="
					+ fb.getId() + " AND antwort='#####'";
			ResultSet myRS = mySQL.executeQuery(statment);
			while (myRS.next()) {
				Question question = new Question();
				question.setQuestion(unslashUnicode(myRS.getString("FrageMC")));
				question.setQuestionId(myRS.getInt("idMultipleChoice"));
				question.setCategory(unslashUnicode(myRS.getString("Kategorie")));
				question.setDate(myRS.getString("Datum"));
				question.setFlags(new FlagList());
				question.setPosition(Integer.parseInt(myRS.getString("Position")));
				question.setQuestionType("MC");
				question.setQuestionnaireId(fb.getId());
				question.addAntwort_moeglichkeit(myRS.getString("Antwort"));
				ueberschriften.add(question);
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
	 * @param question
	 *            FrageErstellen: die Frage
	 * @return boolean
	 */
	// anneSehrNeu
	public static boolean deleteFrage(Question question) {
		ArrayList<Integer> antmcnr = new ArrayList<Integer>(); // IDs der Antworten
															// aus MC Fragen
		ArrayList<String> antwortenmc = new ArrayList<String>(); // Antworten zu MC
															// Fragen
		String statement;
		int start = -1;
		int end = -1;

		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			if (question.getQuestionType().equals("MC")) {
				Statement mySQL = myCon.createStatement();
				statement = "SELECT flags, idMultipleChoice FROM FB_has_mc where flags LIKE '%__" + question.getQuestionId()
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
				statement = "SELECT flags, idFreieFragen FROM FB_has_ff WHERE flags LIKE '%__" + question.getQuestionId()
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
						+ "ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE mc.idmultiplechoice=" + question.getQuestionId();
				myRS = mySQL.executeQuery(statement);

				while (myRS.next()) {
					if (!antmcnr.isEmpty()) {
						for (int i = 0; i < antmcnr.size(); i++) {
							if (myRS.getInt("AntwortNr") != antmcnr.get(i)
									&& !myRS.getString("Antwort").equals(antwortenmc.get(i))) {
								antmcnr.add(myRS.getInt("AntwortNr"));
								antwortenmc.add(myRS.getString("Antwort"));
							}
						}
					} else {
						antmcnr.add(myRS.getInt("AntwortNr"));
						antwortenmc.add(myRS.getString("Antwort"));
					}
				}

				// Antworten, die noch in einem anderen Fragebogen vorkommen,
				// aus dem ArrayList entfernen
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
				statement = "DELETE FROM Fb_has_Mc WHERE idMultipleChoice=" + question.getQuestionId();
				mySQL.execute(statement);
				mySQL = null;
			} else if (question.getQuestionType().equals("FF")) {
				Statement mySQL = myCon.createStatement();
				statement = "SELECT flags, idFreieFragen FROM FB_has_ff where flags LIKE '%__" + question.getQuestionId()
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
				statement = "SELECT flags, idmultiplechoice FROM FB_has_mc where flags LIKE '%__" + question.getQuestionId()
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
				statement = "DELETE FROM Fb_has_Ff WHERE idFreieFragen=" + question.getQuestionId();
				mySQL.execute(statement);
				mySQL = null;
			}

			if (question.getQuestionType().equals("MC")) {
				Statement mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice FROM FB_has_MC WHERE idMultipleChoice=" + question.getQuestionId();
				ResultSet myRS = mySQL.executeQuery(statement);

				// steht ID der MC Frage nach dem LÃ¶schen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die MC Frage gelÃ¶scht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM MC_has_A WHERE idMultipleChoice=" + question.getQuestionId();
					mySQL.execute(statement);
					mySQL = null;

					mySQL = myCon.createStatement();
					statement = "DELETE FROM MultipleChoice WHERE idMultipleChoice=" + question.getQuestionId();
					mySQL.execute(statement);
					mySQL = null;
				}
			} else if (question.getQuestionType().equals("FF")) {
				Statement mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen FROM Fb_has_ff WHERE idFreieFragen=" + question.getQuestionId();
				ResultSet myRS = mySQL.executeQuery(statement);

				// steht ID der FF Frage nach dem LÃ¶schen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die FF Frage gelÃ¶scht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM FreieFragen WHERE idFreieFragen=" + question.getQuestionId();
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
