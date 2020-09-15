package survey;

import application.Database;
import application.GlobalVars;
import flag.FlagList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Question;
import model.QuestionType;
import model.Questionnaire;
import model.Headline;
import question.QuestionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SurveyService extends Database {
	/**
	 * Erstellt einen ArrayList aus allen Fragen, welche im ausgewählten Fragebogen
	 * sind.
	 * 
	 * @return ArrayList FrageErstellen
	 * @author Julian und Eric
	 */
	public static List<Question> getQuestions(Questionnaire fb) {
		try {
			System.out.println("FragebogenID: " + fb.getId());
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statment = "SELECT mc1.FrageMC, mc1.idMultipleChoice, Fragebogen.Datum, fb_has_mc.Position, fb_has_mc.Flags, Kategorie, Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE Fragebogen.idFragebogen="
					+ fb.getId();
			ResultSet myRS = mySQL.executeQuery(statment);

			ArrayList<String> antworten = new ArrayList<>();
			ArrayList<Integer> pos = new ArrayList<>();
			ArrayList<String> alleFragen = new ArrayList<>();
			ArrayList<Headline> ueberschriften = new ArrayList<>();

			ArrayList<Question> ffFragen = new ArrayList<>();
			ArrayList<Question> mcFragen = new ArrayList<>();

			// �berschrift MUSS MC sein...
			while (myRS.next()) {
				boolean isUeberschrift = false;
				boolean skip = false;
				String frage = "";
				frage = unslashUnicode(myRS.getString("FrageMC"));
				if (!myRS.getString("Antwort").equals("#####")) {
					alleFragen.add(frage);
				}

				if ((mcFragen.isEmpty() || !mcFragen.get(mcFragen.size() - 1).getQuestion().equals(frage))
						&& !myRS.getString("Antwort").equals("#####")) {
					Question fragenObj = new Question();
					fragenObj.setQuestion(frage);
					fragenObj.setQuestionId(myRS.getInt("idMultipleChoice"));
					fragenObj.setCategory(unslashUnicode(myRS.getString("Kategorie")));
					fragenObj.setDate(myRS.getString("Datum"));
					fragenObj.setFlags(new FlagList(myRS.getString("Flags")));
					fragenObj.setPosition(Integer.parseInt(myRS.getString("Position")));
					fragenObj.setQuestionType(QuestionType.MULTIPLE_CHOICE);
					fragenObj.setQuestionnaireId(fb.getId());

					int iii;
					for (iii = 0; iii < ueberschriften.size(); iii++) {

						if (fragenObj.getPosition() == ueberschriften.get(iii).getPosition()) {
							isUeberschrift = true;
							break;
						}
					}

					if (isUeberschrift) {
						fragenObj.setHeadline(ueberschriften.get(iii).getHeadline());
					}

					mcFragen.add(fragenObj);
				} else {
					if (myRS.getString("Antwort").equals("#####")) {
						skip = true;
						ueberschriften.add(new Headline(Integer.parseInt(myRS.getString("Position")),
								unslashUnicode(myRS.getString("FrageMC"))));
					}
				}

				if ((mcFragen.isEmpty() || mcFragen.get(mcFragen.size() - 1).getQuestion().equals(frage)) && !skip) {
					antworten.add(unslashUnicode(myRS.getString("Antwort")));
				}
			}

			myRS = null;
			mySQL = null;
			mySQL = myCon.createStatement();
			statment = "SELECT ff1.FrageFF, ff1.idFreieFragen, Fragebogen.Datum, fb_has_ff.Position, fb_has_ff.Flags, Kategorie FROM fragebogen JOIN fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen JOIN freiefragen ff1 ON fb_has_ff.idFreieFragen=ff1.idFreieFragen JOIN kategorie ON ff1.idKategorie=kategorie.idKategorie WHERE Fragebogen.idFragebogen="
					+ fb.getId();
			myRS = mySQL.executeQuery(statment);

			while (myRS.next()) {
				boolean isUeberschrift = false;
				boolean skip = false;
				String frage = unslashUnicode(myRS.getString("FrageFF"));
				alleFragen.add(frage);
				if ((ffFragen.isEmpty() || 
						!ffFragen.get(ffFragen.size() - 1).getQuestion().equals(frage))/* && !myRS.getString("Antwort").equals("#####")*/) {
					Question fragenObj = new Question();
					fragenObj.setQuestion(frage);
					fragenObj.setQuestionId(myRS.getInt("idFreieFragen"));
					fragenObj.setCategory(unslashUnicode(myRS.getString("Kategorie")));
					fragenObj.setDate(myRS.getString("Datum"));
					fragenObj.setFlags(new FlagList(myRS.getString("Flags")));
					fragenObj.setPosition(Integer.parseInt(myRS.getString("Position")));
					fragenObj.setQuestionType(QuestionType.SHORT_ANSWER);
					fragenObj.setQuestionnaireId(fb.getId());

					int iii;
					for (iii = 0; iii < ueberschriften.size(); iii++) {

						if (fragenObj.getPosition() == ueberschriften.get(iii).getPosition()) {
							isUeberschrift = true;
							break;
						}
					}

					if (isUeberschrift) {
						fragenObj.setHeadline(ueberschriften.get(iii).getHeadline());
					}

					ffFragen.add(fragenObj);
				} else {
					if (myRS.getString("Antwort").equals("#####")) {
						skip = true;
						ueberschriften.add(new Headline(Integer.parseInt(myRS.getString("Position")),
								unslashUnicode(myRS.getString("FrageMC"))));
					}
				}

				if ((ffFragen.isEmpty() || ffFragen.get(ffFragen.size() - 1).getQuestion().equals(frage)) && !skip) {
					antworten.add("");
				}
			}

			ArrayList<Question> fragen = new ArrayList<Question>();

			for (int z = 0; z < mcFragen.size(); z++) {
				fragen.add(mcFragen.get(z));
			}

			for (int z = 0; z < ffFragen.size(); z++) {
				fragen.add(ffFragen.get(z));
			}

			alleFragen.add("DUMMY");

			int count = 1;
			for (int i = 0; i < alleFragen.size() - 1; i++) {

				if (alleFragen.get(i).equals(alleFragen.get(i + 1))) {
					count++;
				} else {
					pos.add(count);
					count = 1;
				}
			}
			int c = 0;

			for (int i = 0; i < pos.size(); i++) {
				for (int j = 0; j < pos.get(i); j++) {
					try {
						fragen.get(i).addAnswerOption(antworten.get(i + j + c));
					} catch (Exception e) {
						/*ErrorLog.fehlerBerichtB("ERROR",
								Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
								e.getMessage());*/
					}
				}
				c += pos.get(i) - 1;
			}

			fragen.sort(null);

			myCon.close();

			return fragen;
		} catch (SQLException e) {
			/*ErrorLog.fehlerBerichtB("ERROR",
					Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());*/
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Speichert alle durch den Benutzer gegebenen Antworten in die Datenbank.
	 * 
	 * @param fragen
	 *            ArrayList ArrayList FrageErstellen: alle Fragen des Fragebogens
	 * @author Julian und Eric
	 */
	public static void saveUmfrage(List<ArrayList<Question>> fragen) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);

			String statement = "INSERT INTO Befragung VALUES(NULL, CURDATE(), ?)";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setInt(1, fragen.get(0).get(0).getQuestionnaireId());
			psSql.executeUpdate();

			int b_id = 0;

			Statement mySQL = myCon.createStatement();
			statement = "SELECT MAX(idBefragung) FROM Befragung";
			ResultSet myRS = mySQL.executeQuery(statement);
			if (myRS.next()) {
				b_id = myRS.getInt("MAX(idBefragung)");

				for (int i = 0; i < fragen.size(); i++) {
					ArrayList<Question> panel = fragen.get(i);
					for (Question question : panel) {
						if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
							if (question.getAnswer().size() > 0) {
								for (String antwort : question.getAnswer()) {
									antwort = antwort.replaceAll("<.*>", "");
									myRS = null;
									mySQL = null;
									mySQL = myCon.createStatement();
									statement = "SELECT B_has_MCid FROM B_has_MC WHERE idBefragung=" + b_id
											+ " AND idMultipleChoice=" + question.getQuestionId() + " AND AntwortNr="
											+ QuestionService.getAntwortID(antwort);
									myRS = mySQL.executeQuery(statement);
									if (!myRS.next()) {
										myRS = null;
										mySQL = null;
										mySQL = myCon.createStatement();
										statement = "INSERT INTO B_has_MC VALUES(NULL," + b_id + ", "
												+ question.getQuestionId() + ", " + QuestionService.getAntwortID(antwort) + ")";
										mySQL.executeUpdate(statement);
									}
								}
							}
						} else if (question.getAnswer().size() > 0) {
							for (String antwort : question.getAnswer()) {
								antwort = antwort.replaceAll("<.*>", "");
								myRS = null;
								mySQL = null;
								mySQL = myCon.createStatement();
								statement = "SELECT B_has_FFid FROM B_has_FF WHERE idBefragung=" + b_id
										+ " AND idFreieFragen=" + question.getQuestionId() + " AND AntwortNr="
										+ QuestionService.getAntwortID(antwort);
								myRS = mySQL.executeQuery(statement);
								if (!myRS.next()) {
									myRS = null;
									mySQL = null;
									mySQL = myCon.createStatement();
									statement = "INSERT INTO B_has_FF VALUES(NULL," + b_id + ", " + question.getQuestionId()
											+ ", " + QuestionService.getAntwortID(antwort) + ")";
									mySQL.executeUpdate(statement);
								}
							}
						}
					}
				}
			}
			myCon.close();
		} catch (SQLException e) {
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
		}
		resetFragebogen();
	}
	
	/**
	 * Setzt den ArrayList "fragenJePanel" zur�ck.
	 */
	public static void resetFragebogen() {
		
		for(ArrayList<Question> fragen : GlobalVars.questionsPerPanel) {
			for(Question question : fragen) {
				question.setAnswer(null);
				for(CheckBox checkbox : question.getAnswersMC()) {
					checkbox.setSelected(false);
				}
				for(TextField textField : question.getAnswersFF()) {
					textField.setText("");
				}
				for(ListView<String> list : question.getAnswersLIST()) {
					list.getItems().clear();
				}
			}
		}
	}
}
