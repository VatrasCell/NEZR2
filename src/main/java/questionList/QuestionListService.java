package questionList;

import application.Database;
import application.NotificationController;
import flag.FlagListService;
import message.MessageId;
import model.AnswerOption;
import model.Headline;
import model.Question;
import model.QuestionType;
import question.QuestionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static application.SqlStatement.SQL_COLUMN_ANSWER_OPTION_ID;
import static application.SqlStatement.SQL_COLUMN_ANSWER_OPTION_NAME;
import static application.SqlStatement.SQL_COLUMN_CATEGORY_NAME;
import static application.SqlStatement.SQL_COLUMN_CREATION_DATE;
import static application.SqlStatement.SQL_COLUMN_HEADLINE_ID;
import static application.SqlStatement.SQL_COLUMN_HEADLINE_ID2;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_COLUMN_NAME;
import static application.SqlStatement.SQL_COLUMN_POSITION;
import static application.SqlStatement.SQL_COLUMN_QUESTION;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_CREATE_HEADLINE;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_QUESTION_ID;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER_HAS_ANSWERS_RELATION_BY_QUESTION_ID;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_GET_HEADLINES;
import static application.SqlStatement.SQL_GET_HEADLINE_BY_ID;
import static application.SqlStatement.SQL_GET_HEADLINE_BY_NAME;
import static application.SqlStatement.SQL_GET_HEADLINE_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTION;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS;
import static application.SqlStatement.SQL_GET_OTHER_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_IDS;
import static application.SqlStatement.SQL_GET_OTHER_SHORT_ANSWER_QUESTIONNAIRE_RELATION_IDS;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_QUESTION;

public class QuestionListService extends Database {

    public static List<Question> getQuestions(int questionnaireId) {
        List<Question> questions = new ArrayList<>();
        questions.addAll(Objects.requireNonNull(getMultipleChoiceQuestions(questionnaireId)));
        questions.addAll(Objects.requireNonNull(getShortAnswerQuestions(questionnaireId)));

        questions.sort(Comparator.comparing(Question::getPosition));

        return questions;
    }

    public static List<Question> getMultipleChoiceQuestions(int questionnaireId) {
        List<Question> questions = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_QUESTION);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                Question question = new Question();
                question.setQuestion(myRS.getString(SQL_COLUMN_QUESTION));
                question.setQuestionId(myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ID));
                question.setCategory(QuestionService.provideCategory(myRS.getString(SQL_COLUMN_CATEGORY_NAME)));
                question.setDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                question.setFlags(FlagListService.getFlagList(
                        myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID),
                        QuestionType.MULTIPLE_CHOICE));
                question.setPosition(Integer.parseInt(myRS.getString(SQL_COLUMN_POSITION)));
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                question.setAnswerOptions(QuestionService.getAnswerOptions(question.getQuestionId()));
                int headlineId = myRS.getInt(SQL_COLUMN_HEADLINE_ID);
                if (headlineId > 0) {
                    question.setHeadline(getHeadline(headlineId));
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

    public static List<Question> getShortAnswerQuestions(int questionnaireId) {
        List<Question> questions = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SHORT_ANSWER_QUESTION);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                Question question = new Question();
                question.setQuestion(myRS.getString(SQL_COLUMN_QUESTION));
                question.setQuestionId(myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID));
                question.setCategory(QuestionService.provideCategory(myRS.getString(SQL_COLUMN_CATEGORY_NAME)));
                question.setDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                question.setFlags(FlagListService.getFlagList(
                        myRS.getInt(SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID),
                        QuestionType.SHORT_ANSWER));
                question.setPosition(Integer.parseInt(myRS.getString(SQL_COLUMN_POSITION)));
                question.setQuestionType(QuestionType.SHORT_ANSWER);
                int headlineId = myRS.getInt(SQL_COLUMN_HEADLINE_ID2);
                if (headlineId > 0) {
                    question.setHeadline(getHeadline(headlineId));
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

    public static List<Headline> getHeadlines(int questionnaireId) {
        List<Headline> headlines = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_HEADLINES);
            ResultSet myRS = psSql.executeQuery();
            while (myRS.next()) {
                int id = myRS.getInt(SQL_COLUMN_HEADLINE_ID);
                String name = myRS.getString(SQL_COLUMN_NAME);
                headlines.add(new Headline(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return headlines;
    }

    public static Headline getHeadline(int headlineId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_HEADLINE_BY_ID);
            psSql.setInt(1, headlineId);
            ResultSet myRS = psSql.executeQuery();
            if (myRS.next()) {
                int id = myRS.getInt(SQL_COLUMN_HEADLINE_ID);
                String name = myRS.getString(SQL_COLUMN_NAME);
                return new Headline(id, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Headline getHeadlineByName(String name) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_HEADLINE_BY_NAME);
            psSql.setString(1, name);
            ResultSet myRS = psSql.executeQuery();
            if (myRS.next()) {
                return new Headline(
                        myRS.getInt(SQL_COLUMN_HEADLINE_ID),
                        myRS.getString(SQL_COLUMN_NAME)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createHeadline(String name) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_HEADLINE);
            psSql.setString(1, name);
            psSql.execute();

            NotificationController
                    .createMessage(MessageId.TITLE_CREATE_HEADLINE, MessageId.MESSAGE_CREATE_HEADLINE, name);
        } catch (SQLException e) {
            e.printStackTrace();
            NotificationController
                    .createErrorMessage(MessageId.TITLE_CREATE_HEADLINE, MessageId.MESSAGE_UNDEFINED_ERROR);
        }
    }

    public static boolean checkHeadline(String name) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_HEADLINE_ID);
            psSql.setString(1, name);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createUniqueHeadline(String name) {
        if (checkHeadline(name)) {
            NotificationController
                    .createErrorMessage(MessageId.TITLE_CREATE_HEADLINE, MessageId.MESSAGE_CATEGORY_HEADLINE_EXISTS);
        } else {
            createHeadline(name);
        }
    }

    public static Headline provideHeadline(String name) {
        Headline headline = getHeadlineByName(name);

        if (headline == null) {
            createUniqueHeadline(name);
            headline = getHeadlineByName(name);
        }

        return headline;
    }

    public static void deleteQuestion(int questionnaireId, Question question) {
        deleteQuestion(questionnaireId, question.getQuestionId(), question.getQuestionType());
    }

    public static void deleteQuestion(int questionnaireId, int questionId, QuestionType questionType) {
        QuestionService.deleteFlagsFromTargetQuestion(questionnaireId, questionId);

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

        QuestionService.deleteUnbindedAnswers();
    }

    public static void deleteShortAnswerQuestion(int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_SHORT_ANSWER);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteShortAnswerHasAnswerRelation(int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_SHORT_ANSWER_HAS_ANSWERS_RELATION_BY_QUESTION_ID);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceQuestion(int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceHasAnswerRelation(int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_QUESTION_ID);
            psSql.setInt(1, questionId);

            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean doesMultipleChoiceQuestionExistsInOtherQuestionnaire(int questionnaireId, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
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
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
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
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceQuestionnaireRelation(int questionnaireId, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<AnswerOption> getMultipleChoiceQuestionAnswers(int questionnaireId, int questionId) {
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

    public static List<Integer> getMultipleChoiceQuestionsByQuestionnaireId(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            List<Integer> results = new ArrayList<>();
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                results.add(myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ID));
            }

            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Integer> getShortAnswerQuestionsByQuestionnaireId(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            List<Integer> results = new ArrayList<>();
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID);
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                results.add(myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID));
            }

            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
