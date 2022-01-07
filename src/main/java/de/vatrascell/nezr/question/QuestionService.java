package de.vatrascell.nezr.question;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.flag.FlagList;
import de.vatrascell.nezr.flag.FlagListService;
import de.vatrascell.nezr.flag.React;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Category;
import de.vatrascell.nezr.model.Headline;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.validation.ValidationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_OPTION_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_POSITION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_SHORT_ANSWER;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION_WITH_VALIDATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MAX_MULTIPLE_CHOICE_POSITION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MAX_SHORT_ANSWER_POSITION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_IDS;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SHORT_ANSWER_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER;
import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION;

@Service
@AllArgsConstructor
public class QuestionService extends Database {

    private final CategoryService categoryService;
    private final HeadlineService headlineService;
    private final FlagListService flagListService;
    private final AnswerOptionService answerOptionService;
    private final ValidationService validationService;

    public int getMaxQuestionPosition(int questionnaireId) {
        int maxPosMc = Objects.requireNonNull(getMaxPosition(questionnaireId, QuestionType.MULTIPLE_CHOICE));
        int maxPosFf = Objects.requireNonNull(getMaxPosition(questionnaireId, QuestionType.SHORT_ANSWER));

        return Math.max(maxPosFf, maxPosMc);
    }

    private Integer getMaxPosition(int questionnaireId, QuestionType questionType) {
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
    /*public void getPossibleFlags(FlagList flags, QuestionEditParam param) {
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

    //TODO duplicateQuestion

    public void saveShortAnswerQuestion(int questionnaireId, Question question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            // de.vatrascell.nezr.react
            for (React react : question.getFlags().getReacts()) {
                if (question.getFlags().isRequired()) {
                    provideQuestionRequired(myCon, questionnaireId, react.getQuestionType());
                }
            }

            // category
            Category category = categoryService.provideCategory(myCon, question.getCategory().getName());

            //headline
            Headline headline = question.getHeadline() == null ? null : headlineService.provideHeadline(myCon, question.getHeadline().getName());

            // de.vatrascell.nezr.question
            Integer shortAnswerId = provideShortAnswerQuestion(myCon, question.getQuestion(), category.getId());

            categoryService.setCategoryOnQuestion(myCon, category.getId(), shortAnswerId, question.getQuestionType());

            if (headline != null) {
                headlineService.setHeadlineOnQuestion(myCon, headline.getId(), shortAnswerId, question.getQuestionType());
            }

            // questionnaire
            Integer relationId = getQuestionQuestionnaireRelationId(questionnaireId, shortAnswerId, question.getQuestionType());

            if (relationId != null) {
                flagListService.updateShortAnswerFlagList(myCon, relationId, question.getFlags());
                setPositionOnShortAnswerQuestionnaireRelation(myCon, question.getPosition(), relationId);
            } else {
                Integer validationId = null;
                if (question.getFlags().getValidation() != null) {
                    validationService.createValidation(myCon, question.getFlags().getValidation());
                    validationId = validationService.getLastValidationId();
                }
                createShortAnswerQuestionnaireRelation(myCon, questionnaireId, shortAnswerId, question.getPosition(), validationId);
            }

            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveMultipleChoice(int questionnaireId, Question question) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            // de.vatrascell.nezr.react
            for (React react : question.getFlags().getReacts()) {
                if (question.getFlags().isRequired()) {
                    provideQuestionRequired(myCon, questionnaireId, react.getQuestionType());
                }
            }

            // answerOptions
            question.getAnswerOptions().stream()
                    .filter(answerOption -> answerOption.getId() == null)
                    .forEach(answerOption -> {
                        try {
                            answerOption.setId(answerOptionService.provideAnswerOptionId(myCon, answerOption.getValue()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

            // category
            Category category = categoryService.provideCategory(myCon, question.getCategory().getName());

            //headline
            Headline headline = question.getHeadline() == null ? null : headlineService.provideHeadline(myCon, question.getHeadline().getName());

            // de.vatrascell.nezr.question
            Integer multipleChoiceId = provideMultipleChoiceQuestion(myCon, question.getQuestion(), category.getId());

            categoryService.setCategoryOnQuestion(myCon, category.getId(), multipleChoiceId, question.getQuestionType());
            if (headline != null) {
                headlineService.setHeadlineOnQuestion(myCon, headline.getId(), multipleChoiceId, question.getQuestionType());
            }

            // answers
            List<Integer> oldRelationIds = getMultipleChoiceAnswerOptionsRelationIds(Objects.requireNonNull(multipleChoiceId));
            List<Integer> newRelationIds = new ArrayList<>();

            for (AnswerOption answerOption : question.getAnswerOptions()) {
                Integer relationId = getMultipleChoiceAnswersRelationId(multipleChoiceId, answerOption.getId());
                if (relationId != null) {
                    newRelationIds.add(relationId);
                } else {
                    answerOptionService.createMultipleChoiceAnswerOptionsRelation(myCon, multipleChoiceId, answerOption.getId());
                }
            }

            for (int oldRelationId : oldRelationIds) {
                if (!newRelationIds.contains(oldRelationId)) {
                    answerOptionService.deleteMultipleChoiceAnswerOptionsRelation(myCon, oldRelationId);
                }
            }

            // questionnaire
            Integer relationId = getQuestionQuestionnaireRelationId(questionnaireId, multipleChoiceId, question.getQuestionType());

            if (relationId != null) {
                newRelationIds.add(relationId);

                flagListService.updateMultipleChoiceFlagList(myCon, relationId, question.getFlags());
                setPositionOnMultipleChoiceQuestionnaireRelation(myCon, question.getPosition(), relationId);
            } else {
                createMultipleChoiceQuestionnaireRelation(myCon, questionnaireId, multipleChoiceId, question.getPosition());
                relationId = getQuestionQuestionnaireRelationId(questionnaireId, multipleChoiceId, question.getQuestionType());

                flagListService.createMultipleChoiceFlagList(myCon, relationId, question.getFlags());
            }

            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doQuestionExistsInQuestionnaire(String question, int questionnaireId, QuestionType questionType) {
        Integer questionId = getQuestionId(question, questionType);
        if (questionId != null) {
            return getQuestionQuestionnaireRelationId(questionnaireId, questionId, questionType) != null;
        }

        return false;
    }

    public List<Integer> getMultipleChoiceAnswerOptionsRelationIds(int multipleChoiceId) {
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

    public Integer getMultipleChoiceAnswersRelationId(int multipleChoiceId, int answerId) {
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

    public Integer getQuestionQuestionnaireRelationId(int questionnaireId, int questionId, QuestionType questionType) {
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

    public Integer getQuestionId(String question, QuestionType questionType) {
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

    private void deleteFlagsFromTargetQuestion(int questionnaireId, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);
            deleteMultipleChoiceReactFlagsFromTargetQuestion(myCon, questionnaireId, questionId);
            deleteShortAnswerReactFlagsFromTargetQuestion(myCon, questionnaireId, questionId);
            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteShortAnswerReactFlagsFromTargetQuestion(Connection connection, int questionnaireId, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionId);
            psSql.setInt(3, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                FlagList flags = flagListService.getFlagList(
                        myRS.getInt(SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID),
                        QuestionType.SHORT_ANSWER);
                int targetQuestionId = myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID);

                if (flags != null && !flags.getReacts().isEmpty()) {
                    flags.setReacts(null);

                    flagListService.setQuestionRequired(connection, getQuestionQuestionnaireRelationId(questionnaireId, questionId, QuestionType.SHORT_ANSWER), QuestionType.SHORT_ANSWER);
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private void deleteMultipleChoiceReactFlagsFromTargetQuestion(Connection connection, int questionnaireId, int questionId) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE);
            psSql.setInt(1, questionId);
            psSql.setInt(2, questionId);
            psSql.setInt(3, questionnaireId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                FlagList flags = flagListService.getFlagList(
                        myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID),
                        QuestionType.MULTIPLE_CHOICE);
                int targetQuestionId = myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ID);

                if (flags != null && !flags.getReacts().isEmpty()) {
                    flags.setReacts(null);

                    flagListService.setQuestionRequired(connection, getQuestionQuestionnaireRelationId(questionnaireId, questionId, QuestionType.MULTIPLE_CHOICE), QuestionType.MULTIPLE_CHOICE);
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    private void provideQuestionRequired(Connection connection, int questionnaireId, QuestionType questionType) throws SQLException {
        FlagList flagList = flagListService.getFlagList(questionnaireId, questionType);
        if (!flagList.isRequired()) {
            flagList.setRequired(true);

            flagListService.setQuestionRequired(connection, flagList.getId(), questionType);
        }
    }

    private void createMultipleChoiceQuestion(Connection connection, String question, int categoryId) throws SQLException {
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

    private Integer provideMultipleChoiceQuestion(Connection connection, String question, int categoryId) throws SQLException {
        Integer multipleChoiceId = getQuestionId(question, QuestionType.MULTIPLE_CHOICE);

        if (multipleChoiceId == null) {
            createMultipleChoiceQuestion(connection, question, categoryId);
            multipleChoiceId = getQuestionId(question, QuestionType.MULTIPLE_CHOICE);
        }

        return multipleChoiceId;
    }

    private void createShortAnswerQuestion(Connection connection, String question, int categoryId) throws SQLException {
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

    private Integer provideShortAnswerQuestion(Connection connection, String question, int categoryId) throws SQLException {
        Integer shortAnswerId = getQuestionId(question, QuestionType.SHORT_ANSWER);

        if (shortAnswerId == null) {
            createShortAnswerQuestion(connection, question, categoryId);
            shortAnswerId = getQuestionId(question, QuestionType.SHORT_ANSWER);
        }

        return shortAnswerId;
    }

    private void setPositionOnMultipleChoiceQuestionnaireRelation(Connection connection, int position, int relationId) throws SQLException {
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

    private void setPositionOnShortAnswerQuestionnaireRelation(Connection connection, int position, int relationId) throws SQLException {
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

    private void createMultipleChoiceQuestionnaireRelation(Connection connection, int questionnaireId, int multipleChoiceId, int position) throws SQLException {
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

    private void createShortAnswerQuestionnaireRelation(Connection connection, int questionnaireId, int shortAnswerId, int position, Integer validationId) throws SQLException {
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
}
