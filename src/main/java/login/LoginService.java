package login;

import application.Database;

import java.sql.Connection;
import java.sql.DriverManager;

import static application.GlobalFuncs.getURL;

public class LoginService extends Database {

    public static boolean login(String user, String pwd) {
        String path = String.format("%s%s.mv.db", DB_PATH, DB_NAME);

        String absPath = getURL(path).getPath().replace(".mv.db", "");
        String url = String.format("jdbc:h2:%s;MODE=MySQL", absPath);
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
