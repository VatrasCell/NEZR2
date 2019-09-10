package login;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
		File f = new File("./h2/bin/data/nezr_v5.mv.db");
		if (!f.exists() && f.isDirectory()) {
			System.out.println("DB file is missing");
			return false;
		}

		Connection connection;
		try {
			String url = String.format("jdbc:h2:./h2/bin/data/%s;MODE=MySQL", db);
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
