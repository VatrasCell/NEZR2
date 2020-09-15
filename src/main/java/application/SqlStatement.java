package application;

public class SqlStatement {

    public static final String SQL_GET_LOCATION_ID = "SELECT idOrt FROM Ort WHERE ort=?";
    public static final String SQL_CREATE_QUESTIONNAIRE = "INSERT INTO fragebogen VALUES(NULL, ?, ?, FALSE, ?, FALSE)";
    public static final String SQL_GET_LAST_QUESTIONNAIRE_ID = "SELECT MAX(idFragebogen) FROM fragebogen";
    public static final String SQL_IS_QUESTIONNAIRE_FINAL = "SELECT idFragebogen FROM fragebogen WHERE final=TRUE AND idFragebogen=?";
    public static final String SQL_SET_QUESTIONNAIRE_FINAL_STATUS = "UPDATE fragebogen SET final=? WHERE idFragebogen=?";
    public static final String SQL_RENAME_QUESTIONNAIRE = "UPDATE fragebogen SET Name=? WHERE idFragebogen=?";

    public static final String SQL_COLUMN_LABEL_MAX_QUESTIONNAIRE_ID = "MAX(idFragebogen)";
}
