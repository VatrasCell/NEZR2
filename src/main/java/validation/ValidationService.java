package validation;

import application.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static application.SqlStatement.SQL_COLUMN_HAS_LENGTH;
import static application.SqlStatement.SQL_COLUMN_IS_ALL_CHARS;
import static application.SqlStatement.SQL_COLUMN_IS_ALPHANUMERIC;
import static application.SqlStatement.SQL_COLUMN_IS_LETTERS;
import static application.SqlStatement.SQL_COLUMN_IS_NUMBERS;
import static application.SqlStatement.SQL_COLUMN_IS_REGEX;
import static application.SqlStatement.SQL_COLUMN_LENGTH;
import static application.SqlStatement.SQL_COLUMN_MAX_LENGTH;
import static application.SqlStatement.SQL_COLUMN_MIN_LENGTH;
import static application.SqlStatement.SQL_COLUMN_REGEX;
import static application.SqlStatement.SQL_COLUMN_VALIDATION_ID;
import static application.SqlStatement.SQL_GET_VALIDATION_BY_SA_REL_ID;

public class ValidationService extends Database {


    public static Validation getValidation(int questionRelationId) {

        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {

            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_VALIDATION_BY_SA_REL_ID);
            psSql.setInt(1, questionRelationId);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                Validation validation = new Validation();
                validation.setId(myRS.getInt(SQL_COLUMN_VALIDATION_ID));
                validation.setNumbers(myRS.getBoolean(SQL_COLUMN_IS_NUMBERS));
                validation.setLetters(myRS.getBoolean(SQL_COLUMN_IS_LETTERS));
                validation.setAlphanumeric(myRS.getBoolean(SQL_COLUMN_IS_ALPHANUMERIC));
                validation.setAllChars(myRS.getBoolean(SQL_COLUMN_IS_ALL_CHARS));
                validation.setRegex(myRS.getBoolean(SQL_COLUMN_IS_REGEX));
                validation.setHasLength(myRS.getBoolean(SQL_COLUMN_HAS_LENGTH));
                validation.setRegex(myRS.getString(SQL_COLUMN_REGEX));
                validation.setMinLength(myRS.getInt(SQL_COLUMN_MIN_LENGTH));
                validation.setMaxLength(myRS.getInt(SQL_COLUMN_MAX_LENGTH));
                validation.setLength(myRS.getInt(SQL_COLUMN_LENGTH));
                return validation;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
