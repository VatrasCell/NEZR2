package login;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import application.Datenbank;

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
		File f = new File(path);
		if (!f.exists() && f.isDirectory()) {
			System.out.println("DB file is missing");
			return false;
		}

		Connection connection;
		try {
			String url = String.format("jdbc:h2:%s%s;MODE=MySQL", DB_PATH, DB_NAME);
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
