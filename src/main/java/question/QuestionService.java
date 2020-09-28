package question;

import application.Database;
import flag.FlagList;
import flag.Number;
import flag.NumberOperator;
import flag.Symbol;
import flag.SymbolType;
import model.FrageEditParam;
import model.Question;
import model.QuestionType;
import model.Questionnaire;
import org.controlsfx.control.Notifications;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static application.SqlStatement.SQL_CREATE_ANSWER;
import static application.SqlStatement.SQL_CREATE_CATEGORY;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_ANSWERS_RELATION;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_DELETE_ANSWERS;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_ID;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_GET_ANSWER_ID;
import static application.SqlStatement.SQL_GET_CATEGORY;
import static application.SqlStatement.SQL_GET_CATEGORY_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_IDS;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_SET_FLAGS_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;

public class QuestionService extends Database {
    /**
     * Gibt alle Antworten einer Frage zurueck.
     *
     * @param question FrageErstellen: die Frage
     * @return ArrayList String aller Antworten
     */
    public static ArrayList<String> getAnswers(Question question) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            Statement mySQL = myCon.createStatement();
            String text = slashUnicode(question.getQuestion()).replaceAll("\\\\", "\\\\\\\\");
            String statement = "SELECT Antwort FROM fragebogen JOIN "
                    + "fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN "
                    + "multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN "
                    + "kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN "
                    + "mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN "
                    + "antworten ON mc_has_a.AntwortNr=antworten.AntwortNr " + "WHERE mc1.FrageMC='" + text + "' "
                    + "UNION  " + "SELECT Antwort FROM fragebogen JOIN "
                    + "fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen JOIN "
                    + "freiefragen ff1 ON fb_has_ff.idFreieFragen=ff1.idFreieFragen JOIN "
                    + "kategorie ON ff1.idKategorie=kategorie.idKategorie JOIN freiefragen "
                    + "JOIN ff_has_a ON ff1.idFreieFragen=ff_has_a.idFreieFragen JOIN "
                    + "antworten ON ff_has_a.AntwortNr=antworten.AntwortNr " + "WHERE ff1.FrageFF='" + text + "'";
            ResultSet myRS = mySQL.executeQuery(statement);
            ArrayList<String> answers = new ArrayList<>();

            while (myRS.next()) {
                answers.add(unslashUnicode(myRS.getString("Antwort")));
            }
            myCon.close();

            return answers;
        } catch (SQLException e) {
            e.printStackTrace();
            //ErrorLog.fehlerBerichtB("ERROR",
            //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
        }
        return null;
    }

    /**
     * Gibt das Maximum an moeglicher Position im Fragebogen zurueck.
     *
     * @param fb FragebogenDialog: der Fragebogen
     * @return int
     */
    public static int getCountPosition(Questionnaire fb) {
        int positionCount = 0;
        int maxPosMc = 0;
        int maxPosFf = 0;
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            Statement mySQL = myCon.createStatement();
            String statement = "SELECT MAX(fb_has_mc.Position) AS position FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen WHERE fragebogen.idFragebogen="
                    + fb.getId();
            ResultSet myRS = mySQL.executeQuery(statement);
            if (myRS.next()) {
                maxPosMc = myRS.getInt("position");
            }

            myRS = null;
            mySQL = null;
            mySQL = myCon.createStatement();
            statement = "SELECT MAX(fb_has_ff.Position) AS position FROM fragebogen JOIN fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen WHERE fragebogen.idFragebogen="
                    + fb.getId();
            myRS = mySQL.executeQuery(statement);

            if (myRS.next()) {
                maxPosFf = myRS.getInt("position");
            }
            if (maxPosFf > maxPosMc) {
                positionCount = maxPosFf;
            } else {
                positionCount = maxPosMc;
            }

            myCon.close();
        } catch (SQLException e) {
            e.printStackTrace();
            //ErrorLog.fehlerBerichtB("ERROR",
            //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
        }
        return positionCount;
    }

    /**
     * Gibt alle Kategorien zurueck.
     *
     * @return ArrayList String aller Kategorien
     */
    public static ArrayList<String> getKategorie() {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            Statement mySQL = myCon.createStatement();
            String statement = "SELECT * FROM kategorie";
            ResultSet myRS = mySQL.executeQuery(statement);
            ArrayList<String> kategorien = new ArrayList<String>();

            while (myRS.next()) {
                kategorien.add(unslashUnicode(myRS.getString("Kategorie")));
            }
            myCon.close();
            return kategorien;
        } catch (SQLException e) {
            e.printStackTrace();
            //ErrorLog.fehlerBerichtB("ERROR",
            //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
        }
        return null;
    }

    /**
     * Erstellt String mit den angegebenen Flags.
     *
     * @author Florian
     */
    public static void getMoeglicheFlags(FlagList flags, FrageEditParam param) {
        if (param.isRequired()) {
            flags.add(new Symbol(SymbolType.REQUIRED));
        }
        if (param.isList()) {
            flags.add(new Symbol(SymbolType.LIST));
        }
        if (param.isMultipleChoice()) {
            flags.add(new Symbol(SymbolType.MC));
        }
        if (param.isTextarea()) {
            flags.add(new Symbol(SymbolType.TEXT));
        }
        if (param.isYesNoQuestion()) {
            flags.add(new Symbol(SymbolType.JN));
            if (param.isSingleLine()) {
                flags.add(new Symbol(SymbolType.JNExcel));
            }
        }

        if (param.isNumeric()) {
            if (param.getNumberType().equals("Größer gleich Zahl")) {
                flags.add(new Number(NumberOperator.GTE, param.getCountChars()));
            }

            if (param.getNumberType().equals("Kleiner gleich Zahl")) {
                flags.add(new Number(NumberOperator.LTE, param.getCountChars()));
            }

            if (param.getNumberType().equals("Genau wie die Zahl")) {
                flags.add(new Number(NumberOperator.EQ, param.getCountChars()));
            }
        }

        //

        if (param.isValuationAsk()) {
            flags.add(new Symbol(SymbolType.B));
        }
    }

    /**
     * Updated die Flags, beim LÃ¶schen einer Antwort, auf die reagiert wird.
     * Gibt bei Erfolg TRUE zurueck.
     *
     * @param question FrageErstellen: die Frage
     * @return boolean
     */
    // anneSehrNeu
    public static boolean updateFlags(Question question) {
        String statement;
        String flag = "";
        int start = -1;
        int end = -1;

        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                Statement mySQL = myCon.createStatement();
                statement = "SELECT flags, idMultipleChoice FROM FB_has_mc where flags LIKE '%__" + question.getQuestionId()
                        + "A%'";
                ResultSet myRS = mySQL.executeQuery(statement);

                // Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
                // reagiert
                while (myRS.next()) {
                    flag = myRS.getString("flags");
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
                statement = "SELECT flags, idFreieFragen FROM FB_has_ff where flags LIKE '%__" + question.getQuestionId()
                        + "A%'";
                myRS = mySQL.executeQuery(statement);

                // Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
                // reagiert
                while (myRS.next()) {
                    flag = myRS.getString("flags");
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
            } else {
                Statement mySQL = myCon.createStatement();
                statement = "SELECT flags, idFreieFragen FROM FB_has_ff where flags LIKE '%__" + question.getQuestionId()
                        + "A%'";
                ResultSet myRS = mySQL.executeQuery(statement);

                // Flags updaten, wenn eine Frage auf die zu lÃ¶schende Frage
                // reagiert
                while (myRS.next()) {
                    flag = myRS.getString("flags");
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
                    flag = myRS.getString("flags");
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
            }
            myCon.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updatet die Flags. Gibt bei Erfolg TRUE zurueck.
     *
     * @param fb   FragebogenDialog: der Fragebogen
     * @param type String: Art der Frage
     * @param id   int: ID der Frage
     * @return boolean
     */
    public static boolean updateFlags(Questionnaire fb, QuestionType type, int id) {
        if (type.equals(QuestionType.SHORT_ANSWER)) {
            try {
                Connection myCon = DriverManager.getConnection(url, user, pwd);
                Statement mySQL = myCon.createStatement();
                String statement = "SELECT Flags FROM Fb_has_FF WHERE idFragebogen=" + fb.getId()
                        + " AND idFreieFragen=" + id;
                ResultSet myRS = mySQL.executeQuery(statement);

                if (myRS.next()) {
                    if (myRS.getString("Flags").contains("+")) {
                        return true;
                    } else {
                        String flags = myRS.getString("Flags") + " +";

                        mySQL = myCon.createStatement();
                        statement = "UPDATE Fb_has_FF SET Flags='" + flags + "'  WHERE idFragebogen="
                                + fb.getId() + " AND idFreieFragen=" + id;
                        mySQL.executeUpdate(statement);
                        return true;
                    }

                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //ErrorLog.fehlerBerichtB("ERROR",
                //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
                //		e.getMessage());
                return false;
            }

        } else {
            try {
                Connection myCon = DriverManager.getConnection(url, user, pwd);
                Statement mySQL = myCon.createStatement();
                String statement = "SELECT Flags FROM Fb_has_MC WHERE idFragebogen=" + fb.getId()
                        + " AND idMultipleChoice=" + id;
                ResultSet myRS = mySQL.executeQuery(statement);

                if (myRS.next()) {
                    if (myRS.getString("Flags").contains("+")) {
                        return true;
                    } else {
                        String flags = myRS.getString("Flags") + " +";

                        mySQL = myCon.createStatement();
                        statement = "UPDATE Fb_has_MC SET Flags='" + flags + "'  WHERE idFragebogen="
                                + fb.getId() + " AND idMultipleChoice=" + id;
                        mySQL.executeUpdate(statement);
                        return true;
                    }
                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //ErrorLog.fehlerBerichtB("ERROR",
                //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
                //		e.getMessage());
                return false;
            }
        }
    }

    /**
     * Prueft, ob eine Frage Pflichtfrage ist. Gibt bei Erfolg TRUE zurueck.
     *
     * @param fb   FragebogenDialog: der Fragebogen
     * @param type String: Art der Frage
     * @param id   int: ID der Frage
     * @return boolean
     */
    public static boolean isPflichtfrage(Questionnaire fb, QuestionType type, int id) {
        if (type.equals(QuestionType.SHORT_ANSWER)) {
            try {
                Connection myCon = DriverManager.getConnection(url, user, pwd);
                Statement mySQL = myCon.createStatement();
                String statement = "SELECT Flags FROM Fb_has_FF WHERE idFragebogen=" + fb.getId()
                        + " AND idFreieFragen=" + id;
                ResultSet myRS = mySQL.executeQuery(statement);

                if (myRS.next()) {
                    return myRS.getString("Flags").contains("+");
                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //rrorLog.fehlerBerichtB("ERROR",
                //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
                //		e.getMessage());
                return false;
            }

        } else {
            try {
                Connection myCon = DriverManager.getConnection(url, user, pwd);
                Statement mySQL = myCon.createStatement();
                String statement = "SELECT Flags FROM Fb_has_MC WHERE idMultipleChoice=" + fb.getId()
                        + " AND idMultipleChoice=" + id;
                ResultSet myRS = mySQL.executeQuery(statement);

                if (myRS.next()) {
                    return myRS.getString("Flags").contains("+");
                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                //ErrorLog.fehlerBerichtB("ERROR",
                //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(),
                //		e.getMessage());
                return false;
            }
        }
    }

    /**
     * PrÃ¼ft, ob eine Frage doppelt vor kommt und gibt dem Duplikat einen
     * Suffix.
     *
     * @param question String: die Frage
     * @return String: die Frage ggf. mit Suffix
     * @author Eric
     */
    public static String duplicateQuestion(String question) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = null;
            String statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
            psSql = myCon.prepareStatement(statement);
            psSql.setString(1, slashUnicode(slashUnicode(question)));
            ResultSet myRS = psSql.executeQuery();

            if (!myRS.next()) {
                psSql = null;
                statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, slashUnicode(slashUnicode(question)));
                myRS = psSql.executeQuery();

                if (!myRS.next()) {
                    return question;
                }
            }
            int zahl;
            Pattern MY_PATTERN = Pattern.compile("#\\[[0-9]+\\]");
            Matcher m = MY_PATTERN.matcher(question);
            if (m.find()) {
                String string = m.group(0);
                zahl = Integer.parseInt(string.substring(2, string.length() - 1));
                question = question.substring(0, m.start());
            } else {
                zahl = 1;
            }

            while (true) {
                String suffix = "#[" + ++zahl + "]";
                myCon = DriverManager.getConnection(url, user, pwd);
                psSql = null;
                statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, slashUnicode(slashUnicode(question + suffix)));
                myRS = psSql.executeQuery();

                if (!myRS.next()) {
                    psSql = null;
                    statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
                    psSql = myCon.prepareStatement(statement);
                    psSql.setString(1, slashUnicode(slashUnicode(question + suffix)));
                    myRS = psSql.executeQuery();

                    if (!myRS.next()) {
                        question = question + suffix;
                        break;
                    }
                }
            }

            return question;

        } catch (SQLException e) {
            //ErrorLog.fehlerBerichtB("ERROR",
            //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Speichert eine neue Freie Frage. Gibt bei Erfolg TRUE zurÃ¼ck.
     *
     * @param selectedFB FragebogenDialog
     * @param question   FrageErstellen
     * @return boolean
     * @author Anne
     */
    public static boolean saveShortAnswerQuestion(Questionnaire selectedFB, Question question) {

        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            String statement;
            int idKategorie = -1;
            int idFreieFrage = -1;
            int idFragebogen = selectedFB.getId();

            if (question.getQuestionId() == 0) {
                statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
                PreparedStatement psSql = myCon.prepareStatement(statement);
                psSql.setString(1, slashUnicode(question.getQuestion()));
                ResultSet myRS = psSql.executeQuery();

                if (myRS.next()) {
                    idFreieFrage = myRS.getInt("idFreieFragen");
                }
                psSql = null;
                myRS = null;
            } else {
                idFreieFrage = question.getQuestionId();
            }

            Statement mySQL = myCon.createStatement();
            statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
            PreparedStatement psSql = myCon.prepareStatement(statement);
            psSql.setString(1, slashUnicode(question.getCategory()));
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                idKategorie = myRS.getInt("idKategorie");
            } else {
                psSql = null;
                mySQL = null;

                mySQL = myCon.createStatement();
                statement = "INSERT INTO Kategorie VALUES(NULL, ?)";
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, slashUnicode(question.getCategory()));
                psSql.executeUpdate();

                psSql = null;
                mySQL = null;
                myRS = null;

                mySQL = myCon.createStatement();
                statement = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, slashUnicode(question.getCategory()));
                myRS = psSql.executeQuery();

                if (myRS.next()) {
                    idKategorie = myRS.getInt("idKategorie");
                }
            }
            mySQL = null;
            myRS = null;
            psSql = null;

            mySQL = myCon.createStatement();
            statement = "SELECT idKategorie, idFreieFragen FROM FreieFragen WHERE idFreieFragen=" + idFreieFrage;
            myRS = mySQL.executeQuery(statement);

            if (myRS.next()) {
                mySQL = null;
                if (myRS.getInt("idKategorie") != idKategorie) {
                    mySQL = myCon.createStatement();
                    statement = "UPDATE FreieFragen SET idKategorie=" + idKategorie + " WHERE idFreieFragen="
                            + idFreieFrage;
                    mySQL.execute(statement);
                    mySQL = null;
                }
                mySQL = myCon.createStatement();
                statement = "UPDATE FreieFragen SET FrageFF=? WHERE idFreieFragen=" + idFreieFrage;
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, slashUnicode(question.getQuestion()));
                psSql.executeUpdate();

            } else {
                psSql = null;
                mySQL.close();
                mySQL = null;

                mySQL = myCon.createStatement();
                statement = "INSERT INTO FreieFragen VALUES(NULL, ? ," + idKategorie + ")";
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, slashUnicode(question.getQuestion()));
                psSql.executeUpdate();

                mySQL = null;
                myRS = null;
                psSql = null;

                mySQL = myCon.createStatement();
                statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, slashUnicode(question.getQuestion()));
                myRS = psSql.executeQuery();

                if (myRS.next()) {
                    idFreieFrage = myRS.getInt("idFreieFragen");
                }
            }

            mySQL = null;
            myRS = null;
            psSql = null;

            mySQL = myCon.createStatement();
            statement = "SELECT idRelFBFF FROM FB_HAS_FF WHERE idFragebogen=" + idFragebogen + " AND idFreieFragen="
                    + idFreieFrage;
            myRS = mySQL.executeQuery(statement);

            if (myRS.next()) {
                int idRelFBFF = myRS.getInt("idRelFBFF");
                mySQL = null;
                myRS = null;
                mySQL = myCon.createStatement();
                statement = "UPDATE FB_HAS_FF SET Flags='" + question.getFlags().createFlagString() + "' WHERE idRelFBFF=" + idRelFBFF;
                mySQL.executeUpdate(statement);

                mySQL = null;
                myRS = null;
                mySQL = myCon.createStatement();
                statement = "UPDATE FB_HAS_FF SET Position=" + question.getPosition() + " WHERE idRelFBFF=" + idRelFBFF;
                mySQL.executeUpdate(statement);
            } else {
                mySQL.close();
                mySQL = myCon.createStatement();
                statement = "INSERT INTO Fb_has_FF VALUES (NULL," + idFragebogen + "," + idFreieFrage + ","
                        + question.getPosition() + ",'" + question.getFlags().createFlagString() + "')";
                mySQL.executeUpdate(statement);
                mySQL = null;
                myRS = null;
            }

            psSql = null;
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
     * Gibt die ID der Antwort zurÃ¼ck. Ist die Antwort noch nicht vorhanden,
     * wird sie zuerst in der Datenbank gespeichert.
     *
     * @param answer String
     * @return int
     * @author Eric
     */
    public static int provideAnswerId(String answer) {
        Integer id;

        id = getAnswerId(Objects.requireNonNull(answer));

        if (id == null) {
            createAnswer(answer);
            id = getAnswerId(answer);
        }

        return Objects.requireNonNull(id);
    }

    /**
     * Loescht die gegebene Antwort. Gibt bei Erfolg TRUE zurueck.
     *
     * @param answerIds        ArrayList<Integer>: ids der zu-lÃ¶schendenen Antworten
     * @param multipleChoiceId int: die zugehÃ¶rige Frage
     */
    // anneSehrNeu
    public static void deleteAnswers(ArrayList<Integer> answerIds, int multipleChoiceId) {

        for (Integer answerId : answerIds) {
            deleteMultipleChoiceAnswersRelation(answerId, multipleChoiceId);
        }

        deleteAnswers();
    }

    /**
     * Speichert eine neue Multipe Choice Frage. Gibt bei Erfolg TRUE zurÃ¼ck.
     *
     * @author Eric
     */
    public static void saveMultipleChoice(int questionnaireId, Question question, ArrayList<Integer> answersIds) {

        Integer multipleChoiceId;
        Integer categoryId;

        multipleChoiceId = getMultipleChoiceId(question.getQuestion());

        // category
        categoryId = getCategoryId(question.getCategory());

        if (categoryId == null) {
            createCategory(question.getCategory());
            categoryId = getCategoryId(question.getCategory());
        }

        // question
        if (multipleChoiceId == null) {
            createMultipleChoiceQuestion(question.getQuestion(), Objects.requireNonNull(categoryId));
            multipleChoiceId = getMultipleChoiceId(question.getQuestion());
        }

        // answers
        List<Integer> oldRelationIds = getMultipleChoiceAnswersRelationIds(Objects.requireNonNull(multipleChoiceId));
        List<Integer> newRelationIds = new ArrayList<>();

        for (Integer answersId : answersIds) {
            Integer relationId = getMultipleChoiceAnswersRelationId(multipleChoiceId, answersId);
            if (relationId != null) {
                newRelationIds.add(relationId);
            } else {
                createMultipleChoiceAnswersRelation(multipleChoiceId, answersId);
            }
        }

        for (int oldRelationId : oldRelationIds) {
            if (!newRelationIds.contains(oldRelationId)) {
                deleteMultipleChoiceAnswersRelation(oldRelationId);
            }
        }

        // questionnaire
        Integer relationId = getMultipleChoiceQuestionnaireRelationId(questionnaireId, multipleChoiceId);

        if (relationId != null) {
            newRelationIds.add(relationId);

            setFlagsOnMultipleChoiceQuestionnaireRelation(question.getFlags().createFlagString(), relationId);
            setPositionOnMultipleChoiceQuestionnaireRelation(question.getPosition(), relationId);
        } else {
            createMultipleChoiceQuestionnaireRelation(questionnaireId, multipleChoiceId, question.getPosition(), question.getFlags().createFlagString());
        }
    }

    /**
     * Speichert eine neue Bewertungsfrage. Gibt bei Erfolg TRUE zurÃ¼ck.
     *
     * @author Anne
     */
    public static void saveEvaluationQuestion(int questionnaireId, Question question) {

        Integer categoryId;
        Integer evaluationQuestionId;

        evaluationQuestionId = getMultipleChoiceId(question.getQuestion());

        // category
        categoryId = getCategoryId(question.getCategory());

        if (categoryId == null) {
            createCategory(question.getCategory());
            categoryId = getCategoryId(question.getCategory());
        }

        // question
        if (evaluationQuestionId == null) {
            createMultipleChoiceQuestion(question.getQuestion(), Objects.requireNonNull(categoryId));
            evaluationQuestionId = getMultipleChoiceId(question.getQuestion());
        }

        // questionnaire
        Integer relationId = getMultipleChoiceQuestionnaireRelationId(questionnaireId, Objects.requireNonNull(evaluationQuestionId));

        if (relationId != null) {
            setFlagsOnMultipleChoiceQuestionnaireRelation(question.getFlags().createFlagString(), relationId);
            setPositionOnMultipleChoiceQuestionnaireRelation(question.getPosition(), relationId);
        } else {
            createMultipleChoiceQuestionnaireRelation(questionnaireId, evaluationQuestionId, question.getPosition(), question.getFlags().createFlagString());
        }

        //answer
        int countAnswer = getMultipleChoiceAnswersRelationIds(evaluationQuestionId).size();

        if (countAnswer != 11) {
            for (int i = 0; i <= 10; i++) {
                int answerId = provideAnswerId(String.valueOf(i));
                createMultipleChoiceAnswersRelation(evaluationQuestionId, answerId);
            }
        }
    }

    public static boolean createCategory(String category) {
        try {
            if (checkCategory(category)) {
                Notifications.create().title("Kategorie anlegen").text("Die Kategorie existiert bereits!").showError();
            } else {
                Connection myCon = DriverManager.getConnection(url, user, pwd);
                PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_CATEGORY);
                psSql.execute();
                myCon.close();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            //ErrorLog.fehlerBerichtB("ERROR",
            //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
        }
        return false;
    }

    public static boolean checkCategory(String category) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_CATEGORY);
            psSql.setString(1, category);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return true;
            }

            myCon.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Integer getMultipleChoiceId(String question) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ID);
            psSql.setString(1, slashUnicode(question));
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt("multipleChoiceId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getCategoryId(String category) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_CATEGORY_ID);
            psSql.setString(1, slashUnicode(category));
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt("idKategorie");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createMultipleChoiceQuestion(String question, int categoryId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_MULTIPLE_CHOICE);
            psSql.setString(1, slashUnicode(question));
            psSql.setInt(2, categoryId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> getMultipleChoiceAnswersRelationIds(int multipleChoiceId) {
        List<Integer> results = new ArrayList<>();
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_IDS);
            psSql.setInt(1, multipleChoiceId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                results.add(myRS.getInt("idRelMCA"));
            }
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static Integer getMultipleChoiceAnswersRelationId(int multipleChoiceId, int answerId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_ID);
            psSql.setInt(1, multipleChoiceId);
            psSql.setInt(2, answerId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt("idRelMCA");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createMultipleChoiceAnswersRelation(int multipleChoiceId, int answerId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_MULTIPLE_CHOICE_ANSWERS_RELATION);
            psSql.setInt(1, multipleChoiceId);
            psSql.setInt(2, answerId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceAnswersRelation(int relationId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_ID);
            psSql.setInt(1, relationId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceAnswersRelation(int answerId, int multipleChoiceId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION);
            psSql.setInt(1, answerId);
            psSql.setInt(2, multipleChoiceId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getMultipleChoiceQuestionnaireRelationId(int questionnaireId, int multipleChoiceId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, multipleChoiceId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt("idRelFBMC");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFlagsOnMultipleChoiceQuestionnaireRelation(String flags, int multipleChoiceQuestionnaireRelationId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_FLAGS_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setString(1, flags);
            psSql.setInt(2, multipleChoiceQuestionnaireRelationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setPositionOnMultipleChoiceQuestionnaireRelation(int position, int multipleChoiceQuestionnaireRelationId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, position);
            psSql.setInt(2, multipleChoiceQuestionnaireRelationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createMultipleChoiceQuestionnaireRelation(int questionnaireId, int multipleChoiceId, int position, String flags) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, multipleChoiceId);
            psSql.setInt(3, position);
            psSql.setString(4, flags);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAnswers() {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            Statement mySQL = myCon.createStatement();
            mySQL.execute(SQL_DELETE_ANSWERS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getAnswerId(String answer) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_ANSWER_ID);
            psSql.setString(1, answer);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt("AntwortNr");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createAnswer(String answer) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_ANSWER);
            psSql.setString(1, answer);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
