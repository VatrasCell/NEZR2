package flag;

import application.Database;
import model.QuestionType;
import question.QuestionService;
import react.ReactService;
import validation.ValidationService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static application.SqlStatement.SQL_COLUMN_FLAG_LIST_ID;
import static application.SqlStatement.SQL_COLUMN_IS_EVALUATION_QUESTION;
import static application.SqlStatement.SQL_COLUMN_IS_LIST;
import static application.SqlStatement.SQL_COLUMN_IS_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_COLUMN_IS_REQUIRED;
import static application.SqlStatement.SQL_COLUMN_IS_SINGLE_LINE;
import static application.SqlStatement.SQL_COLUMN_IS_TEXT_AREA;
import static application.SqlStatement.SQL_COLUMN_IS_YES_NO_QUESTION;
import static application.SqlStatement.SQL_GET_FLAG_LIST_ID_ON_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_GET_FLAG_LIST_ID_ON_SHORT_ANSWER;
import static application.SqlStatement.SQL_GET_FLAG_LIST_MC_BY_ID;
import static application.SqlStatement.SQL_GET_FLAG_LIST_SA_BY_ID;
import static application.SqlStatement.SQL_SET_FLAG_LIST_MC_REQUIRED;
import static application.SqlStatement.SQL_SET_FLAG_LIST_SA_REQUIRED;
import static application.SqlStatement.SQL_UPDATE_FLAG_LIST_MC;
import static application.SqlStatement.SQL_UPDATE_FLAG_LIST_SA;

public class FlagListService extends Database {

    public static FlagList getFlagList(int questionnaireId, int questionId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            return getFlagList(QuestionService.getMultipleChoiceQuestionnaireRelationId(questionnaireId, questionId), questionType);
        } else {
            return getFlagList(QuestionService.getShortAnswerQuestionnaireRelationId(questionnaireId, questionId), questionType);
        }
    }

    public static FlagList getFlagList(int questionRelationId, QuestionType questionType) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {

            String statement = questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_GET_FLAG_LIST_MC_BY_ID : SQL_GET_FLAG_LIST_SA_BY_ID;

            PreparedStatement psSql = myCon.prepareStatement(statement);
            psSql.setInt(1, questionRelationId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                FlagList flagList = new FlagList();
                flagList.setId(questionRelationId);
                flagList.setRequired(myRS.getBoolean(SQL_COLUMN_IS_REQUIRED));
                if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
                    flagList.setEvaluationQuestion(myRS.getBoolean(SQL_COLUMN_IS_EVALUATION_QUESTION));
                    flagList.setMultipleChoice(myRS.getBoolean(SQL_COLUMN_IS_MULTIPLE_CHOICE));
                    flagList.setList(myRS.getBoolean(SQL_COLUMN_IS_LIST));
                    flagList.setYesNoQuestion(myRS.getBoolean(SQL_COLUMN_IS_YES_NO_QUESTION));
                    flagList.setSingleLine(myRS.getBoolean(SQL_COLUMN_IS_SINGLE_LINE));
                } else {
                    flagList.setTextArea(myRS.getBoolean(SQL_COLUMN_IS_TEXT_AREA));
                }

                flagList.setValidation(ValidationService.getValidation(questionRelationId));
                flagList.setReacts(ReactService.getReacts(questionRelationId, questionType));
                return flagList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setQuestionRequired(int questionnaireId, int questionId, QuestionType questionType) {
        if (questionType.equals(QuestionType.MULTIPLE_CHOICE)) {
            setQuestionRequired(QuestionService.getMultipleChoiceQuestionnaireRelationId(questionnaireId, questionId), questionType);
        } else {
            setQuestionRequired(QuestionService.getShortAnswerQuestionnaireRelationId(questionnaireId, questionId), questionType);
        }
    }

    public static void setQuestionRequired(int flagListId, QuestionType questionType) {

        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_SET_FLAG_LIST_MC_REQUIRED : SQL_SET_FLAG_LIST_SA_REQUIRED);
            psSql.setInt(1, flagListId);
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getFlagListIdByQuestionIdAndQuestionnaireId(QuestionType questionType, int questionnaireId, int questionId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(questionType.equals(QuestionType.SHORT_ANSWER) ?
                    SQL_GET_FLAG_LIST_ID_ON_SHORT_ANSWER : SQL_GET_FLAG_LIST_ID_ON_MULTIPLE_CHOICE);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, questionId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_FLAG_LIST_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateMultipleChoiceFlagList(int relationId, FlagList flagList) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_UPDATE_FLAG_LIST_MC);
            psSql.setBoolean(1, flagList.isEvaluationQuestion());
            psSql.setBoolean(2, flagList.isRequired());
            psSql.setBoolean(3, flagList.isMultipleChoice());
            psSql.setBoolean(4, flagList.isList());
            psSql.setBoolean(5, flagList.isYesNoQuestion());
            psSql.setBoolean(6, flagList.isSingleLine());
            psSql.setInt(7, relationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateShortAnswerFlagList(int relationId, FlagList flagList) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_UPDATE_FLAG_LIST_SA);
            psSql.setBoolean(1, flagList.isRequired());
            psSql.setBoolean(2, flagList.isTextArea());
            psSql.setInt(3, relationId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
