package util;

import application.Database;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static application.GlobalFuncs.getURL;

public class DBTestUtil extends Database {

    @Before
    public void setUp() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        String path = String.format("%s%s.mv.db", DB_PATH, DB_TEST_NAME);

        String absPath = getURL(path).getPath().replace(".mv.db", "");
        Database.url = String.format("jdbc:h2:%s;MODE=MySQL", absPath);
        Database.user = "root";
        Database.pwd = "1234";

        restDatabase();
        addLocations();
    }

    protected void restDatabase() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, RESET_DB_SCRIPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addLocations() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, LOCATIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addCategories() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, CATEGORIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addQuestionnaire() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, QUESTIONNAIRE_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addMultipleChoiceQuestion() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, QUESTIONNAIRE_1);
            addCategories();
            runSqlScript(connection, QUESTION_MC_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addMultipleChoiceQuestionRequired() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, QUESTIONNAIRE_1);
            addCategories();
            runSqlScript(connection, QUESTION_MC_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addShortAnswerQuestion() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, QUESTIONNAIRE_1);
            addCategories();
            runSqlScript(connection, QUESTION_SA_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runSqlScript(Connection connection, String name) throws IOException, SQLException {
        ScriptRunner runner = new ScriptRunner(connection, false, false);
        String path = String.format("%s%s%s", DB_PATH, SCRIPT_PATH, name);
        String absPath = getURL(path).getPath();
        runner.runScript(new BufferedReader(new FileReader(absPath)));
    }
}
