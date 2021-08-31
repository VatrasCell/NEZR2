package de.vatrascell.nezr.question;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.application.NotificationController;
import de.vatrascell.nezr.message.MessageId;
import de.vatrascell.nezr.model.Headline;
import de.vatrascell.nezr.model.QuestionType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_HEADLINE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_NAME;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_HEADLINE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_HEADLINES;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_HEADLINE_BY_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_HEADLINE_BY_NAME;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_HEADLINE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_HEADLINE_ON_MULTIPLE_CHOICE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_HEADLINE_ON_SHORT_ANSWER;

public class HeadlineService extends Database {
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

    public static void createHeadline(Connection connection, String name) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_HEADLINE);
            psSql.setString(1, name);
            psSql.execute();

            NotificationController
                    .createMessage(MessageId.TITLE_CREATE_HEADLINE, MessageId.MESSAGE_CREATE_HEADLINE, name);
        } catch (SQLException e) {
            connection.rollback();
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
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            if (checkHeadline(name)) {
                NotificationController
                        .createErrorMessage(MessageId.TITLE_CREATE_HEADLINE, MessageId.MESSAGE_CATEGORY_HEADLINE_EXISTS);
            } else {
                createHeadline(myCon, name);
            }

            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static void createUniqueHeadline(Connection connection, String name) throws SQLException {
        if (checkHeadline(name)) {
            NotificationController
                    .createErrorMessage(MessageId.TITLE_CREATE_HEADLINE, MessageId.MESSAGE_CATEGORY_HEADLINE_EXISTS);
        } else {
            createHeadline(connection, name);
        }
    }

    public static Headline provideHeadline(Connection connection, String name) throws SQLException {
        Headline headline = getHeadlineByName(name);

        if (headline == null) {
            createUniqueHeadline(connection, name);
            headline = getHeadlineByName(name);
        }

        return headline;
    }

    public static void setHeadlineOnQuestion(Connection connection, int headline, int multipleChoiceId, QuestionType questionType) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_SET_HEADLINE_ON_MULTIPLE_CHOICE : SQL_SET_HEADLINE_ON_SHORT_ANSWER);
            psSql.setInt(1, headline);
            psSql.setInt(2, multipleChoiceId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }
}
