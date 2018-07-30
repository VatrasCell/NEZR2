package survey;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import application.Datenbank;
import application.GlobalVars;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Frage;
import model.Fragebogen;
import model.Ueberschrift;
import question.QuestionService;

public class SurveyService extends Datenbank {
	/**
	 * Erstellt einen Vector aus allen Fragen, welche im ausgewählten Fragebogen
	 * sind.
	 * 
	 * @return Vector FrageErstellen
	 * @author Julian und Eric
	 */
	public static Vector<Frage> getFragen(Fragebogen fb) {
		try {
			System.out.println("FragebogenID: " + fb.getId());
			Connection myCon = DriverManager.getConnection(url, user, pwd);
			Statement mySQL = myCon.createStatement();
			String statment = "SELECT mc1.FrageMC, mc1.idMultipleChoice, Fragebogen.Datum, fb_has_mc.Position, fb_has_mc.Flags, Kategorie, Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE Fragebogen.idFragebogen="
					+ fb.getId();
			ResultSet myRS = mySQL.executeQuery(statment);

			Vector<String> antworten = new Vector<String>();
			Vector<Integer> pos = new Vector<Integer>();
			Vector<String> alleFragen = new Vector<String>();
			Vector<Ueberschrift> ueberschriften = new Vector<Ueberschrift>();

			Vector<Frage> ffFragen = new Vector<Frage>();
			Vector<Frage> mcFragen = new Vector<Frage>();

			// �berschrift MUSS MC sein...
			while (myRS.next()) {
				boolean isUeberschrift = false;
				boolean skip = false;
				String frage = "";
				frage = unslashUnicode(myRS.getString("FrageMC"));
				if (!myRS.getString("Antwort").equals("#####")) {
					alleFragen.addElement(frage);
				}

				if ((mcFragen.isEmpty() || !mcFragen.get(mcFragen.size() - 1).getFrage().equals(frage))
						&& !myRS.getString("Antwort").equals("#####")) {
					Frage fragenObj = new Frage();
					fragenObj.setFrage(frage);
					fragenObj.setFrageID(myRS.getInt("idMultipleChoice"));
					fragenObj.setKategorie(unslashUnicode(myRS.getString("Kategorie")));
					fragenObj.setDatum(myRS.getString("Datum"));
					fragenObj.setFlags(myRS.getString("Flags"));
					fragenObj.setPosition(Integer.parseInt(myRS.getString("Position")));
					fragenObj.setArt("MC");
					fragenObj.setFragebogenID(fb.getId());

					int iii;
					for (iii = 0; iii < ueberschriften.size(); iii++) {

						if (fragenObj.getPosition() == ueberschriften.get(iii).getPostition()) {
							isUeberschrift = true;
							break;
						}
					}

					if (isUeberschrift) {
						fragenObj.setUeberschrift(ueberschriften.get(iii).getUeberschrift());
					}

					mcFragen.addElement(fragenObj);
				} else {
					if (myRS.getString("Antwort").equals("#####")) {
						skip = true;
						ueberschriften.addElement(new Ueberschrift(Integer.parseInt(myRS.getString("Position")),
								unslashUnicode(myRS.getString("FrageMC"))));
					}
				}

				if ((mcFragen.isEmpty() || mcFragen.get(mcFragen.size() - 1).getFrage().equals(frage)) && !skip) {
					antworten.addElement(unslashUnicode(myRS.getString("Antwort")));
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
				alleFragen.addElement(frage);
				if ((ffFragen.isEmpty() || 
						!ffFragen.get(ffFragen.size() - 1).getFrage().equals(frage))/* && !myRS.getString("Antwort").equals("#####")*/) {
					Frage fragenObj = new Frage();
					fragenObj.setFrage(frage);
					fragenObj.setFrageID(myRS.getInt("idFreieFragen"));
					fragenObj.setKategorie(unslashUnicode(myRS.getString("Kategorie")));
					fragenObj.setDatum(myRS.getString("Datum"));
					fragenObj.setFlags(myRS.getString("Flags"));
					fragenObj.setPosition(Integer.parseInt(myRS.getString("Position")));
					fragenObj.setArt("FF");
					fragenObj.setFragebogenID(fb.getId());

					int iii;
					for (iii = 0; iii < ueberschriften.size(); iii++) {

						if (fragenObj.getPosition() == ueberschriften.get(iii).getPostition()) {
							isUeberschrift = true;
							break;
						}
					}

					if (isUeberschrift) {
						fragenObj.setUeberschrift(ueberschriften.get(iii).getUeberschrift());
					}

					ffFragen.addElement(fragenObj);
				} else {
					if (myRS.getString("Antwort").equals("#####")) {
						skip = true;
						ueberschriften.addElement(new Ueberschrift(Integer.parseInt(myRS.getString("Position")),
								unslashUnicode(myRS.getString("FrageMC"))));
					}
				}

				if ((ffFragen.isEmpty() || ffFragen.get(ffFragen.size() - 1).getFrage().equals(frage)) && !skip) {
					antworten.addElement("");
				}
			}

			Vector<Frage> fragen = new Vector<Frage>();

			for (int z = 0; z < mcFragen.size(); z++) {
				fragen.addElement(mcFragen.get(z));
			}

			for (int z = 0; z < ffFragen.size(); z++) {
				fragen.addElement(ffFragen.get(z));
			}

			alleFragen.addElement("DUMMY");

			int count = 1;
			for (int i = 0; i < alleFragen.size() - 1; i++) {

				if (alleFragen.get(i).equals(alleFragen.get(i + 1))) {
					count++;
				} else {
					pos.addElement(count);
					count = 1;
				}
			}
			int c = 0;

			for (int i = 0; i < pos.size(); i++) {
				for (int j = 0; j < pos.get(i); j++) {
					try {
						fragen.get(i).addAntwort_moeglichkeit(antworten.get(i + j + c));
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
	 *            Vector Vector FrageErstellen: alle Fragen des Fragebogens
	 * @author Julian und Eric
	 */
	public static void saveUmfrage(Vector<Vector<Frage>> fragen) {
		try {
			Connection myCon = DriverManager.getConnection(url, user, pwd);

			String statement = "INSERT INTO Befragung VALUES(NULL, CURDATE(), ?)";
			PreparedStatement psSql = myCon.prepareStatement(statement);
			psSql.setInt(1, fragen.get(0).get(0).getFragebogenID());
			psSql.executeUpdate();

			int b_id = 0;

			Statement mySQL = myCon.createStatement();
			statement = "SELECT MAX(idBefragung) FROM Befragung";
			ResultSet myRS = mySQL.executeQuery(statement);
			if (myRS.next()) {
				b_id = myRS.getInt("MAX(idBefragung)");

				for (int i = 0; i < fragen.size(); i++) {
					Vector<Frage> panel = fragen.get(i);
					for (Frage frage : panel) {
						if (frage.getArt() == "MC") {
							if (frage.getAntwort().size() > 0) {
								for (String antwort : frage.getAntwort()) {
									antwort = antwort.replaceAll("<.*>", "");
									myRS = null;
									mySQL = null;
									mySQL = myCon.createStatement();
									statement = "SELECT B_has_MCid FROM B_has_MC WHERE idBefragung=" + b_id
											+ " AND idMultipleChoice=" + frage.getFrageID() + " AND AntwortNr="
											+ QuestionService.getAntwortID(antwort);
									myRS = mySQL.executeQuery(statement);
									if (!myRS.next()) {
										myRS = null;
										mySQL = null;
										mySQL = myCon.createStatement();
										statement = "INSERT INTO B_has_MC VALUES(NULL," + b_id + ", "
												+ frage.getFrageID() + ", " + QuestionService.getAntwortID(antwort) + ")";
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
										+ QuestionService.getAntwortID(antwort);
								myRS = mySQL.executeQuery(statement);
								if (!myRS.next()) {
									myRS = null;
									mySQL = null;
									mySQL = myCon.createStatement();
									statement = "INSERT INTO B_has_FF VALUES(NULL," + b_id + ", " + frage.getFrageID()
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
	 * Setzt den Vector "fragenJePanel" zur�ck.
	 */
	public static void resetFragebogen() {
		
		for(Vector<Frage> fragen : GlobalVars.fragenJePanel) {
			for(Frage frage : fragen) {
				frage.setAntwort(null);
				for(CheckBox checkbox : frage.getAntwortenMC()) {
					checkbox.setSelected(false);
				}
				for(TextField textField : frage.getAntwortenFF()) {
					textField.setText("");
				}
				for(ListView<String> list : frage.getAntwortenLIST()) {
					list.getItems().clear();
				}
			}
		}
	}
}
