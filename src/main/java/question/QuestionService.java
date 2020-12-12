package question;

import application.Database;
import flag.FlagList;
import flag.Number;
import flag.NumberOperator;
import flag.Symbol;
import flag.SymbolType;
import model.Answer;
import model.FrageEditParam;
import model.Question;
import model.QuestionType;
import org.controlsfx.control.Notifications;
import questionList.QuestionListService;

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

import static application.SqlStatement.SQL_COLUMN_ANSWER_ID;
import static application.SqlStatement.SQL_COLUMN_ANSWER_NAME;
import static application.SqlStatement.SQL_COLUMN_CATEGORY_ID;
import static application.SqlStatement.SQL_COLUMN_CATEGORY_NAME;
import static application.SqlStatement.SQL_COLUMN_FLAGS;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_RELATION_ID;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_COLUMN_POSITION;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_CREATE_ANSWER;
import static application.SqlStatement.SQL_CREATE_CATEGORY;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_ANSWERS_RELATION;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_CREATE_SHORT_ANSWER;
import static application.SqlStatement.SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_DELETE_ANSWERS;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_ID;
import static application.SqlStatement.SQL_GET_ANSWERS;
import static application.SqlStatement.SQL_GET_ANSWER_ID;
import static application.SqlStatement.SQL_GET_CATEGORIE_NAMES;
import static application.SqlStatement.SQL_GET_CATEGORY_ID;
import static application.SqlStatement.SQL_GET_MAX_MULTIPLE_CHOICE_POSITION;
import static application.SqlStatement.SQL_GET_MAX_SHORT_ANSWER_POSITION;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_IDS;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_FLAGS;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_GET_SHORT_ANSWERS_FLAGS;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_SET_FLAGS_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_SET_FLAGS_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_UPDATE_MULTIPLE_CHOICE_FLAGS;
import static application.SqlStatement.SQL_UPDATE_SHORT_ANSWERS_FLAGS;

public class QuestionService extends Database {
    /**
     * Gibt alle Antworten einer Frage zurueck.
     *
     * @param question FrageErstellen: die Frage
     * @return ArrayList String aller Antworten
     */
    public static ArrayList<String> getAnswers(Question question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            String text = question.getQuestion();
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_ANSWERS);
            psSql.setString(1, text);
            psSql.setString(2, text);

            ResultSet myRS = psSql.executeQuery();
            ArrayList<String> answers = new ArrayList<>();

            while (myRS.next()) {
                answers.add(myRS.getString(SQL_COLUMN_ANSWER_NAME));
            }
            return answers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gibt das Maximum an moeglicher Position im Fragebogen zurueck.
     *
     * @param questionnaireId FragebogenDialog: der Fragebogen
     * @return int
     */
    public static int getCountPosition(int questionnaireId) {
        int maxPosMc = Objects.requireNonNull(getMaxMultipleChoicePosition(questionnaireId));
        int maxPosFf = Objects.requireNonNull(getMaxShortAnswerPosition(questionnaireId));

        return Math.max(maxPosFf, maxPosMc);
    }

    public static Integer getMaxMultipleChoicePosition(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MAX_MULTIPLE_CHOICE_POSITION);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_POSITION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getMaxShortAnswerPosition(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MAX_SHORT_ANSWER_POSITION);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_POSITION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gibt alle Kategorien zurueck.
     *
     * @return ArrayList String aller Kategorien
     */
    public static ArrayList<String> getCategories() {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            Statement mySQL = myCon.createStatement();
            ResultSet myRS = mySQL.executeQuery(SQL_GET_CATEGORIE_NAMES);
            ArrayList<String> categories = new ArrayList<>();

            while (myRS.next()) {
                categories.add(myRS.getString(SQL_COLUMN_CATEGORY_NAME));
            }

            return categories;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Erstellt String mit den angegebenen Flags.
     *
     * @author Florian
     */
    public static void getPossibleFlags(FlagList flags, FrageEditParam param) {
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
     */
    // anneSehrNeu
    public static void updateFlags(int questionnaireId, Question question) {
        QuestionListService.deleteMultipleChoiceReactFlagsFromTargetQuestion(questionnaireId, question.getQuestionId());
        QuestionListService.deleteShortAnswerReactFlagsFromTargetQuestion(questionnaireId, question.getQuestionId());
    }

    /**
     * Updatet die Flags. Gibt bei Erfolg TRUE zurueck.
     *
     * @param questionnaireId FragebogenDialog: der Fragebogen
     * @param type            String: Art der Frage
     * @param questionId      int: ID der Frage
     */
    public static void provideQuestionRequired(int questionnaireId, QuestionType type, int questionId) {
        FlagList flagList = new FlagList(getFlags(questionnaireId, type, questionId));
        if (!flagList.is(SymbolType.REQUIRED)) {
            flagList.add(new Symbol(SymbolType.REQUIRED));

            updateFlags(type, flagList.createFlagString(), questionnaireId, questionId);
        }
    }

    public static boolean isQuestionRequired(int questionnaireId, QuestionType type, int questionId) {
        return Objects.requireNonNull(getFlags(questionnaireId, type, questionId))
                .contains(SymbolType.REQUIRED.toString());
    }

    public static String getFlags(int questionnaireId, QuestionType type, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(type.equals(QuestionType.SHORT_ANSWER) ?
                    SQL_GET_SHORT_ANSWERS_FLAGS : SQL_GET_MULTIPLE_CHOICE_FLAGS);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, questionId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getString(SQL_COLUMN_FLAGS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateFlags(QuestionType type, String flags, int questionnaireId, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(type.equals(QuestionType.SHORT_ANSWER) ?
                    SQL_UPDATE_SHORT_ANSWERS_FLAGS : SQL_UPDATE_MULTIPLE_CHOICE_FLAGS);
            psSql.setString(1, flags);
            psSql.setInt(2, questionnaireId);
            psSql.setInt(3, questionId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
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
    //TODO Duplicate Prozess und Mehrfachverwendung von Fragen überarbeiten
    @Deprecated
    public static String duplicateQuestion(String question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = null;
            String statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
            psSql = myCon.prepareStatement(statement);
            psSql.setString(1, question);
            ResultSet myRS = psSql.executeQuery();

            if (!myRS.next()) {
                psSql = null;
                statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, question);
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
                psSql = null;
                statement = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
                psSql = myCon.prepareStatement(statement);
                psSql.setString(1, question + suffix);
                myRS = psSql.executeQuery();

                if (!myRS.next()) {
                    psSql = null;
                    statement = "SELECT idMultipleChoice FROM MultipleChoice WHERE FrageMC=?";
                    psSql = myCon.prepareStatement(statement);
                    psSql.setString(1, question + suffix);
                    myRS = psSql.executeQuery();

                    if (!myRS.next()) {
                        question = question + suffix;
                        break;
                    }
                }
            }

            return question;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Speichert eine neue Freie Frage. Gibt bei Erfolg TRUE zurÃ¼ck.
     *
     * @param question FrageErstellen
     * @author Anne
     */
    public static void saveShortAnswerQuestion(int questionnaireId, Question question) {

        // category
        Integer categoryId = provideCategoryId(question.getCategory());

        // question
        Integer shortAnswerId = provideShortAnswerQuestion(question.getQuestion(), categoryId);

        // questionnaire
        Integer relationId = getShortAnswerQuestionnaireRelationId(questionnaireId, shortAnswerId);

        if (relationId != null) {
            setFlagsOnShortAnswerQuestionnaireRelation(question.getFlags().createFlagString(), relationId);
            setPositionOnShortAnswerQuestionnaireRelation(question.getPosition(), relationId);
        } else {
            createShortAnswerQuestionnaireRelation(questionnaireId, shortAnswerId, question.getPosition(), question.getFlags().createFlagString());
        }
    }

    /**
     * Speichert eine neue Multipe Choice Frage. Gibt bei Erfolg TRUE zurÃ¼ck.
     *
     * @author Eric
     */
    public static void saveMultipleChoice(int questionnaireId, Question question, ArrayList<Answer> answers) {

        // category
        Integer categoryId = provideCategoryId(question.getCategory());

        // question
        Integer multipleChoiceId = provideMultipleChoiceQuestion(question.getQuestion(), categoryId);

        // answers
        List<Integer> oldRelationIds = getMultipleChoiceAnswersRelationIds(Objects.requireNonNull(multipleChoiceId));
        List<Integer> newRelationIds = new ArrayList<>();

        for (Answer answer : answers) {
            Integer relationId = getMultipleChoiceAnswersRelationId(multipleChoiceId, answer.getId());
            if (relationId != null) {
                newRelationIds.add(relationId);
            } else {
                createMultipleChoiceAnswersRelation(multipleChoiceId, answer.getId());
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

        // category
        Integer categoryId = provideCategoryId(question.getCategory());

        // question
        Integer evaluationQuestionId = provideMultipleChoiceQuestion(question.getQuestion(), categoryId);

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

    public static boolean provideCategory(String category) {
        if (checkCategory(category)) {
            Notifications.create().title("Kategorie anlegen").text("Die Kategorie existiert bereits!").showError();
        } else {
            createCategory(category);
        }
        //TODO Bool Wert entfernen
        return true;
    }

    public static void createCategory(String category) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_CATEGORY);
            psSql.setString(1, category);
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkCategory(String category) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_CATEGORY_ID);
            psSql.setString(1, category);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Integer getMultipleChoiceId(String question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ID);
            psSql.setString(1, question);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getShortAnswerId(String question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SHORT_ANSWER_ID);
            psSql.setString(1, question);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getCategoryId(String category) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_CATEGORY_ID);
            psSql.setString(1, category);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_CATEGORY_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer provideCategoryId(String category) {
        Integer categoryId = getCategoryId(category);

        if (categoryId == null) {
            provideCategory(category);
            categoryId = getCategoryId(category);
        }

        return categoryId;
    }

    public static void createMultipleChoiceQuestion(String question, int categoryId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_MULTIPLE_CHOICE);
            psSql.setString(1, question);
            psSql.setInt(2, categoryId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer provideMultipleChoiceQuestion(String question, int categoryId) {
        Integer multipleChoiceId = getMultipleChoiceId(question);

        if (multipleChoiceId == null) {
            createMultipleChoiceQuestion(question, categoryId);
            multipleChoiceId = getMultipleChoiceId(question);
        }

        return multipleChoiceId;
    }

    public static void createShortAnswerQuestion(String question, int categoryId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_SHORT_ANSWER);
            psSql.setString(1, question);
            psSql.setInt(2, categoryId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer provideShortAnswerQuestion(String question, int categoryId) {
        Integer shortAnswerId = getShortAnswerId(question);

        if (shortAnswerId == null) {
            createShortAnswerQuestion(question, categoryId);
            shortAnswerId = getShortAnswerId(question);
        }

        return shortAnswerId;
    }

    public static List<Integer> getMultipleChoiceAnswersRelationIds(int multipleChoiceId) {
        List<Integer> results = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_IDS);
            psSql.setInt(1, multipleChoiceId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                results.add(myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_RELATION_ID));
            }
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static Integer getMultipleChoiceAnswersRelationId(int multipleChoiceId, int answerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_ID);
            psSql.setInt(1, multipleChoiceId);
            psSql.setInt(2, answerId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_RELATION_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createMultipleChoiceAnswersRelation(int multipleChoiceId, int answerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_MULTIPLE_CHOICE_ANSWERS_RELATION);
            psSql.setInt(1, multipleChoiceId);
            psSql.setInt(2, answerId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceAnswersRelation(int relationId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_ID);
            psSql.setInt(1, relationId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceAnswersRelation(int answerId, int multipleChoiceId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION);
            psSql.setInt(1, answerId);
            psSql.setInt(2, multipleChoiceId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getMultipleChoiceQuestionnaireRelationId(int questionnaireId, int multipleChoiceId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, multipleChoiceId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getShortAnswerQuestionnaireRelationId(int questionnaireId, int shortAnswerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, shortAnswerId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setFlagsOnMultipleChoiceQuestionnaireRelation(String flags, int relationId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_FLAGS_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setString(1, flags);
            psSql.setInt(2, relationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setFlagsOnShortAnswerQuestionnaireRelation(String flags, int relationId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_FLAGS_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION);
            psSql.setString(1, flags);
            psSql.setInt(2, relationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setPositionOnMultipleChoiceQuestionnaireRelation(int position, int relationId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, position);
            psSql.setInt(2, relationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setPositionOnShortAnswerQuestionnaireRelation(int position, int relationId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, position);
            psSql.setInt(2, relationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createMultipleChoiceQuestionnaireRelation(int questionnaireId, int multipleChoiceId, int position, String flags) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
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

    public static void createShortAnswerQuestionnaireRelation(int questionnaireId, int shortAnswerId, int position, String flags) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, shortAnswerId);
            psSql.setInt(3, position);
            psSql.setString(4, flags);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAnswers() {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            Statement mySQL = myCon.createStatement();
            mySQL.execute(SQL_DELETE_ANSWERS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getAnswerId(String answer) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_ANSWER_ID);
            psSql.setString(1, answer);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_ANSWER_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createAnswer(String answer) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_ANSWER);
            psSql.setString(1, answer);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
