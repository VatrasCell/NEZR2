package questionList;

import application.Database;
import flag.FlagList;
import flag.React;
import model.Answer;
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
import java.util.Optional;

import static application.SqlStatement.SQL_COLUMN_ANSWER_ID;
import static application.SqlStatement.SQL_COLUMN_ANSWER_NAME;
import static application.SqlStatement.SQL_COLUMN_CATEGORY_NAME;
import static application.SqlStatement.SQL_COLUMN_CREATION_DATE;
import static application.SqlStatement.SQL_COLUMN_FLAGS;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_COLUMN_POSITION;
import static application.SqlStatement.SQL_COLUMN_QUESTION;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_QUESTION_ID;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER_HAS_ANSWERS_RELATION_BY_QUESTION_ID;
import static application.SqlStatement.SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_GET_HEADLINES;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTION;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS;
import static application.SqlStatement.SQL_GET_OTHER_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_IDS;
import static application.SqlStatement.SQL_GET_OTHER_SHORT_ANSWER_QUESTIONNAIRE_RELATION_IDS;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_QUESTION;
import static application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER;
import static application.SqlStatement.SQL_UPDATE_MULTIPLE_CHOICE_FLAGS;
import static application.SqlStatement.SQL_UPDATE_SHORT_ANSWERS_FLAGS;

public class QuestionListService extends Database {

    public static List<Question> getQuestions(int questionnaireId) {
        List<Question> questions = new ArrayList<>();
        List<Headline> headlines = getHeadlines(questionnaireId);
        questions.addAll(Objects.requireNonNull(getMultipleChoiceQuestions(questionnaireId)));
        questions.addAll(Objects.requireNonNull(getShortAnswerQuestions(questionnaireId)));

        questions.sort(Comparator.comparing(Question::getPosition));

        headlines.forEach(
                headline -> {
                    Optional<Question> foundQuestion = questions.stream().filter(question -> question.getPosition() == headline.getPosition()).findFirst();
                    foundQuestion.ifPresent(question -> question.setHeadline(headline.getHeadline()));
                }
        );

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
                question.setCategory(myRS.getString(SQL_COLUMN_CATEGORY_NAME));
                question.setDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                question.setFlags(new FlagList(myRS.getString(SQL_COLUMN_FLAGS)));
                question.setPosition(Integer.parseInt(myRS.getString(SQL_COLUMN_POSITION)));
                question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
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
                question.setCategory(myRS.getString(SQL_COLUMN_CATEGORY_NAME));
                question.setDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                question.setFlags(new FlagList(myRS.getString(SQL_COLUMN_FLAGS)));
                question.setPosition(Integer.parseInt(myRS.getString(SQL_COLUMN_POSITION)));
                question.setQuestionType(QuestionType.SHORT_ANSWER);
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
            psSql.setInt(1, questionnaireId);
            ResultSet myRS = psSql.executeQuery();
            while (myRS.next()) {
                int position = Integer.parseInt(myRS.getString(SQL_COLUMN_POSITION));
                String value = myRS.getString(SQL_COLUMN_QUESTION);
                headlines.add(new Headline(position, value));
            }
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

    public static List<Answer> getMultipleChoiceQuestionAnswers(int questionnaireId, int questionId) {
        List<Answer> answers = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                Answer answer = new Answer();
                answer.setId(myRS.getInt(SQL_COLUMN_ANSWER_ID));
                answer.setValue(myRS.getString(SQL_COLUMN_ANSWER_NAME));

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

        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                FlagList flags = new FlagList(myRS.getString(SQL_COLUMN_FLAGS));
                int targetQuestionId = myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID);
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
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {

            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                FlagList flags = new FlagList(myRS.getString(SQL_COLUMN_FLAGS));
                int targetQuestionId = myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ID);
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
