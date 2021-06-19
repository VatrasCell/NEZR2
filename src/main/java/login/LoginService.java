package login;

import application.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class LoginService extends Database {

    public static boolean login(String user, String pwd) {

        String url = createDatabaseURL(DB_NAME);

        try (Connection ignored = DriverManager.getConnection(url, user, pwd)) {
            Database.user = user;
            Database.pwd = pwd;
            Database.url = url;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
