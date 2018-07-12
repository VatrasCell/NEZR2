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

import application.Datenbank;
import model.Frage;
import model.Fragebogen;
import question.QuestionService;
import application.GlobalFuncs;
import application.GlobalVars;

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
			int idstandort = -1;
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			PreparedStatement psSql = null;
			mySQL = myCon.createStatement();
			String statement = "SELECT idOrt FROM ort WHERE Ort=?";
			psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(standort));
			ResultSet myRS = psSql.executeQuery();
			Vector<Fragebogen> fragebogen = new Vector<>();

			while (myRS.next()) {
				idstandort = myRS.getInt("idOrt");
			}

			mySQL = null;
			myRS = null;

			mySQL = myCon.createStatement();
			statement = "SELECT * FROM Fragebogen WHERE idOrt='" + idstandort + "'";
			myRS = mySQL.executeQuery(statement);
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
	public static boolean copyFragebogen(Fragebogen fb) {
		int oldID = fb.getId();
		System.out.println(fb.toString());
		int newID = -1;
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			int ortID = -1;
			String statement = "SELECT idOrt FROM Ort WHERE ort=?";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setString(1, slashUnicode(fb.getOrt()));
			ResultSet myRS = psSql.executeQuery();
			if (myRS.next()) {
				ortID = myRS.getInt("idOrt");
			}
			myRS = null;
			Statement mySQL = myCon.createStatement();
			statement = "INSERT INTO fragebogen VALUE(NULL, " + GlobalFuncs.getcurDate() + ", "
					+ slashUnicode(fb.getName()) + ", FALSE, " + ortID + ", FALSE)";
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
					 */

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
			//ErrorLog.fehlerBerichtB("ERROR",
			//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
		}
		return false;
	}
}
