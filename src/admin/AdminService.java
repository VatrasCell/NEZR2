package admin;

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
import model.Frage;
import model.Fragebogen;
import question.QuestionService;
import application.GlobalFuncs;
import application.GlobalVars;
import flag.FlagList;

public class AdminService extends Datenbank {
	
	/**
	 * Gibt alle Frageboegen eines Standortes zurueck
	 * 
	 * @param standort
	 *            String: der Standort
	 * @return Vector FragebogenDialog aller Frageboegen des Standortes
	 * @author Eric
	 */
	public static Vector<Fragebogen> getFragebogen(String standort) {
		try {
			Vector<Fragebogen> fragebogen = new Vector<>();
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			int idstandort = getStandortId(standort);

			Statement mySQL = myCon.createStatement();
			String statement = "SELECT * FROM Fragebogen WHERE idOrt='" + idstandort + "'";
			ResultSet myRS = mySQL.executeQuery(statement);
			//System.out.println(idstandort);
			//System.out.println(myRS.toString());
			while (myRS.next()) {
				Fragebogen fb = new Fragebogen();
				fb.setName(unslashUnicode(myRS.getString("name")));
				fb.setDate(myRS.getString("datum"));
				fb.setOrt(standort);
				fb.setActiv(myRS.getBoolean("aktiviert")); // anneNeu
				fb.setId(myRS.getInt("idFragebogen")); // anneNeuFlorian
				fb.setFinal(myRS.getBoolean("final"));
				fragebogen.add(fb);
			}
			myCon.close();
			return fragebogen;
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
	public static boolean updateFragebogen(Fragebogen fb) {
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
	public static boolean disableFragebogen(Fragebogen fb) {
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
	public static boolean copyFragebogen(Fragebogen fb, String ort) {
		int oldID = fb.getId();
		System.out.println(fb.toString());
		int newID = -1;
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			int ortID = getStandortId(ort);
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

				Vector<Vector<String>> vecs = new Vector<Vector<String>>();
				while (myRS.next()) {
					Vector<String> vec = new Vector<String>();
					vec.add(myRS.getInt("idMultipleChoice") + "");
					vec.add(myRS.getInt("Position") + "");
					vec.add(myRS.getString("Flags"));
					vecs.add(vec);
				}

				Vector<Frage> mcFragen = new Vector<>();

				for (Vector<String> data : vecs) {
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
				Vector<Vector<String>> vecs2 = new Vector<Vector<String>>();
				while (myRS.next()) {
					Vector<String> vec = new Vector<String>();
					vec.add(myRS.getInt("idFreieFragen") + "");
					vec.add(myRS.getInt("Position") + "");
					vec.add(myRS.getString("Flags"));
					vecs2.add(vec);
				}

				Vector<Frage> ffFragen = new Vector<>();
				for (Vector<String> data : vecs2) {
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
	public static boolean copyFragebogen(Fragebogen fb, String ort) {
		//TODO
		return false;
	}
	
	/**
	 * Loescht den gegebenen Fragebogen. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Anne
	 */
	public static boolean deleteFragebogen(Fragebogen fb) {
		Vector<Integer> idsmc = new Vector<Integer>(); // IDs der MC Fragen
		Vector<Integer> idsff = new Vector<Integer>(); // IDs der Freien Fragen
		Vector<Integer> antmcnr = new Vector<Integer>(); // IDs der Antworten
															// aus MC Fragen
		Vector<String> antwortenmc = new Vector<String>(); // Antworten zu MC
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
					+ "mc_has_a.AntwortNr=antworten.AntwortNr WHERE fragebogen.idFragebogen!=" + fb.getId();
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
			statement = "SELECT fb_has_ff.idFreieFragen FROM fb_has_ff WHERE idFragebogen=" + fb.getId();
			myRS = mySQL.executeQuery(statement);

			while (myRS.next()) {
				idsff.add(myRS.getInt("idFreieFragen"));
			}
			myRS = null;
			mySQL = null;

			// LÃ¶schen der Relationen von Fragebogen zu MultipleChoice
			for (short i = 0; i < idsmc.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Mc WHERE idMultipleChoice=" + idsmc.get(i) + " AND idFragebogen="
						+ fb.getId();
				mySQL.execute(statement);
				mySQL = null;
			}

			// LÃ¶schen der Relationen von Fragebogen zu FreieFragen
			for (short i = 0; i < idsff.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "DELETE FROM Fb_has_Ff WHERE idFreieFragen=" + idsff.get(i) + " AND idFragebogen="
						+ fb.getId();
				mySQL.execute(statement);
				mySQL = null;
			}

			for (short i = 0; i < idsmc.size(); i++) {
				mySQL = myCon.createStatement();
				statement = "SELECT idMultipleChoice FROM FB_has_MC WHERE idMultipleChoice=" + idsmc.get(i);
				myRS = mySQL.executeQuery(statement);

				// steht ID der MC Frage nach dem LÃ¶schen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die MC Frage gelÃ¶scht werden, denn sie kommt
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
				// steht ID der FF Frage nach dem LÃ¶schen der Relation zum
				// jeweiligen Fragebogen immernoch wo anders?
				// Wenn nicht, darf die FF Frage gelÃ¶scht werden, denn sie kommt
				// in keinem anderen Fragebogen vor
				if (!myRS.next()) {
					mySQL = myCon.createStatement();
					statement = "DELETE FROM FreieFragen WHERE idFreieFragen=" + idsff.get(i);
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
					mySQL = myCon.createStatement();
					statement = "DELETE FROM Antworten WHERE AntwortNr=" + antmcnr.get(j);
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
	
	/**
	 * Benennt den gegebenen Fragebogen um. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 */
	public static boolean renameFragebogen(Fragebogen fb) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET Name='" + slashUnicode(fb.getName())
					+ "' WHERE idFragebogen=" + fb.getId();
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
	 * Setzt den gegebenen Fragebogen auf final. Gibt bei Erfolg TRUE zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 */
	public static boolean setFinal(Fragebogen fb) {
		try {
			// anneSuperNeu
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET final=TRUE WHERE idFragebogen=" + fb.getId();
			mySQL.execute(statement);

			mySQL = null;
			myCon.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Setzt den gegebenen Fragebogen auf nicht final. Gibt bei Erfolg TRUE
	 * zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog
	 * @return boolean
	 * @author Eric
	 */
	public static boolean setUnFinal(Fragebogen fb) {
		try {
			// anneSuperNeu
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "UPDATE fragebogen SET final=FALSE WHERE idFragebogen=" + fb.getId();
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
	 * Prueft, ob der gegebenen Fragebogen final ist. Gibt bei Erfolg TRUE
	 * zurueck.
	 * 
	 * @param fb
	 *            FragebogenDialog: der Fragebogen
	 * @return boolean
	 * @author Eric
	 */
	public static boolean isFinal(Fragebogen fb) {
		try {
			// anneSuperNeu
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "SELECT idFragebogen FROM fragebogen WHERE final=TRUE AND idFragebogen="
					+ fb.getId();
			ResultSet myRS = mySQL.executeQuery(statement);

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
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}
	
	public static boolean createFragebogen(String name) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statement = "SELECT Name FROM fragebogen WHERE Name='" + name + "'";
			ResultSet myRS = mySQL.executeQuery(statement);

			if (myRS.next()) {
				Notifications.create().title("Fragebogen erstellen").text("Ein Fragebogen mit dem Namen existiert bereits!").showError();
			} else {
				mySQL = null;
				myRS = null;
				mySQL = myCon.createStatement();
				statement = "INSERT INTO fragebogen VALUES(NULL, '" + GlobalFuncs.getcurDate() + "', '" + name + "', FALSE, "
						+ getStandortId(GlobalVars.standort) + ", FALSE)";
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
	
	public static int getStandortId(String ort) {
		Connection myCon;
		int ortID = -1;
		try {
			myCon = DriverManager.getConnection(url, user, pwd);			
			String statement = "SELECT idOrt FROM Ort WHERE ort=?";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(ort));
			ResultSet myRS = psSql.executeQuery();
			if (myRS.next()) {
				ortID = myRS.getInt("idOrt");
			}
			myRS = null;	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ortID;
	}
}
