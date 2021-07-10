package question;

import application.Database;
import application.NotificationController;
import message.MessageId;
import model.Category;
import model.QuestionType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static application.SqlStatement.SQL_COLUMN_CATEGORY_ID;
import static application.SqlStatement.SQL_COLUMN_CATEGORY_NAME;
import static application.SqlStatement.SQL_CREATE_CATEGORY;
import static application.SqlStatement.SQL_GET_CATEGORIES;
import static application.SqlStatement.SQL_GET_CATEGORY_BY_NAME;
import static application.SqlStatement.SQL_SET_CATEGORY_ON_MULTIPLE_CHOICE;
import static application.SqlStatement.SQL_SET_CATEGORY_ON_SHORT_ANSWER;

public class CategoryService extends Database {
    public static List<Category> getCategories() {
        try (Connection myCon = DriverManager.getConnection(Database.url, Database.user, Database.pwd)) {
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

    public static void createUniqueCategory(String category) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            if (checkCategory(category)) {
                NotificationController
                        .createErrorMessage(MessageId.TITLE_CREATE_CATEGORY, MessageId.MESSAGE_CATEGORY_ALREADY_EXISTS);
            } else {
                createCategory(myCon, category);
            }
            myCon.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createUniqueCategory(Connection connection, String category) throws SQLException {
        if (checkCategory(category)) {
            NotificationController
                    .createErrorMessage(MessageId.TITLE_CREATE_CATEGORY, MessageId.MESSAGE_CATEGORY_ALREADY_EXISTS);
        } else {
            createCategory(connection, category);
        }
    }

    private static void createCategory(Connection connection, String category) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_CATEGORY);
            psSql.setString(1, category);
            psSql.execute();

            NotificationController
                    .createMessage(MessageId.TITLE_CREATE_CATEGORY, MessageId.MESSAGE_CATEGORY_CREATED_SUCCESSFULLY, category);
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            NotificationController
                    .createErrorMessage(MessageId.TITLE_CREATE_CATEGORY, MessageId.MESSAGE_UNDEFINED_ERROR);
        }
    }

    public static boolean checkCategory(String category) {
        try (Connection myCon = DriverManager.getConnection(Database.url, Database.user, Database.pwd)) {
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

    public static Category getCategory(String name) {
        try (Connection myCon = DriverManager.getConnection(Database.url, Database.user, Database.pwd)) {
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

    public static Category provideCategory(Connection connection, String name) throws SQLException {
        Category category = getCategory(name);

        if (category == null) {
            createUniqueCategory(connection, name);
            category = getCategory(name);
        }

        return category;
    }

    public static void setCategoryOnQuestion(Connection connection, int categoryId, int questionId, QuestionType questionType) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(questionType.equals(QuestionType.MULTIPLE_CHOICE) ?
                    SQL_SET_CATEGORY_ON_MULTIPLE_CHOICE : SQL_SET_CATEGORY_ON_SHORT_ANSWER);
            psSql.setInt(1, categoryId);
            psSql.setInt(2, questionId);
            psSql.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }
}
