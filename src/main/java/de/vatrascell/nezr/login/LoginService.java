package de.vatrascell.nezr.login;

import de.vatrascell.nezr.application.Database;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;

@Service
public class LoginService extends Database {

    public boolean login(String user, String pwd) {

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
