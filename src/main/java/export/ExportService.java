package export;

import application.Database;
import export.model.ExcelCell;
import model.AnswerOption;
import model.Question;
import model.QuestionType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static application.SqlStatement.SQL_COLUMN_ANSWER;
import static application.SqlStatement.SQL_COLUMN_NAME;
import static application.SqlStatement.SQL_COLUMN_SURVEY_ID;
import static application.SqlStatement.SQL_COLUMN_SURVEY_ID_COUNT;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_SURVEY_ID_AND_ANSWER;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_SURVEY_ID_BY_ANSWER;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_SURVEY_ID_AND_ANSWER;
import static application.SqlStatement.SQL_GET_SURVEY_COUNT;

public class ExportService extends Database {

    public static int getSurveyCount() {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SURVEY_COUNT);
            ResultSet myRS = psSql.executeQuery();
            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_SURVEY_ID_COUNT);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static ArrayList<ExcelCell> getAnswerPositions(Question question, String fromDate, String toDate) {
        ArrayList<ExcelCell> excelCells = new ArrayList<>();
        if (isFlaggedMultipleChoiceQuestion(question)) {
            excelCells.addAll(getMultipleChoiceAnswerCells(question.getQuestionnaireId(), question.getQuestionId(), fromDate, toDate));
        } else if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
            excelCells.addAll(getShortAnswerAnswerCells(question.getQuestionnaireId(), question.getQuestionId(), fromDate, toDate));
        }
        return excelCells;
    }

    public static ArrayList<ExcelCell> getAnswerPositions(Question question, AnswerOption answerOption, String fromDate, String toDate) {
        ArrayList<ExcelCell> excelCells = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_SURVEY_ID_BY_ANSWER);
            psSql.setInt(1, question.getQuestionnaireId());
            psSql.setInt(2, question.getQuestionId());
            psSql.setInt(3, answerOption.getId());
            psSql.setString(4, fromDate);
            psSql.setString(5, toDate);
            ResultSet myRS = psSql.executeQuery();

            int oldId = -1;
            ArrayList<String> answers = new ArrayList<>();
            while (myRS.next()) {
                int newId = myRS.getInt(SQL_COLUMN_SURVEY_ID);
                if (oldId != newId) {
                    answers = new ArrayList<>();
                    answers.add("1");
                    excelCells.add(new ExcelCell(newId, answers));
                    oldId = newId;
                } else {
                    answers.add("1");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return excelCells;
    }

    private static boolean isFlaggedMultipleChoiceQuestion(Question question) {
        return ((question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) && (question.getFlags().isEvaluationQuestion()))
                || (question.getFlags().isList())
                || (question.getFlags().isYesNoQuestion());
    }

    private static List<ExcelCell> getMultipleChoiceAnswerCells(int questionnaireId, int questionId, String fromDate, String toDate) {
        List<ExcelCell> excelCells = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_SURVEY_ID_AND_ANSWER);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, questionId);
            psSql.setString(3, fromDate);
            psSql.setString(4, toDate);
            ResultSet myRS = psSql.executeQuery();

            int oldId = -1;
            ArrayList<String> answers = new ArrayList<>();
            while (myRS.next()) {
                int newId = myRS.getInt(SQL_COLUMN_SURVEY_ID);
                answers.add(myRS.getString(SQL_COLUMN_NAME));
                if (oldId != newId) {
                    answers = new ArrayList<>();
                    excelCells.add(new ExcelCell(newId, answers));
                    oldId = newId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return excelCells;
    }

    private static List<ExcelCell> getShortAnswerAnswerCells(int questionnaireId, int questionId, String fromDate, String toDate) {
        List<ExcelCell> excelCells = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_SHORT_ANSWER_SURVEY_ID_AND_ANSWER);
            psSql.setInt(1, questionnaireId);
            psSql.setInt(2, questionId);
            psSql.setString(3, fromDate);
            psSql.setString(4, toDate);
            ResultSet myRS = psSql.executeQuery();

            int oldId = -1;
            ArrayList<String> answers = new ArrayList<>();
            while (myRS.next()) {
                int newId = myRS.getInt(SQL_COLUMN_SURVEY_ID);
                answers.add(myRS.getString(SQL_COLUMN_ANSWER));
                if (oldId != newId) {
                    answers = new ArrayList<>();
                    excelCells.add(new ExcelCell(newId, answers));
                    oldId = newId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return excelCells;
    }
}
