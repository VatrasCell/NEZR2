package survey;

import application.Database;
import application.GlobalVars;
import flag.FlagList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Headline;
import model.Question;
import model.QuestionType;
import question.QuestionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static application.SqlStatement.SQL_CREATE_SURVEY;
import static application.SqlStatement.SQL_CREATE_SURVEY_HAS_MULTIPLE_CHOICE_RELATION;
import static application.SqlStatement.SQL_CREATE_SURVEY_HAS_SHORT_ANSWER_RELATION;
import static application.SqlStatement.SQL_GET_MAX_SURVEY_ID;
import static application.SqlStatement.SQL_GET_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID;
import static application.SqlStatement.SQL_GET_SURVEY_HAS_SHORT_ANSWER_RELATION_ID;

public class SurveyService extends Database {

    /**
     * Erstellt einen ArrayList aus allen Fragen, welche im ausgewählten Fragebogen
     * sind.
     *
     * @return ArrayList FrageErstellen
     * @author Julian und Eric
     */
    @Deprecated
    public static List<Question> getQuestions(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            System.out.println("FragebogenID: " + questionnaireId);
            Statement mySQL = myCon.createStatement();
            String statment = "SELECT mc1.FrageMC, mc1.idMultipleChoice, Fragebogen.Datum, fb_has_mc.Position, fb_has_mc.Flags, Kategorie, Antwort FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN antworten ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE Fragebogen.idFragebogen="
                    + questionnaireId;
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
                frage = myRS.getString("FrageMC");
                if (!myRS.getString("Antwort").equals("#####")) {
                    alleFragen.add(frage);
                }

                if ((mcFragen.isEmpty() || !mcFragen.get(mcFragen.size() - 1).getQuestion().equals(frage))
                        && !myRS.getString("Antwort").equals("#####")) {
                    Question fragenObj = new Question();
                    fragenObj.setQuestion(frage);
                    fragenObj.setQuestionId(myRS.getInt("idMultipleChoice"));
                    fragenObj.setCategory(myRS.getString("Kategorie"));
                    fragenObj.setDate(myRS.getString("Datum"));
                    fragenObj.setFlags(new FlagList(myRS.getString("Flags")));
                    fragenObj.setPosition(Integer.parseInt(myRS.getString("Position")));
                    fragenObj.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                    fragenObj.setQuestionnaireId(questionnaireId);

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
                                myRS.getString("FrageMC")));
                    }
                }

                if ((mcFragen.isEmpty() || mcFragen.get(mcFragen.size() - 1).getQuestion().equals(frage)) && !skip) {
                    antworten.add(myRS.getString("Antwort"));
                }
            }

            myRS = null;
            mySQL = null;
            mySQL = myCon.createStatement();
            statment = "SELECT ff1.FrageFF, ff1.idFreieFragen, Fragebogen.Datum, fb_has_ff.Position, fb_has_ff.Flags, Kategorie FROM fragebogen JOIN fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen JOIN freiefragen ff1 ON fb_has_ff.idFreieFragen=ff1.idFreieFragen JOIN kategorie ON ff1.idKategorie=kategorie.idKategorie WHERE Fragebogen.idFragebogen="
                    + questionnaireId;
            myRS = mySQL.executeQuery(statment);

            while (myRS.next()) {
                boolean isUeberschrift = false;
                boolean skip = false;
                String frage = myRS.getString("FrageFF");
                alleFragen.add(frage);
                if ((ffFragen.isEmpty() ||
                        !ffFragen.get(ffFragen.size() - 1).getQuestion().equals(frage))/* && !myRS.getString("Antwort").equals("#####")*/) {
                    Question fragenObj = new Question();
                    fragenObj.setQuestion(frage);
                    fragenObj.setQuestionId(myRS.getInt("idFreieFragen"));
                    fragenObj.setCategory(myRS.getString("Kategorie"));
                    fragenObj.setDate(myRS.getString("Datum"));
                    fragenObj.setFlags(new FlagList(myRS.getString("Flags")));
                    fragenObj.setPosition(Integer.parseInt(myRS.getString("Position")));
                    fragenObj.setQuestionType(QuestionType.SHORT_ANSWER);
                    fragenObj.setQuestionnaireId(questionnaireId);

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
                                myRS.getString("FrageMC")));
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
     * @param questionsLists ArrayList ArrayList FrageErstellen: alle Fragen des Fragebogens
     * @author Julian und Eric
     */
    public static void saveSurvey(int questionnaireId, List<ArrayList<Question>> questionsLists) {
        int surveyId = createSurvey(questionnaireId);
        List<Question> questions = discardSecondDimension(questionsLists);

        for (Question question : questions) {
            for (String answer : question.getAnswer()) {
                answer = answer.replaceAll("<.*>", "");
                int answerId = QuestionService.provideAnswerId(answer);
                if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                    if (!existsSurveyMultipleChoiceRelation(surveyId, question.getQuestionId(), answerId)) {
                        createSurveyMultipleChoiceRelation(surveyId, question.getQuestionId(), answerId);
                    }
                } else {
                    if (!existsSurveyShortAnswerRelation(surveyId, question.getQuestionId(), answerId)) {
                        createSurveyShortAnswerRelation(surveyId, question.getQuestionId(), answerId);
                    }
                }
            }
        }

        resetQuestionnaire();
    }

    public static void createSurveyMultipleChoiceRelation(int surveyId, int questionId, int answerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_SURVEY_HAS_MULTIPLE_CHOICE_RELATION);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            psSql.setInt(3, answerId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsSurveyMultipleChoiceRelation(int surveyId, int questionId, int answerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            psSql.setInt(3, answerId);
            ResultSet myRS = psSql.executeQuery();
            return myRS.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void createSurveyShortAnswerRelation(int surveyId, int questionId, int answerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_SURVEY_HAS_SHORT_ANSWER_RELATION);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            psSql.setInt(3, answerId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsSurveyShortAnswerRelation(int surveyId, int questionId, int answerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SURVEY_HAS_SHORT_ANSWER_RELATION_ID);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            psSql.setInt(3, answerId);
            ResultSet myRS = psSql.executeQuery();
            return myRS.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int createSurvey(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {

            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_SURVEY);
            psSql.setInt(1, questionnaireId);
            psSql.executeUpdate();

            ResultSet myRS = psSql.executeQuery(SQL_GET_MAX_SURVEY_ID);
            if (myRS.next()) {
                return myRS.getInt("MAX(idBefragung)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static List<Question> discardSecondDimension(List<ArrayList<Question>> questionsLists) {
        List<Question> results = new ArrayList<>();
        questionsLists.forEach(results::addAll);

        return results;
    }

    public static void resetQuestionnaire() {

        for (ArrayList<Question> questions : GlobalVars.questionsPerPanel) {
            for (Question question : questions) {
                question.setAnswer(null);
                for (CheckBox checkbox : question.getAnswersMC()) {
                    checkbox.setSelected(false);
                }
                for (TextField textField : question.getAnswersFF()) {
                    textField.setText("");
                }
                for (ListView<String> list : question.getAnswersLIST()) {
                    list.getItems().clear();
                }
            }
        }
    }
}
