package question;

import application.Database;
import application.NotificationController;
import flag.FlagList;
import flag.FlagListService;
import message.MessageId;
import model.AnswerOption;
import model.Category;
import model.Headline;
import model.Question;
import model.QuestionType;
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

import static application.SqlStatement.SQL_COLUMN_ANSWER_OPTION_ID;
import static application.SqlStatement.SQL_COLUMN_CATEGORY_ID;
import static application.SqlStatement.SQL_COLUMN_CATEGORY_NAME;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_RELATION_ID;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_COLUMN_NAME;
import static application.SqlStatement.SQL_COLUMN_POSITION;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_CREATE_ANSWER;
import static application.SqlStatement.SQL_CREATE_CATEGORY;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_ANSWERS_RELATION;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_CREATE_SHORT_ANSWER;
import static application.SqlStatement.SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_ID;
import static application.SqlStatement.SQL_DELETE_UNBINDED_ANSWERS;
import static application.SqlStatement.SQL_GET_ANSWER_ID;
import static application.SqlStatement.SQL_GET_ANSWER_OPTION;
import static application.SqlStatement.SQL_GET_ANSWER_OPTIONS;
import static application.SqlStatement.SQL_GET_CATEGORIES;
import static application.SqlStatement.SQL_GET_CATEGORY_BY_NAME;
import static application.SqlStatement.SQL_GET_MAX_MULTIPLE_CHOICE_POSITION;
import static application.SqlStatement.SQL_GET_MAX_SHORT_ANSWER_POSITION;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_IDS;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID;
import static application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER;
import static application.SqlStatement.SQL_SET_CATEGORY_ON_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_SET_CATEGORY_ON_SHORT_ANSWER;
import static application.SqlStatement.SQL_SET_HEADLINE_ON_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_SET_HEADLINE_ON_SHORT_ANSWER;
import static application.SqlStatement.SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION;
import static application.SqlStatement.SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION;

public class QuestionService extends Database {

    public static List<AnswerOption> getAnswerOptions(int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_ANSWER_OPTIONS);
            psSql.setInt(1, questionId);

            ResultSet myRS = psSql.executeQuery();
            ArrayList<AnswerOption> answerOptions = new ArrayList<>();

            while (myRS.next()) {
                AnswerOption answerOption = new AnswerOption();
                answerOption.setId(myRS.getInt(SQL_COLUMN_ANSWER_OPTION_ID));
                answerOption.setValue(myRS.getString(SQL_COLUMN_NAME));
                answerOptions.add(answerOption);
            }
            return answerOptions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AnswerOption getAnswerOption(String name) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_ANSWER_OPTION);
            psSql.setString(1, name);

            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                AnswerOption answerOption = new AnswerOption();
                answerOption.setId(myRS.getInt(SQL_COLUMN_ANSWER_OPTION_ID));
                answerOption.setValue(myRS.getString(SQL_COLUMN_NAME));
                return answerOption;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


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

    public static List<Category> getCategories() {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            Statement mySQL = myCon.createStatement();
            ResultSet myRS = mySQL.executeQuery(SQL_GET_CATEGORIES);
            List<Category> categories = new ArrayList<>();

            while (myRS.next()) {
                Category category = new Category(
                        myRS.getInt(SQL_COLUMN_CATEGORY_ID),
                        myRS.getString(SQL_COLUMN_CATEGORY_NAME)
                );
                categories.add(category);
            }

            return categories;
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
        deleteMultipleChoiceReactFlagsFromTargetQuestion(questionnaireId, questionId);
        deleteShortAnswerReactFlagsFromTargetQuestion(questionnaireId, questionId);
    }

    private static void deleteShortAnswerReactFlagsFromTargetQuestion(int questionnaireId, int questionId) {

        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER);
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

                    FlagListService.setQuestionRequired(questionnaireId, targetQuestionId, QuestionType.SHORT_ANSWER);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteMultipleChoiceReactFlagsFromTargetQuestion(int questionnaireId, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {

            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE);
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

                    FlagListService.setQuestionRequired(questionnaireId, targetQuestionId, QuestionType.MULTIPLE_CHOICE);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void provideQuestionRequired(int questionnaireId, QuestionType questionType) {
        FlagList flagList = FlagListService.getFlagList(questionnaireId, questionType);
        if (!flagList.isRequired()) {
            flagList.setRequired(true);

            FlagListService.setQuestionRequired(flagList.getId(), questionType);
        }
    }

    public static boolean isQuestionRequired(int questionnaireId, QuestionType questionType, int questionId) {
        return FlagListService.getFlagList(questionnaireId, questionId, questionType).isRequired();
    }

    //TODO duplicateQuestion

    public static void saveShortAnswerQuestion(int questionnaireId, Question question) {

        // category
        Category category = provideCategory(question.getCategory().getName());

        //headline
        Headline headline = question.getHeadline() == null ? null : QuestionListService.provideHeadline(question.getHeadline().getName());

        // question
        Integer shortAnswerId = provideShortAnswerQuestion(question.getQuestion(), category.getId());

        setCategoryOnShortAnswer(category.getId(), shortAnswerId);
        if (headline != null) {
            setHeadlineOnShortAnswer(headline.getId(), shortAnswerId);
        }

        // questionnaire
        Integer relationId = getShortAnswerQuestionnaireRelationId(questionnaireId, shortAnswerId);

        if (relationId != null) {
            FlagListService.updateShortAnswerFlagList(relationId, question.getFlags());
            setPositionOnShortAnswerQuestionnaireRelation(question.getPosition(), relationId);
        } else {
            createShortAnswerQuestionnaireRelation(questionnaireId, shortAnswerId, question.getPosition(), question.getFlags().createFlagString());
        }
    }

    public static void saveMultipleChoice(int questionnaireId, Question question, ArrayList<AnswerOption> answerOptions) {

        // category
        Category category = provideCategory(question.getCategory().getName());

        //headline
        Headline headline = question.getHeadline() == null ? null : QuestionListService.provideHeadline(question.getHeadline().getName());

        // question
        Integer multipleChoiceId = provideMultipleChoiceQuestion(question.getQuestion(), category.getId());

        setCategoryOnMultipleChoice(category.getId(), multipleChoiceId);
        if (headline != null) {
            setHeadlineOnMultipleChoice(headline.getId(), multipleChoiceId);
        }

        // answers
        List<Integer> oldRelationIds = getMultipleChoiceAnswersRelationIds(Objects.requireNonNull(multipleChoiceId));
        List<Integer> newRelationIds = new ArrayList<>();

        for (AnswerOption answerOption : answerOptions) {
            Integer relationId = getMultipleChoiceAnswersRelationId(multipleChoiceId, answerOption.getId());
            if (relationId != null) {
                newRelationIds.add(relationId);
            } else {
                createMultipleChoiceAnswersRelation(multipleChoiceId, answerOption.getId());
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

            FlagListService.updateMultipleChoiceFlagList(relationId, question.getFlags());
            setPositionOnMultipleChoiceQuestionnaireRelation(question.getPosition(), relationId);
        } else {
            createMultipleChoiceQuestionnaireRelation(questionnaireId, multipleChoiceId, question.getPosition(), question.getFlags().createFlagString());
        }
    }

    public static void saveEvaluationQuestion(int questionnaireId, Question question) {

        // category
        Category category = provideCategory(question.getCategory().getName());

        //headline
        Headline headline = question.getHeadline() == null ? null : QuestionListService.provideHeadline(question.getHeadline().getName());

        // question
        Integer evaluationQuestionId = provideMultipleChoiceQuestion(question.getQuestion(), category.getId());

        setCategoryOnMultipleChoice(category.getId(), evaluationQuestionId);
        if (headline != null) {
            setHeadlineOnMultipleChoice(headline.getId(), evaluationQuestionId);
        }

        // questionnaire
        Integer relationId = getMultipleChoiceQuestionnaireRelationId(questionnaireId, Objects.requireNonNull(evaluationQuestionId));

        if (relationId != null) {
            FlagListService.updateMultipleChoiceFlagList(relationId, question.getFlags());
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

    public static int provideAnswerId(String answer) {
        Integer id;

        id = getAnswerId(Objects.requireNonNull(answer));

        if (id == null) {
            createAnswer(answer);
            id = getAnswerId(answer);
        }

        return Objects.requireNonNull(id);
    }

    public static void deleteAnswers(ArrayList<Integer> answerIds, int multipleChoiceId) {

        for (Integer answerId : answerIds) {
            deleteMultipleChoiceAnswersRelation(answerId, multipleChoiceId);
        }

        deleteUnbindedAnswers();
    }

    public static void createUniqueCategory(String category) {
        if (checkCategory(category)) {
            NotificationController
                    .createErrorMessage(MessageId.TITLE_CREATE_CATEGORY, MessageId.MESSAGE_CATEGORY_ALREADY_EXISTS);
        } else {
            createCategory(category);
        }
    }

    public static void createCategory(String category) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_CATEGORY);
            psSql.setString(1, category);
            psSql.execute();

            NotificationController
                    .createMessage(MessageId.TITLE_CREATE_CATEGORY, MessageId.MESSAGE_CATEGORY_CREATED_SUCCESSFULLY, category);
        } catch (SQLException e) {
            e.printStackTrace();
            NotificationController
                    .createErrorMessage(MessageId.TITLE_CREATE_CATEGORY, MessageId.MESSAGE_UNDEFINED_ERROR);
        }
    }

    public static boolean checkCategory(String category) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_CATEGORY_BY_NAME);
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

    public static Category getCategory(String name) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_CATEGORY_BY_NAME);
            psSql.setString(1, name);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return new Category(
                        myRS.getInt(SQL_COLUMN_CATEGORY_ID),
                        myRS.getString(SQL_COLUMN_CATEGORY_NAME)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Category provideCategory(String name) {
        Category category = getCategory(name);

        if (category == null) {
            createUniqueCategory(name);
            category = getCategory(name);
        }

        return category;
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
                return myRS.getInt(SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setCategoryOnMultipleChoice(int categoryId, int multipleChoiceId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_CATEGORY_ON_MULTIPLE_CHOICE);
            psSql.setInt(1, categoryId);
            psSql.setInt(2, multipleChoiceId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setHeadlineOnMultipleChoice(int headline, int multipleChoiceId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_HEADLINE_ON_MULTIPLE_CHOICE);
            psSql.setInt(1, headline);
            psSql.setInt(2, multipleChoiceId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setCategoryOnShortAnswer(int categoryId, int shortAnswerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_CATEGORY_ON_SHORT_ANSWER);
            psSql.setInt(1, categoryId);
            psSql.setInt(2, shortAnswerId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setHeadlineOnShortAnswer(int headline, int shortAnswerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_HEADLINE_ON_SHORT_ANSWER);
            psSql.setInt(1, headline);
            psSql.setInt(2, shortAnswerId);
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

    public static void deleteUnbindedAnswers() {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            Statement mySQL = myCon.createStatement();
            mySQL.execute(SQL_DELETE_UNBINDED_ANSWERS);
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
                return myRS.getInt(SQL_COLUMN_ANSWER_OPTION_ID);
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

    public static boolean doQuestionExistsInQuestionnaire(String question, int questionnaireId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            Integer questionId = QuestionService.getMultipleChoiceId(question);
            if (questionId != null) {
                return QuestionService.getMultipleChoiceQuestionnaireRelationId(questionnaireId, questionId) != null;
            }
        } else {
            Integer questionId = QuestionService.getShortAnswerId(question);
            if (questionId != null) {
                return QuestionService.getShortAnswerQuestionnaireRelationId(questionnaireId, questionId) != null;
            }
        }

        return false;
    }
}
