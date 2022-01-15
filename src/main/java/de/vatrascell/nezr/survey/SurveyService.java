package de.vatrascell.nezr.survey;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.model.SubmittedAnswer;
import de.vatrascell.nezr.model.Survey;
import de.vatrascell.nezr.model.SurveyPage;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_ANSWER;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_ANSWER_OPTION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_CREATION_DATE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_NAME;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_QUESTIONNAIRE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_SURVEY_HAS_ANSWER_OPTION_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_SURVEY_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_SURVEY_ID_MAX;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_SURVEY;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_SURVEY_HAS_ANSWER_OPTION_RELATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_SURVEY_HAS_MULTIPLE_CHOICE_RELATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_SURVEY_HAS_SHORT_ANSWER_RELATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MAX_SURVEY_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWERS;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SHORT_ANSWER_OF_SURVEY;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SURVEYS_BY_QUESTIONNAIRE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SURVEY_HAS_ANSWER_OPTION_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SURVEY_HAS_SHORT_ANSWER_RELATION_ID;

@Service
public class SurveyService extends Database {

    public void saveSurvey(int questionnaireId, List<SurveyPage> pages) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            int surveyId = createSurvey(myCon, questionnaireId);
            List<Question> questions = discardSecondDimension(pages);

            for (Question question : questions) {
                if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                    List<AnswerOption> submittedAnswerOptions = question.getSubmittedAnswer().getSubmittedAnswerOptions();
                    Integer surveyMultipleChoiceRelationId = getSurveyQuestionRelationId(surveyId, question.getQuestionId(), question.getQuestionType());
                    if (surveyMultipleChoiceRelationId == null) {
                        createSurveyMultipleChoiceRelation(myCon, surveyId, question.getQuestionId());
                        surveyMultipleChoiceRelationId = Objects.requireNonNull(getSurveyQuestionRelationId(surveyId, question.getQuestionId(), question.getQuestionType()));
                    }
                    for (AnswerOption answerOption : submittedAnswerOptions) {
                        Integer relId = getSurveyQuestionRelationId(answerOption.getId(), surveyMultipleChoiceRelationId, question.getQuestionType());
                        if (relId == null) {
                            createSurveyAnswerOptionRelation(myCon, answerOption.getId(), surveyMultipleChoiceRelationId);
                        }
                    }

                } else {
                    if (!existsSurveyShortAnswerRelation(surveyId, question.getQuestionId())) {
                        createSurveyShortAnswerRelation(myCon, surveyId, question.getQuestionId(), question.getSubmittedAnswer().getSubmittedAnswerText());
                    }
                }
            }

            resetQuestionnaire();

            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int createSurvey(Connection connection, int questionnaireId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_SURVEY);
            psSql.setInt(1, questionnaireId);
            psSql.executeUpdate();

            psSql = connection.prepareStatement(SQL_GET_MAX_SURVEY_ID);
            ResultSet myRS = psSql.executeQuery();
            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_SURVEY_ID_MAX);
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }

        return -1;
    }

    public void resetQuestionnaire() {

        for (ArrayList<Question> questions : GlobalVars.questionsPerPanel) {
            for (Question question : questions) {
                question.setSubmittedAnswer(null);
                for (CheckBox checkbox : question.getAnswerCheckBoxes()) {
                    checkbox.setSelected(false);
                }

                TextField textField = question.getAnswerTextField();
                if (textField != null) {
                    textField.setText("");
                }

                TextArea textArea = question.getAnswerTextArea();
                if (textArea != null) {
                    textArea.setText("");
                }

                ListView<AnswerOption> list = question.getAnswerOptionListView();
                if (list != null) {
                    list.getItems().clear();
                }

            }
        }
    }

    private void createSurveyMultipleChoiceRelation(Connection connection, int surveyId, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_SURVEY_HAS_MULTIPLE_CHOICE_RELATION);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private void createSurveyAnswerOptionRelation(Connection connection, int answerId, int surveyMultipleChoiceRelationId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_SURVEY_HAS_ANSWER_OPTION_RELATION);
            psSql.setInt(1, answerId);
            psSql.setInt(2, surveyMultipleChoiceRelationId);
            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private Integer getSurveyQuestionRelationId(int surveyId, int questionId, QuestionType questionType) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_GET_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID : SQL_GET_SURVEY_HAS_ANSWER_OPTION_RELATION_ID);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            ResultSet myRS = psSql.executeQuery();
            if (myRS.next()) {
                return myRS.getInt(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                        SQL_COLUMN_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID : SQL_COLUMN_SURVEY_HAS_ANSWER_OPTION_RELATION_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void createSurveyShortAnswerRelation(Connection connection, int surveyId, int questionId, String answer) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_SURVEY_HAS_SHORT_ANSWER_RELATION);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            psSql.setString(3, answer);
            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private boolean existsSurveyShortAnswerRelation(int surveyId, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SURVEY_HAS_SHORT_ANSWER_RELATION_ID);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            ResultSet myRS = psSql.executeQuery();
            return myRS.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private List<Question> discardSecondDimension(List<SurveyPage> pages) {
        List<Question> results = new ArrayList<>();
        pages.forEach(page -> results.addAll(page.getQuestions()));

        return results;
    }

    public List<Survey> getSurveys(int questionnaireId, String fromDate, String toDate) {
        List<Survey> surveys = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SURVEYS_BY_QUESTIONNAIRE_ID);
            psSql.setString(1, fromDate);
            psSql.setString(2, toDate);
            psSql.setInt(3, questionnaireId);
            ResultSet myRS = psSql.executeQuery();
            while (myRS.next()) {
                Survey survey = new Survey();
                survey.setSurveyId(myRS.getInt(SQL_COLUMN_SURVEY_ID));
                survey.setCreationDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                survey.setQuestionnaireId(myRS.getInt(SQL_COLUMN_QUESTIONNAIRE_ID));
                surveys.add(survey);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return surveys;
    }

    public SubmittedAnswer getAnswer(int surveyId, Question question) {
        if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
            return getShortAnswerSubmittedAnswer(surveyId, question.getQuestionId());
        } else {
            return getMultipleChoiceSubmittedAnswer(surveyId, question.getQuestionId());
        }
    }

    private SubmittedAnswer getShortAnswerSubmittedAnswer(int surveyId, int questionId) {
        SubmittedAnswer submittedAnswer = new SubmittedAnswer();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SHORT_ANSWER_OF_SURVEY);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            ResultSet myRS = psSql.executeQuery();
            if (myRS.next()) {
                submittedAnswer = new SubmittedAnswer();
                submittedAnswer.setSubmittedAnswerText(myRS.getString(SQL_COLUMN_ANSWER));
                return submittedAnswer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return submittedAnswer;
    }

    private SubmittedAnswer getMultipleChoiceSubmittedAnswer(int surveyId, int questionId) {
        SubmittedAnswer submittedAnswer = new SubmittedAnswer();
        List<AnswerOption> answerOptions = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ANSWERS);
            psSql.setInt(1, surveyId);
            psSql.setInt(2, questionId);
            ResultSet myRS = psSql.executeQuery();
            while (myRS.next()) {
                AnswerOption answerOption = new AnswerOption();
                answerOption.setId(myRS.getInt(SQL_COLUMN_ANSWER_OPTION_ID));
                answerOption.setValue(myRS.getString(SQL_COLUMN_NAME));
                answerOptions.add(answerOption);
            }

            submittedAnswer.setSubmittedAnswerOptions(answerOptions);
            return submittedAnswer;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return submittedAnswer;
    }
}
