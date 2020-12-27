package survey;

import application.Database;
import application.GlobalVars;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

import static application.SqlStatement.SQL_COLUMN_SURVEY_ID_MAX;
import static application.SqlStatement.SQL_CREATE_SURVEY;
import static application.SqlStatement.SQL_CREATE_SURVEY_HAS_MULTIPLE_CHOICE_RELATION;
import static application.SqlStatement.SQL_CREATE_SURVEY_HAS_SHORT_ANSWER_RELATION;
import static application.SqlStatement.SQL_GET_MAX_SURVEY_ID;
import static application.SqlStatement.SQL_GET_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID;
import static application.SqlStatement.SQL_GET_SURVEY_HAS_SHORT_ANSWER_RELATION_ID;

public class SurveyService extends Database {

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

            psSql = myCon.prepareStatement(SQL_GET_MAX_SURVEY_ID);
            ResultSet myRS = psSql.executeQuery();
            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_SURVEY_ID_MAX);
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
