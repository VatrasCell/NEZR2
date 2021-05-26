package react;

import application.Database;
import flag.React;
import model.QuestionType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static application.SqlStatement.SQL_COLUMN_ANSWER_POSITION;
import static application.SqlStatement.SQL_COLUMN_MULTIPLE_CHOICE_ID;
import static application.SqlStatement.SQL_COLUMN_REACT_ID;
import static application.SqlStatement.SQL_COLUMN_SHORT_ANSWER_ID;
import static application.SqlStatement.SQL_GET_MULTIPLE_CHOICE_REACTS;
import static application.SqlStatement.SQL_GET_SHORT_ANSWER_REACTS;

public class ReactService extends Database {

    public static List<React> getReacts(int questionRelationId, QuestionType questionType) {
        List<React> reacts = new ArrayList<>();
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = questionType.equals(QuestionType.SHORT_ANSWER) ?
                    myCon.prepareStatement(SQL_GET_SHORT_ANSWER_REACTS) :
                    myCon.prepareStatement(SQL_GET_MULTIPLE_CHOICE_REACTS);
            psSql.setInt(1, questionRelationId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                React react = new React(
                        myRS.getInt(SQL_COLUMN_REACT_ID),
                        questionType,
                        myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID) != 0 ?
                                myRS.getInt(SQL_COLUMN_SHORT_ANSWER_ID) : myRS.getInt(SQL_COLUMN_MULTIPLE_CHOICE_ID),
                        myRS.getInt(SQL_COLUMN_ANSWER_POSITION));
                reacts.add(react);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reacts;
    }
}
