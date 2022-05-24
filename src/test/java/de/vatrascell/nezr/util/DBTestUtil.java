package de.vatrascell.nezr.util;

import de.vatrascell.nezr.application.Database;
import org.junit.jupiter.api.BeforeAll;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;

public class DBTestUtil extends Database {

    @BeforeAll
    static void setUp() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");

        Database.url = createDatabaseURL(DB_TEST_NAME);
        Database.user = "tester";
        Database.pwd = "EZ4.6emdwsu)9!TA";

        restDatabase();
        addLocations();
    }

    protected static void restDatabase() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, RESET_DB_SCRIPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void addLocations() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, LOCATIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void addCategories() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, CATEGORIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void addQuestionnaire() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, QUESTIONNAIRE_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void addMultipleChoiceQuestion() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, QUESTIONNAIRE_1);
            addCategories();
            runSqlScript(connection, QUESTION_MC_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void addMultipleChoiceQuestionRequired() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, QUESTIONNAIRE_1);
            addCategories();
            runSqlScript(connection, QUESTION_MC_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void addShortAnswerQuestion() {
        try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
            runSqlScript(connection, QUESTIONNAIRE_1);
            addCategories();
            runSqlScript(connection, QUESTION_SA_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runSqlScript(Connection connection, String name) throws IOException, SQLException {
        ScriptRunner runner = new ScriptRunner(connection, false, false);
        String path = String.format("%s/%s/%s", DB_PATH, SCRIPT_PATH, name);
        String absPath = getURL(path).getPath();
        runner.runScript(new BufferedReader(new FileReader(absPath)));
    }
}
