package login;

import application.Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;

import static application.GlobalFuncs.getURL;

public class LoginService extends Datenbank {

	/**
	 * Testet, ob ein Verbindung zum Datenbankserver möglich ist.
	 * 
	 * @param user
	 *            String: Username
	 * @param pwd
	 *            String: Passwort
	 * @return boolean
	 * @author Alle
	 */
	public static boolean testDB(String user, String pwd) {
		System.out.println("search db...");
		String path = String.format("%s%s.mv.db", DB_PATH, DB_NAME);

        String absPath = getURL(path).getPath().replace(".mv.db", "");

		Connection connection;
		try {
			String url = String.format("jdbc:h2:%s;MODE=MySQL", absPath);
			connection = DriverManager.getConnection(url, user,
					pwd);

			connection.close();
			Datenbank.user = user;
			Datenbank.pwd = pwd;
			Datenbank.url = url;
			System.out.println("db success");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
