package start;

import application.Database;
import model.Questionnaire;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static application.SqlStatement.SQL_COLUMN_CREATION_DATE;
import static application.SqlStatement.SQL_COLUMN_LOCATION_NAME;
import static application.SqlStatement.SQL_COLUMN_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_COLUMN_QUESTIONNAIRE_NAME;
import static application.SqlStatement.SQL_GET_ACTIVE_QUESTIONNAIRE;

public class StartService extends Database {

    public static Questionnaire getActiveQuestionnaire() {

        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_ACTIVE_QUESTIONNAIRE);
            ResultSet myRS = psSql.executeQuery();

            if (myRS.next()) {
                Questionnaire questionnaire = new Questionnaire();
                questionnaire.setName(myRS.getString(SQL_COLUMN_QUESTIONNAIRE_NAME));
                questionnaire.setDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                questionnaire.setId(myRS.getInt(SQL_COLUMN_QUESTIONNAIRE_ID));
                questionnaire.setOrt(myRS.getString(SQL_COLUMN_LOCATION_NAME));
                questionnaire.setActive(true);
                return questionnaire;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
