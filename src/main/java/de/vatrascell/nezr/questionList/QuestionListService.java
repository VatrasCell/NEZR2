package de.vatrascell.nezr.questionList;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.flag.FlagListService;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.question.AnswerOptionService;
import de.vatrascell.nezr.question.CategoryService;
import de.vatrascell.nezr.question.HeadlineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_ANSWER_OPTION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_ANSWER_OPTION_NAME;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_CATEGORY_NAME;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_CREATION_DATE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_HEADLINE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_HEADLINE_ID2;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_POSITION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_QUESTION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_BY_QUESTION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_DELETE_SHORT_ANSWER;
import static de.vatrascell.nezr.application.SqlStatement.SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_OTHER_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_IDS;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_OTHER_SHORT_ANSWER_QUESTIONNAIRE_RELATION_IDS;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SHORT_ANSWER_QUESTION;

@Service
@AllArgsConstructor
public class QuestionListService extends Database {

    private final AnswerOptionService answerOptionService;
    private final CategoryService categoryService;
    private final FlagListService flagListService;
    private final HeadlineService headlineService;

    public List<Question> getQuestions(int questionnaireId) {
        List<Question> questions = new ArrayList<>();
        questions.addAll(Objects.requireNonNull(getMultipleChoiceQuestions(questionnaireId)));
        questions.addAll(Objects.requireNonNull(getShortAnswerQuestions(questionnaireId)));

        questions.sort(Comparator.comparing(Question::getPosition));

        return questions;
    }

    public List<Question> getMultipleChoiceQuestions(int questionnaireId) {
        List<Question> questions = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_QUESTION);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                Question question = new Question();
                question.setQuestion(myRS.getString(SQL_COLUMN_QUESTION));
                question.setQuestionId(myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ID));
                question.setCategory(categoryService.getCategory(myRS.getString(SQL_COLUMN_CATEGORY_NAME)));
                question.setDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                question.setFlags(flagListService.getFlagList(
                        myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID),
                        QuestionType.MULTIPLE_CHOICE));
                question.setPosition(Integer.parseInt(myRS.getString(SQL_COLUMN_POSITION)));
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                question.setAnswerOptions(answerOptionService.getAnswerOptions(question.getQuestionId()));
                int headlineId = myRS.getInt(SQL_COLUMN_HEADLINE_ID);
                if (headlineId > 0) {
                    question.setHeadline(headlineService.getHeadline(headlineId));
                }
                question.setQuestionnaireId(questionnaireId);
                questions.add(question);
            }

            return questions;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Question> getShortAnswerQuestions(int questionnaireId) {
        List<Question> questions = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SHORT_ANSWER_QUESTION);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                Question question = new Question();
                question.setQuestion(myRS.getString(SQL_COLUMN_QUESTION));
                question.setQuestionId(myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID));
                question.setCategory(categoryService.getCategory(myRS.getString(SQL_COLUMN_CATEGORY_NAME)));
                question.setDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                question.setFlags(flagListService.getFlagList(
                        myRS.getInt(SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID),
                        QuestionType.SHORT_ANSWER));
                question.setPosition(Integer.parseInt(myRS.getString(SQL_COLUMN_POSITION)));
                question.setQuestionType(QuestionType.SHORT_ANSWER);
                int headlineId = myRS.getInt(SQL_COLUMN_HEADLINE_ID2);
                if (headlineId > 0) {
                    question.setHeadline(headlineService.getHeadline(headlineId));
                }
                question.setQuestionnaireId(questionnaireId);
                questions.add(question);
            }

            return questions;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<AnswerOption> getMultipleChoiceQuestionAnswers(int questionnaireId, int questionId) {
        List<AnswerOption> answerOptions = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                AnswerOption answerOption = new AnswerOption();
                answerOption.setId(myRS.getInt(SQL_COLUMN_ANSWER_OPTION_ID));
                answerOption.setValue(myRS.getString(SQL_COLUMN_ANSWER_OPTION_NAME));

                if (!answerOptions.contains(answerOption)) {
                    answerOptions.add(answerOption);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answerOptions;
    }

    public List<Integer> getQuestionsByQuestionnaireId(int questionnaireId, QuestionType questionType) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            List<Integer> results = new ArrayList<>();
            PreparedStatement psSql = myCon.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID : SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                results.add(myRS.getInt(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                        SQL_COLUMN_MULTIPLE_CHOICE_ID : SQL_COLUMN_SHORT_ANSWER_ID));
            }

            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean doesQuestionExistsInOtherQuestionnaire(int questionnaireId, int questionId, QuestionType questionType) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_GET_OTHER_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_IDS : SQL_GET_OTHER_SHORT_ANSWER_QUESTIONNAIRE_RELATION_IDS);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, questionId);

            return psSql.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void deleteQuestion(int questionnaireId, Question question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            deleteQuestion(myCon, questionnaireId, question.getQuestionId(), question.getQuestionType());

            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteQuestion(Connection connection, int questionnaireId, int questionId, QuestionType questionType) throws SQLException {
        //TODO refactor
        //QuestionService.deleteFlagsFromTargetQuestion(myCon, questionnaireId, questionId);

        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {

            deleteMultipleChoiceQuestionnaireRelation(connection, questionnaireId, questionId);

            if (!doesQuestionExistsInOtherQuestionnaire(questionnaireId, questionId, questionType)) {

                deleteMultipleChoiceHasAnswerRelation(connection, questionId);
                deleteMultipleChoiceQuestion(connection, questionId);
            }
        } else if (questionType.equals(QuestionType.SHORT_ANSWER)) {
            deleteShortAnswerQuestionnaireRelation(connection, questionnaireId, questionId);

            if (!doesQuestionExistsInOtherQuestionnaire(questionnaireId, questionId, questionType)) {

                deleteShortAnswerQuestion(connection, questionId);
            }
        }

        answerOptionService.deleteUnbindedAnswerOptions(connection);
    }

    private void deleteShortAnswerQuestion(Connection connection, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_DELETE_SHORT_ANSWER);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private void deleteMultipleChoiceQuestion(Connection connection, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private void deleteMultipleChoiceHasAnswerRelation(Connection connection, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_BY_QUESTION_ID);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private void deleteShortAnswerQuestionnaireRelation(Connection connection, int questionnaireId, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);

            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private void deleteMultipleChoiceQuestionnaireRelation(Connection connection, int questionnaireId, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);

            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }
}
