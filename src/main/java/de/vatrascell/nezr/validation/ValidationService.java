package de.vatrascell.nezr.validation;

import de.vatrascell.nezr.application.Database;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_HAS_LENGTH;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_IS_ALL_CHARS;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_IS_ALPHANUMERIC;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_IS_LETTERS;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_IS_NUMBERS;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_IS_REGEX;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_LENGTH;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_MAX_LENGTH;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_MIN_LENGTH;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_REGEX;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_VALIDATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_VALIDATION;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_VALIDATION_BY_SA_REL_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_LAST_VALIDATION_ID;

@Service
public class ValidationService extends Database {
    public Validation getValidation(int questionRelationId) {

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

    public void createValidation(Connection connection, Validation validation) throws SQLException {
        try {
            PreparedStatement psSql = connection.prepareStatement(SQL_CREATE_VALIDATION);
            psSql.setBoolean(1, validation.isNumbers());
            psSql.setBoolean(2, validation.isLetters());
            psSql.setBoolean(3, validation.isAlphanumeric());
            psSql.setBoolean(4, validation.isAllChars());
            psSql.setBoolean(5, validation.isRegex());
            psSql.setBoolean(6, validation.isHasLength());
            psSql.setString(7, validation.getRegex());
            psSql.setInt(8, validation.getMinLength());
            psSql.setInt(9, validation.getMaxLength());
            psSql.setInt(10, validation.getLength());
            psSql.execute();

        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    public Integer getLastValidationId() {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_LAST_VALIDATION_ID);

            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                return myRS.getInt(SQL_COLUMN_VALIDATION_ID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
