package application;

import java.io.File;
import java.io.IOException;

public class Database {

    protected final static String DB_NAME = "nezr_v6"; // Datenbankname
    protected final static String DB_PATH = "db/"; // Datenbank Datei Pfad
    protected static String url = ""; // URL der Datenbank useSSL=false
    protected static String user = ""; // Login-ID
    protected static String pwd = ""; // Passwort

    /**
     * ka
     *
     * @param art
     * @param name
     * @return
     * @throws IOException
     */
    protected static String exp_cmd(int art, String name) throws IOException {
        String ret = "false";

        try {
            File theDir = new File("exportSQL");

            if (!theDir.exists()) {
                try {
                    theDir.mkdir();
                } catch (SecurityException se) {
                }
            }

            String statement = "";
            switch (art) {
                case 0:
                    statement = "cmd /c .\\mysqldump -u " + user + " -p" + pwd + " --no-data " + DB_NAME + " > exportSQL\\"
                            + name;
                    break;
                case 1:
                    statement = "cmd /c .\\mysqldump -u " + user + " -p" + pwd + " --no-create-info " + DB_NAME
                            + " > exportSQL\\" + name;
                    break;
                case 2:
                    statement = "cmd /c .\\mysqldump -u " + user + " -p" + pwd + "" + DB_NAME
                            + " > exportSQL\\" + name;
                    break;
            }

            Runtime.getRuntime().exec(statement);

            ret = "true";
        } catch (Exception e) {
            e.printStackTrace();
            //ErrorLog.fehlerBerichtB("ERROR",
            //		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
        }
        return ret;
    }
}
