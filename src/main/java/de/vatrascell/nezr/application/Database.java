package de.vatrascell.nezr.application;

import java.io.File;

public class Database {

    protected final static String DB_NAME = "nezr_v8"; // Datenbankname
    protected final static String DB_TEST_NAME = "nezr_v8_test"; // Datenbankname
    protected final static String RESET_DB_SCRIPT = "datenbank_v8.sql"; // Datenbankname
    protected final static String LOCATIONS = "locations.sql";
    protected final static String CATEGORIES = "categories.sql";
    protected final static String QUESTIONNAIRE_1 = "questionnaire_1.sql";
    protected final static String QUESTION_MC_1 = "question_mc_1.sql";
    protected final static String QUESTION_MC_2 = "question_mc_2.sql";
    protected final static String QUESTION_SA_1 = "question_sa_1.sql";
    protected final static String DB_PATH = "db"; // Datenbank Datei Pfad
    protected final static String SCRIPT_PATH = "script";
    protected static String url = ""; // URL der Datenbank useSSL=false
    protected static String user = ""; // Login-ID
    protected static String pwd = ""; // Passwort

    protected static String createDatabaseURL(String dbName) {
        File theDir = new File(DB_PATH);

        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //
            }
        }

        String path = String.format("%s\\%s.mv.db", theDir.getAbsolutePath(), dbName);

        String absPath = path.replace(".mv.db", "");
        return String.format("jdbc:h2:%s;MODE=MySQL", absPath);
    }

}
