package questionList;

import application.Database;
import flag.FlagList;
import flag.React;
import model.Answer;
import model.Question;
import model.QuestionType;
import question.QuestionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_QUESTION_ID;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER_HAS_ANSWERS_RELATION_BY_QUESTION_ID;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_GET_HEADLINES;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS;
import static application.SqlStatement.SQL_GET_OTHER_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_IDS;
import static application.SqlStatement.SQL_GET_OTHER_SHORT_ANSWER_QUESTIONNAIRE_RELATION_IDS;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER;
import static application.SqlStatement.SQL_UPDATE_MULTIPLE_CHOICE_FLAGS;
import static application.SqlStatement.SQL_UPDATE_SHORT_ANSWERS_FLAGS;

public class QuestionListService extends Database {

    public static ArrayList<Question> getHeadlines(int questionnaireId) {
        ArrayList<Question> headlines = new ArrayList<>();
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_HEADLINES);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();
            while (myRS.next()) {
                Question question = new Question();
                question.setQuestion(unslashUnicode(myRS.getString("FrageMC")));
                question.setQuestionId(myRS.getInt("idMultipleChoice"));
                question.setCategory(unslashUnicode(myRS.getString("Kategorie")));
                question.setDate(myRS.getString("Datum"));
                question.setFlags(new FlagList());
                question.setPosition(Integer.parseInt(myRS.getString("Position")));
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                question.setQuestionnaireId(questionnaireId);
                question.addAnswerOption(myRS.getString("Antwort"));
                headlines.add(question);
            }
            myCon.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return headlines;
    }

    /**
     * Loescht die gegebene Frage
     *
     * @param question FrageErstellen: die Frage
     */
    // anneSehrNeu
    public static void deleteQuestion(int questionnaireId, Question question) {
        deleteQuestion(questionnaireId, question.getQuestionId(), question.getQuestionType());
    }

    public static void deleteQuestion(int questionnaireId, int questionId, QuestionType questionType) {
        deleteMultipleChoiceReactFlagsFromTargetQuestion(questionnaireId, questionId);
        deleteShortAnswerReactFlagsFromTargetQuestion(questionnaireId, questionId);

        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {

            deleteMultipleChoiceQuestionnaireRelation(questionnaireId, questionId);

            if (!doesMultipleChoiceQuestionExistsInOtherQuestionnaire(questionnaireId, questionId)) {

                deleteMultipleChoiceHasAnswerRelation(questionId);
                deleteMultipleChoiceQuestion(questionId);
            }
        } else if (questionType.equals(QuestionType.SHORT_ANSWER)) {
            deleteShortAnswerQuestionnaireRelation(questionnaireId, questionId);

            if (!doesShortAnswerQuestionExistsInOtherQuestionnaire(questionnaireId, questionId)) {

                deleteShortAnswerHasAnswerRelation(questionId);
                deleteShortAnswerQuestion(questionId);
            }
        }

        QuestionService.deleteAnswers();
    }

    public static void deleteShortAnswerQuestion(int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_SHORT_ANSWER);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteShortAnswerHasAnswerRelation(int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_SHORT_ANSWER_HAS_ANSWERS_RELATION_BY_QUESTION_ID);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceQuestion(int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceHasAnswerRelation(int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_QUESTION_ID);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean doesMultipleChoiceQuestionExistsInOtherQuestionnaire(int questionnaireId, int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_OTHER_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_IDS);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, questionId);

            return psSql.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean doesShortAnswerQuestionExistsInOtherQuestionnaire(int questionnaireId, int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_OTHER_SHORT_ANSWER_QUESTIONNAIRE_RELATION_IDS);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, questionId);

            return psSql.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void deleteShortAnswerQuestionnaireRelation(int questionnaireId, int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceQuestionnaireRelation(int questionnaireId, int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Answer> getMultipleChoiceQuestionAnswers(int questionnaireId, int questionId) {
        List<Answer> answers = new ArrayList<>();
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                Answer answer = new Answer();
                answer.setId(myRS.getInt("AntwortNr"));
                answer.setValue(myRS.getString("Antwort"));

                if (!answers.contains(answer)) {
                    answers.add(answer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
    }

    public static void deleteShortAnswerReactFlagsFromTargetQuestion(int questionnaireId, int questionId) {

        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            // Flags updaten, wenn eine Frage auf die zu loeschende Frage reagiert
            while (myRS.next()) {
                FlagList flags = new FlagList(myRS.getString("flags"));
                int targetQuestionId = myRS.getInt("idFreieFrage");
                List<React> flagsToDelete = flags.getAll(React.class);
                if (!flagsToDelete.isEmpty()) {
                    flags.removeAll(flagsToDelete);

                    psSql = myCon.prepareStatement(SQL_UPDATE_SHORT_ANSWERS_FLAGS);
                    psSql.setInt(1, questionnaireId);
                    psSql.setInt(2, targetQuestionId);
                    psSql.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceReactFlagsFromTargetQuestion(int questionnaireId, int questionId) {
        try {
            Connection myCon = DriverManager.getConnection(url, user, pwd);

            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            // Flags updaten, wenn eine Frage auf die zu loeschende Frage reagiert
            while (myRS.next()) {
                FlagList flags = new FlagList(myRS.getString("flags"));
                int targetQuestionId = myRS.getInt("idMultipleChoice");
                List<React> flagsToDelete = flags.getAll(React.class);
                if (!flagsToDelete.isEmpty()) {
                    flags.removeAll(flagsToDelete);

                    psSql = myCon.prepareStatement(SQL_UPDATE_MULTIPLE_CHOICE_FLAGS);
                    psSql.setInt(1, questionnaireId);
                    psSql.setInt(2, targetQuestionId);
                    psSql.execute();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> getMultipleChoiceQuestionsByQuestionnaireId(int questionnaireId) {
        try {
            List<Integer> results = new ArrayList<>();
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                results.add(myRS.getInt("idMultipleChoice"));
            }

            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Integer> getShortAnswerQuestionsByQuestionnaireId(int questionnaireId) {
        try {
            List<Integer> results = new ArrayList<>();
            Connection myCon = DriverManager.getConnection(url, user, pwd);
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                results.add(myRS.getInt("idFreieFragen"));
            }

            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
