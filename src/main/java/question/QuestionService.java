package question;

import application.Database;
import flag.FlagList;
import flag.FlagListService;
import model.AnswerOption;
import model.Category;
import model.Headline;
import model.Question;
import model.QuestionType;
import validation.ValidationService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_OPTION_RELATION_ID;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_COLUMN_POSITION;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_CREATE_SHORT_ANSWER;
import static application.SqlStatement.SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION_WITH_VALIDATION;
import static application.SqlStatement.SQL_GET_MAX_MULTIPLE_CHOICE_POSITION;
import static application.SqlStatement.SQL_GET_MAX_SHORT_ANSWER_POSITION;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_IDS;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER;
import static application.SqlStatement.SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION;

public class QuestionService extends Database {


    public static int getCountPosition(int questionnaireId) {
        int maxPosMc = Objects.requireNonNull(getMaxPosition(questionnaireId, QuestionType.MULTIPLE_CHOICE));
        int maxPosFf = Objects.requireNonNull(getMaxPosition(questionnaireId, QuestionType.SHORT_ANSWER));

        return Math.max(maxPosFf, maxPosMc);
    }

    private static Integer getMaxPosition(int questionnaireId, QuestionType questionType) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_GET_MAX_MULTIPLE_CHOICE_POSITION : SQL_GET_MAX_SHORT_ANSWER_POSITION);
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

    // TODO rework method
    /*public static void getPossibleFlags(FlagList flags, QuestionEditParam param) {
        if (param.isRequired()) {
            flags.add(new Symbol(SymbolType.REQUIRED));
        }
        if (param.isList()) {
            flags.add(new Symbol(SymbolType.LIST));
        }
        if (param.isMultipleChoice()) {
            flags.add(new Symbol(SymbolType.MC));
        }
        if (param.isTextArea()) {
            flags.add(new Symbol(SymbolType.TEXT));
        }
        if (param.isYesNoQuestion()) {
            flags.add(new Symbol(SymbolType.JN));
            if (param.isSingleLine()) {
                flags.add(new Symbol(SymbolType.JNExcel));
            }
        }

        /*if (param.isNumeric()) {
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

        if (param.isEvaluationQuestion()) {
            flags.add(new Symbol(SymbolType.B));
        }
    }*/

    public static void deleteFlagsFromTargetQuestion(int questionnaireId, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);
            deleteMultipleChoiceReactFlagsFromTargetQuestion(myCon, questionnaireId, questionId);
            deleteShortAnswerReactFlagsFromTargetQuestion(myCon, questionnaireId, questionId);
            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteShortAnswerReactFlagsFromTargetQuestion(Connection connection, int questionnaireId, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionId);
            psSql.setInt(3, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                FlagList flags = FlagListService.getFlagList(
                        myRS.getInt(SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID),
                        QuestionType.SHORT_ANSWER);
                int targetQuestionId = myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID);

                if (flags != null && !flags.getReacts().isEmpty()) {
                    flags.setReacts(null);

                    FlagListService.setQuestionRequired(connection, questionnaireId, targetQuestionId, QuestionType.SHORT_ANSWER);
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private static void deleteMultipleChoiceReactFlagsFromTargetQuestion(Connection connection, int questionnaireId, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionId);
            psSql.setInt(3, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                FlagList flags = FlagListService.getFlagList(
                        myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID),
                        QuestionType.MULTIPLE_CHOICE);
                int targetQuestionId = myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ID);

                if (flags != null && !flags.getReacts().isEmpty()) {
                    flags.setReacts(null);

                    FlagListService.setQuestionRequired(connection, questionnaireId, targetQuestionId, QuestionType.MULTIPLE_CHOICE);
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public static void provideQuestionRequired(int questionnaireId, QuestionType questionType) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            FlagList flagList = FlagListService.getFlagList(questionnaireId, questionType);
            if (!flagList.isRequired()) {
                flagList.setRequired(true);

                FlagListService.setQuestionRequired(myCon, flagList.getId(), questionType);
            }

            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isQuestionRequired(int questionnaireId, QuestionType questionType, int questionId) {
        return FlagListService.getFlagList(questionnaireId, questionId, questionType).isRequired();
    }

    //TODO duplicateQuestion

    public static void saveShortAnswerQuestion(int questionnaireId, Question question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);
            // category
            Category category = CategoryService.provideCategory(myCon, question.getCategory().getName());

            //headline
            Headline headline = question.getHeadline() == null ? null : HeadlineService.provideHeadline(myCon, question.getHeadline().getName());

            // question
            Integer shortAnswerId = provideShortAnswerQuestion(myCon, question.getQuestion(), category.getId());

            CategoryService.setCategoryOnQuestion(myCon, category.getId(), shortAnswerId, question.getQuestionType());

            if (headline != null) {
                HeadlineService.setHeadlineOnQuestion(myCon, headline.getId(), shortAnswerId, question.getQuestionType());
            }

            // questionnaire
            Integer relationId = getQuestionQuestionnaireRelationId(questionnaireId, shortAnswerId, question.getQuestionType());

            if (relationId != null) {
                FlagListService.updateShortAnswerFlagList(myCon, relationId, question.getFlags());
                setPositionOnShortAnswerQuestionnaireRelation(myCon, question.getPosition(), relationId);
            } else {
                Integer validationId = null;
                if (question.getFlags().getValidation() != null) {
                    ValidationService.createValidation(myCon, question.getFlags().getValidation());
                    validationId = ValidationService.getLastValidationId();
                }
                createShortAnswerQuestionnaireRelation(myCon, questionnaireId, shortAnswerId, question.getPosition(), validationId);
            }

            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveMultipleChoice(int questionnaireId, Question question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            // answerOptions
            question.getAnswerOptions().stream()
                    .filter(answerOption -> answerOption.getId() == null)
                    .forEach(answerOption -> {
                        try {
                            answerOption.setId(AnswerOptionService.provideAnswerOptionId(myCon, answerOption.getValue()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

            // category
            Category category = CategoryService.provideCategory(myCon, question.getCategory().getName());

            //headline
            Headline headline = question.getHeadline() == null ? null : HeadlineService.provideHeadline(myCon, question.getHeadline().getName());

            // question
            Integer multipleChoiceId = provideMultipleChoiceQuestion(myCon, question.getQuestion(), category.getId());

            CategoryService.setCategoryOnQuestion(myCon, category.getId(), multipleChoiceId, question.getQuestionType());
            if (headline != null) {
                HeadlineService.setHeadlineOnQuestion(myCon, headline.getId(), multipleChoiceId, question.getQuestionType());
            }

            // answers
            List<Integer> oldRelationIds = getMultipleChoiceAnswerOptionsRelationIds(Objects.requireNonNull(multipleChoiceId));
            List<Integer> newRelationIds = new ArrayList<>();

            for (AnswerOption answerOption : question.getAnswerOptions()) {
                Integer relationId = getMultipleChoiceAnswersRelationId(multipleChoiceId, answerOption.getId());
                if (relationId != null) {
                    newRelationIds.add(relationId);
                } else {
                    AnswerOptionService.createMultipleChoiceAnswerOptionsRelation(myCon, multipleChoiceId, answerOption.getId());
                }
            }

            for (int oldRelationId : oldRelationIds) {
                if (!newRelationIds.contains(oldRelationId)) {
                    AnswerOptionService.deleteMultipleChoiceAnswerOptionsRelation(myCon, oldRelationId);
                }
            }

            // questionnaire
            Integer relationId = getQuestionQuestionnaireRelationId(questionnaireId, multipleChoiceId, question.getQuestionType());

            if (relationId != null) {
                newRelationIds.add(relationId);

                FlagListService.updateMultipleChoiceFlagList(myCon, relationId, question.getFlags());
                setPositionOnMultipleChoiceQuestionnaireRelation(myCon, question.getPosition(), relationId);
            } else {
                createMultipleChoiceQuestionnaireRelation(myCon, questionnaireId, multipleChoiceId, question.getPosition());
                relationId = getQuestionQuestionnaireRelationId(questionnaireId, multipleChoiceId, question.getQuestionType());

                FlagListService.createMultipleChoiceFlagList(myCon, relationId, question.getFlags());
            }

            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getQuestionId(String question, QuestionType questionType) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_GET_MULTIPLE_CHOICE_ID : SQL_GET_SHORT_ANSWER_ID);
            psSql.setString(1, question);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                        SQL_COLUMN_MULTIPLE_CHOICE_ID : SQL_COLUMN_SHORT_ANSWER_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createMultipleChoiceQuestion(Connection connection, String question, int categoryId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_MULTIPLE_CHOICE);
            psSql.setString(1, question);
            psSql.setInt(2, categoryId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public static Integer provideMultipleChoiceQuestion(Connection connection, String question, int categoryId) throws SQLException {
        Integer multipleChoiceId = getQuestionId(question, QuestionType.MULTIPLE_CHOICE);

        if (multipleChoiceId == null) {
            createMultipleChoiceQuestion(connection, question, categoryId);
            multipleChoiceId = getQuestionId(question, QuestionType.MULTIPLE_CHOICE);
        }

        return multipleChoiceId;
    }

    public static void createShortAnswerQuestion(Connection connection, String question, int categoryId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_SHORT_ANSWER);
            psSql.setString(1, question);
            psSql.setInt(2, categoryId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public static Integer provideShortAnswerQuestion(Connection connection, String question, int categoryId) throws SQLException {
        Integer shortAnswerId = getQuestionId(question, QuestionType.SHORT_ANSWER);

        if (shortAnswerId == null) {
            createShortAnswerQuestion(connection, question, categoryId);
            shortAnswerId = getQuestionId(question, QuestionType.SHORT_ANSWER);
        }

        return shortAnswerId;
    }

    public static List<Integer> getMultipleChoiceAnswerOptionsRelationIds(int multipleChoiceId) {
        List<Integer> results = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_IDS);
            psSql.setInt(1, multipleChoiceId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                results.add(myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_OPTION_RELATION_ID));
            }
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static Integer getMultipleChoiceAnswersRelationId(int multipleChoiceId, int answerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_ID);
            psSql.setInt(1, multipleChoiceId);
            psSql.setInt(2, answerId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_OPTION_RELATION_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getQuestionQuestionnaireRelationId(int questionnaireId, int questionId, QuestionType questionType) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID : SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, questionId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                        SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID : SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setPositionOnMultipleChoiceQuestionnaireRelation(Connection connection, int position, int relationId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, position);
            psSql.setInt(2, relationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public static void setPositionOnShortAnswerQuestionnaireRelation(Connection connection, int position, int relationId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, position);
            psSql.setInt(2, relationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public static void createMultipleChoiceQuestionnaireRelation(Connection connection, int questionnaireId, int multipleChoiceId, int position) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, multipleChoiceId);
            psSql.setInt(3, position);
            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public static void createShortAnswerQuestionnaireRelation(Connection connection, int questionnaireId, int shortAnswerId, int position, Integer validationId) throws SQLException {
        try {
            String statement = validationId != null ? SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION_WITH_VALIDATION : SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
            PreparedStatement psSql = connection.prepareStatement(statement);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, shortAnswerId);
            psSql.setInt(3, position);
            if (validationId != null) {
                psSql.setInt(4, validationId);
            }
            psSql.execute();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public static boolean doQuestionExistsInQuestionnaire(String question, int questionnaireId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            Integer questionId = QuestionService.getQuestionId(question, questionType);
            if (questionId != null) {
                return QuestionService.getQuestionQuestionnaireRelationId(questionnaireId, questionId, questionType) != null;
            }
        } else {
            Integer questionId = QuestionService.getQuestionId(question, questionType);
            if (questionId != null) {
                return QuestionService.getQuestionQuestionnaireRelationId(questionnaireId, questionId, questionType) != null;
            }
        }

        return false;
    }
}
