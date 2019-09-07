package login;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import application.Datenbank;

public class LoginService extends Datenbank {

	/**
	 * Testet, ob ein Verbinfung zum Datenbankserver möglich ist.
	 * 
	 * @param user
	 *            String: Username
	 * @param pwd
	 *            String: Passwort
	 * @param ip
	 *            String: IP des Servers
	 * @param port
	 *            int: Port
	 * @return boolean
	 * @author Alle
	 */
	public static boolean testDB(String user, String pwd, String ip, int port, boolean doSync) {
		boolean loginMySQL = false;
		boolean loginh2 = false;
		System.out.println(doSync);
		// TODO: Sync
		doSync = false;

		boolean doH2 = false;

		File f = new File("./nezr2/h2/bin/data/nezr_v5.mv.db");
		if (f.exists() && !f.isDirectory()) {
			doH2 = true;
		}
		do {
			if (doH2) {
				System.out.println("loginH2");
				loginh2 = loginH2(user, pwd);
				if (loginh2) {
					if (!doSync) {
						break;
					}
					if (loginMySQL) {
						break;
					} else {
						doH2 = false;
						continue;
					}
				} else {
					if (loginMySQL) {
						break;
					} else {
						doH2 = false;
						continue;
					}
				}
			} else {
				System.out.println("loginMySQL");
				loginMySQL = loginMySQL(user, pwd, ip, port);
				if (loginMySQL) {
					if (!doSync) {
						break;
					}
					if (loginh2) {
						break;
					} else {
						doH2 = true;
						continue;
					}
				} else {
					break;
				}
			}
		} while (true);

		Datenbank.user = user;
		Datenbank.pwd = pwd;
		System.out.println(loginMySQL + " " + loginh2);
		if (loginh2) {
			url = "jdbc:h2:./h2/bin/data/" + db + ";MODE=MySQL";
		} else if (loginMySQL) {
			url = "jdbc:mysql://" + ip + ":" + port + "/" + db + "?useSSL=false";
		}

		if (loginh2 && loginMySQL) {
			//syncDatabases("H2");
		}

		return (loginMySQL || loginh2);
	}

	/*
	public static boolean syncDatabases(String goal) {
		System.out.println("do sync...");
		if (goal.equals("H2")) {
			try {
				exp_cmd(2, "mysql");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Connection connection;
				connection = DriverManager.getConnection(url, user, pwd);
				RunScript.execute(connection, new FileReader(".\\exportSQL\\mysql.sql"));

				return true;
			} catch (SQLException | FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}*/

	private static boolean loginH2(String user, String pwd) {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:h2:./nezr2/h2/bin/data/" + db + ";MODE=MySQL", user,
					pwd);

			connection.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean loginMySQL(String user, String pwd, String ip, int port) {
		Connection connection;
		try {
			connection = DriverManager
					.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db + "?useSSL=false", user, pwd);
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
