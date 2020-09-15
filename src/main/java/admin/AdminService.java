package admin;

import application.Database;
import application.GlobalFuncs;
import application.GlobalVars;
import flag.SymbolType;
import model.Question;
import model.QuestionType;
import model.Questionnaire;
import question.QuestionService;
import survey.SurveyService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static application.SqlStatement.SQL_COLUMN_LABEL_MAX_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_CREATE_QUESTIONNAIRE;
import static application.SqlStatement.SQL_GET_LAST_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_GET_LOCATION_ID;
import static application.SqlStatement.SQL_IS_QUESTIONNAIRE_FINAL;
import static application.SqlStatement.SQL_RENAME_QUESTIONNAIRE;
import static application.SqlStatement.SQL_SET_QUESTIONNAIRE_FINAL_STATUS;

public class AdminService extends Database {
	
	/**
	 * Gibt alle Frageboegen eines Standortes zurueck
	 * 
	 * @param standort
	 *            String: der Standort
	 * @return ArrayList FragebogenDialog aller Frageboegen des Standortes
	 * @author Eric
	 */
	public static ArrayList<Questionnaire> getFragebogen(String standort) {
		try {
			ArrayList<Questionnaire> questionnaire = new ArrayList<>();
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			int idstandort = getLocationId(standort);

			Statement mySQL = myCon.createStatement();
			String statement = "SELECT * FROM Fragebogen WHERE idOrt='" + idstandort + "'";
			ResultSet myRS = mySQL.executeQuery(statement);
			//System.out.println(idstandort);
			//System.out.println(myRS.toString());
			while (myRS.next()) {
				Questionnaire fb = new Questionnaire();
				fb.setName(unslashUnicode(myRS.getString("name")));
				fb.setDate(myRS.getString("datum"));
				fb.setOrt(standort);
				fb.setActive(myRS.getBoolean("aktiviert")); // anneNeu
				fb.setId(myRS.getInt("idFragebogen")); // anneNeuFlorian
				fb.setFinal(myRS.getBoolean("final"));
				questionnaire.add(fb);
			}
			myCon.close();
			return questionnaire;
		} catch (SQLException e) {
			e.printStackTrace();
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return null;
	}
	
	/**
	 * Setzt den gegebenen Fragebogen auf aktiv und deaktiviert alle anderen
	 * Frageboegen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Anne
	 */
	public static boolean activateFragebogen(Questionnaire fb) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET aktiviert=TRUE WHERE idFragebogen=" + fb.getId();
			mySQL.execute(statement);

			mySQL = null;
			mySQL = myCon.createStatement();
			statement = "UPDATE fragebogen SET aktiviert=FALSE WHERE NOT idFragebogen=" + fb.getId();
			mySQL.execute(statement);

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
	 * Deaktiviert den gegebenen Fragebogen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 */
	public static boolean disableFragebogen(Questionnaire fb) {
		try {
			// anneSuperNeu
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET aktiviert=FALSE WHERE idFragebogen=" + fb.getId();
			mySQL.execute(statement);

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
	 * Kopiert den gegebenen Fragebogen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 */
	/*
	public static boolean copyFragebogen(Fragebogen fb, String location) {
		int oldID = fb.getId();
		System.out.println(fb.toString());
		int newID = -1;
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			int ortID = getStandortId(location);
			Statement mySQL = myCon.createStatement();
		 	String statement = "INSERT INTO fragebogen VALUES(NULL, '" + GlobalFuncs.getcurDate() + "', '"
					+ slashUnicode(fb.getName()) + "', FALSE, " + ortID + ", FALSE)";
			mySQL.execute(statement);
			mySQL = null;
			
			mySQL = myCon.createStatement();
			statement = "SELECT MAX(idFragebogen) FROM fragebogen";
			ResultSet myRS = mySQL.executeQuery(statement);
			if (myRS.next()) {
				newID = myRS.getInt("MAX(idFragebogen)");

				mySQL = null;
				myRS = null;

				mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice, Position, Flags FROM Fb_has_MC WHERE idFragebogen=" + oldID;
				myRS = mySQL.executeQuery(statement);

				ArrayList<ArrayList<String>> vecs = new ArrayList<ArrayList<String>>();
				while (myRS.next()) {
					ArrayList<String> vec = new ArrayList<String>();
					vec.add(myRS.getInt("idMultipleChoice") + "");
					vec.add(myRS.getInt("Position") + "");
					vec.add(myRS.getString("Flags"));
					vecs.add(vec);
				}

				ArrayList<Frage> mcFragen = new ArrayList<>();

				for (ArrayList<String> data : vecs) {
					Frage mcFrage = new Frage();
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
						String frage = slashUnicode(QuestionService.duplicateFrage(unslashUnicode(myRS2.getString("FrageMC"))));
						if (frage.contains("\\")) {
							frage = frage.replaceAll("\\\\", "\\\\\\\\");
						}
						statement = "INSERT INTO MultipleChoice VALUES(NULL, '" + frage + "', " + katID + ")";
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
					statement = "INSERT INTO Fb_has_MC VALUES(NULL, " + newID + ", " + newFragenID + ", " + position
							+ ", '" + flags + "')";
					mcFrage.setPosition(position);
					mcFrage.setFlags(new FlagList(flags));
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
						statement = "INSERT INTO Mc_has_A VALUES(NULL, " + newFragenID + ", " + myRS4.getInt("AntwortNr")
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
				ArrayList<ArrayList<String>> vecs2 = new ArrayList<ArrayList<String>>();
				while (myRS.next()) {
					ArrayList<String> vec = new ArrayList<String>();
					vec.add(myRS.getInt("idFreieFragen") + "");
					vec.add(myRS.getInt("Position") + "");
					vec.add(myRS.getString("Flags"));
					vecs2.add(vec);
				}

				ArrayList<Frage> ffFragen = new ArrayList<>();
				for (ArrayList<String> data : vecs2) {
					Frage ffFrage = new Frage();
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
						String frage = slashUnicode(QuestionService.duplicateFrage(unslashUnicode(myRS2.getString("FrageFF"))));
						if (frage.contains("\\")) {
							System.out.println(frage);
							frage = frage.replaceAll("\\\\", "\\\\\\\\");
						}
						statement = "INSERT INTO FreieFragen VALUES(NULL, '" + frage + "', " + katID + ")";
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

					
					/* Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
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
					statement = "INSERT INTO Fb_has_FF VALUES(NULL, " + newID + ", " + newFragenID + ", " + position
							+ ", '" + flags + "')";
					ffFrage.setFrageID(newFragenID);
					ffFrage.setFragebogenID(newID);
					ffFrage.setPosition(position);
					ffFrage.setFlags(new FlagList(flags));
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
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}
	*/
	public static boolean copyQuestionnaire(Questionnaire fb, String location) {
		fb.setId(createQuestionnaire(fb.getName(), location));
		List<Question> questions = SurveyService.getQuestions(fb);
		for (Question question : Objects.requireNonNull(questions)) {
			if(question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
				QuestionService.saveFreieFrage(fb, question);
			} else {
				if(question.getFlags().is(SymbolType.B)) {
					QuestionService.saveBewertungsfrage(fb, question);
				} else {
					ArrayList<Integer> antIds = new ArrayList<>();
					for (String answer : question.getAnswerOptions()) {
						antIds.add(QuestionService.getAntwortID(answer));
					} 
					QuestionService.saveMC(fb, question, antIds);
				}
			}
		}
		//TODO
		return true;
	}

	public static boolean deleteQuestionnaire(Questionnaire fb) {
		ArrayList<Integer> mcQuestionIds = new ArrayList<>(); // IDs der MC Fragen
		ArrayList<Integer> ffQuestionIds = new ArrayList<>(); // IDs der Freien Fragen
		ArrayList<Integer> mcQuestionAnswerIds = new ArrayList<>(); // IDs der Antworten
															// aus MC Fragen
		ArrayList<String> mcQuestionAnswers = new ArrayList<>(); // Antworten zu MC
															// Fragen

		try {
			// Multiple Choice ids mit Antworten
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "SELECT fb_has_mc.idMultipleChoice, Antworten.AntwortNr, Antworten.Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN "
					+ "multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten "
					+ "ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE fragebogen.idFragebogen=" + fb.getId();
			ResultSet myRS = mySQL.executeQuery(statement);

			while (myRS.next()) {
				if (!mcQuestionAnswerIds.isEmpty()) {
					for (int i = 0; i < mcQuestionAnswerIds.size(); i++) {
						if (myRS.getInt("AntwortNr") != mcQuestionAnswerIds.get(i)
								&& !myRS.getString("Antwort").equals(mcQuestionAnswers.get(i))) {
							mcQuestionAnswerIds.add(myRS.getInt("AntwortNr"));
							mcQuestionAnswers.add(myRS.getString("Antwort"));
							break;
						}
					}
				} else {
					mcQuestionAnswerIds.add(myRS.getInt("AntwortNr"));
					mcQuestionAnswers.add(myRS.getString("Antwort"));
				}
				mcQuestionIds.add(myRS.getInt("idMultipleChoice"));
			}
			myRS = null;
			mySQL = null;

			mySQL = myCon.createStatement();
			statement = "SELECT fb_has_mc.idMultipleChoice, Antworten.AntwortNr, Antworten.Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN "
					+ "multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON "
					+ "mc_has_a.AntwortNr=antworten.AntwortNr WHERE fragebogen.idFragebogen!=" + fb.getId();
			myRS = mySQL.executeQuery(statement);

			// Antworten, die noch in einem anderen Fragebogen vorkommen, aus
			// dem ArrayList entfernen
			while (myRS.next()) {
				for (int i = 0; i < mcQuestionAnswerIds.size(); i++) {
					if (myRS.getInt("AntwortNr") == mcQuestionAnswerIds.get(i)) {
						mcQuestionAnswerIds.remove(i);
					}
				}
			}
			myRS = null;
			mySQL = null;

			// Freie Fragen mit id
			mySQL = myCon.createStatement();
			statement = "SELECT fb_has_ff.idFreieFragen FROM fb_has_ff WHERE idFragebogen=" + fb.getId();
			myRS = mySQL.executeQuery(statement);

			while (myRS.next()) {
				ffQuestionIds.add(myRS.getInt("idFreieFragen"));
			}
			myRS = null;
			mySQL = null;

			// LÃ¶schen der Relationen von Fragebogen zu MultipleChoice
			for (short i = 0; i < mcQuestionIds.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Mc WHERE idMultipleChoice=" + mcQuestionIds.get(i) + " AND idFragebogen="
						+ fb.getId();
				mySQL.execute(statement);
				mySQL = null;
			}

			// LÃ¶schen der Relationen von Fragebogen zu FreieFragen
			for (short i = 0; i < ffQuestionIds.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Ff WHERE idFreieFragen=" + ffQuestionIds.get(i) + " AND idFragebogen="
						+ fb.getId();
				mySQL.execute(statement);
				mySQL = null;
			}

			for (short i = 0; i < mcQuestionIds.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice FROM FB_has_MC WHERE idMultipleChoice=" + mcQuestionIds.get(i);
				myRS = mySQL.executeQuery(statement);

				// steht ID der MC Frage nach dem LÃ¶schen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die MC Frage gelÃ¶scht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM MC_has_A WHERE idMultipleChoice=" + mcQuestionIds.get(i);
					mySQL.execute(statement);
					mySQL = null;

					mySQL = myCon.createStatement();
					statement = "DELETE FROM MultipleChoice WHERE idMultipleChoice=" + mcQuestionIds.get(i);
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			for (short i = 0; i < ffQuestionIds.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "SELECT idFreieFragen FROM Fb_has_ff WHERE idFreieFragen=" + ffQuestionIds.get(i);
				myRS = mySQL.executeQuery(statement);
				// steht ID der FF Frage nach dem LÃ¶schen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die FF Frage gelÃ¶scht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM FreieFragen WHERE idFreieFragen=" + ffQuestionIds.get(i);
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			// Antworten lÃ¶schen, wenn nicht 0-10 / ja / nein / ##### (Multiple
			// Choice Edition)
			for (short j = 0; j < mcQuestionAnswerIds.size(); j++) {
				if (!mcQuestionAnswers.get(j).equals("0") && !mcQuestionAnswers.get(j).equals("1")
						&& !mcQuestionAnswers.get(j).equals("2") && !mcQuestionAnswers.get(j).equals("3")
						&& !mcQuestionAnswers.get(j).equals("4") && !mcQuestionAnswers.get(j).equals("5")
						&& !mcQuestionAnswers.get(j).equals("6") && !mcQuestionAnswers.get(j).equals("7")
						&& !mcQuestionAnswers.get(j).equals("8") && !mcQuestionAnswers.get(j).equals("9")
						&& !mcQuestionAnswers.get(j).equals("10") && !mcQuestionAnswers.get(j).equals("ja")
						&& !mcQuestionAnswers.get(j).equals("nein") && !mcQuestionAnswers.get(j).equals("#####")) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM Antworten WHERE AntwortNr=" + mcQuestionAnswerIds.get(j);
					mySQL.execute(statement);
					mySQL = null;
				}
			}

			mySQL = null;

			mySQL = myCon.createStatement();
			statement = "DELETE FROM fragebogen WHERE idFragebogen=" + fb.getId();
			mySQL.execute(statement);

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

	public static boolean renameQuestionnaire(Questionnaire fb) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			PreparedStatement psSql = myCon.prepareStatement(SQL_RENAME_QUESTIONNAIRE);
			psSql.setString(1, slashUnicode(fb.getName()));
			psSql.setInt(2, fb.getId());
			psSql.execute();

			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean setFinal(Questionnaire fb) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			PreparedStatement psSql = myCon.prepareStatement(SQL_SET_QUESTIONNAIRE_FINAL_STATUS);
			psSql.setBoolean(1, true);
			psSql.setInt(2, fb.getId());
			psSql.execute();

			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean setUnFinal(Questionnaire fb) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			PreparedStatement psSql = myCon.prepareStatement(SQL_SET_QUESTIONNAIRE_FINAL_STATUS);
			psSql.setBoolean(1, false);
			psSql.setInt(2, fb.getId());
			psSql.execute();

			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isFinal(Questionnaire fb) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			PreparedStatement psSql = myCon.prepareStatement(SQL_IS_QUESTIONNAIRE_FINAL);
			psSql.setInt(1, fb.getId());
			ResultSet myRS = psSql.executeQuery();

			if (myRS.next()) {
				myCon.close();
				return true;
			} else {
				myCon.close();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static int createQuestionnaire(String name) {
		return createQuestionnaire(name, GlobalVars.location);
	}
	
	public static int createQuestionnaire(String name, String location) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_QUESTIONNAIRE);
			psSql.setString(1, GlobalFuncs.getCurrentDate());
			psSql.setString(2, name);
			psSql.setInt(3, getLocationId(location));
			psSql.execute();

			psSql = myCon.prepareStatement(SQL_GET_LAST_QUESTIONNAIRE_ID);
			ResultSet myRS = psSql.executeQuery();
			int id = -1;
			if (myRS.next()) {
				id = myRS.getInt(SQL_COLUMN_LABEL_MAX_QUESTIONNAIRE_ID);
			}
			
			myCon.close();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}	
	
	public static int getLocationId(String location) {
		Connection myCon;
		int ortID = -1;
		try {
			myCon = DriverManager.getConnection(url, user, pwd);
			PreparedStatement psSql = myCon.prepareStatement(SQL_GET_LOCATION_ID);
			psSql.setString(1, slashUnicode(location));
			ResultSet myRS = psSql.executeQuery();
			if (myRS.next()) {
				ortID = myRS.getInt("idOrt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ortID;
	}
}
