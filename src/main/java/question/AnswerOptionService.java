package question;

import application.Database;
import model.AnswerOption;

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
import static application.SqlStatement.SQL_COLUMN_NAME;
import static application.SqlStatement.SQL_CREATE_ANSWER_OPTION;
import static application.SqlStatement.SQL_CREATE_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION;
import static application.SqlStatement.SQL_DELETE_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_BY_ID;
import static application.SqlStatement.SQL_DELETE_UNBINDED_ANSWER_OPTIONS;
import static application.SqlStatement.SQL_GET_ANSWER_OPTION;
import static application.SqlStatement.SQL_GET_ANSWER_OPTIONS;
import static application.SqlStatement.SQL_GET_ANSWER_OPTION_ID;

public class AnswerOptionService extends Database {
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

    public static int provideAnswerOptionId(String answer) {
        Integer id;

        id = getAnswerOptionId(Objects.requireNonNull(answer));

        if (id == null) {
            createAnswerOption(answer);
            id = getAnswerOptionId(answer);
        }

        return Objects.requireNonNull(id);
    }

    public static void deleteAnswerOptions(ArrayList<Integer> answerIds, int multipleChoiceId) {

        for (Integer answerId : answerIds) {
            deleteMultipleChoiceAnswerOptionsRelation(answerId, multipleChoiceId);
        }

        deleteUnbindedAnswerOptions();
    }

    public static void deleteUnbindedAnswerOptions() {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            Statement mySQL = myCon.createStatement();
            mySQL.execute(SQL_DELETE_UNBINDED_ANSWER_OPTIONS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getAnswerOptionId(String answer) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_ANSWER_OPTION_ID);
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

    public static void createAnswerOption(String answer) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_ANSWER_OPTION);
            psSql.setString(1, answer);
            psSql.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceAnswerOptionsRelation(int relationId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION_BY_ID);
            psSql.setInt(1, relationId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMultipleChoiceAnswerOptionsRelation(int answerId, int multipleChoiceId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION);
            psSql.setInt(1, answerId);
            psSql.setInt(2, multipleChoiceId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createMultipleChoiceAnswerOptionsRelation(int multipleChoiceId, int answerId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_MULTIPLE_CHOICE_ANSWER_OPTIONS_RELATION);
            psSql.setInt(1, multipleChoiceId);
            psSql.setInt(2, answerId);
            psSql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
