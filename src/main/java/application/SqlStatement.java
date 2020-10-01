package application;

public class SqlStatement {

    public static final String SQL_GET_LOCATION_ID = "SELECT idOrt FROM Ort WHERE ort=?";

    public static final String SQL_CREATE_QUESTIONNAIRE = "INSERT INTO fragebogen VALUES(NULL, ?, ?, FALSE, ?, FALSE)";
    public static final String SQL_GET_LAST_QUESTIONNAIRE_ID = "SELECT MAX(idFragebogen) FROM fragebogen";
    public static final String SQL_IS_QUESTIONNAIRE_FINAL = "SELECT idFragebogen FROM fragebogen WHERE final=TRUE AND idFragebogen=?";
    public static final String SQL_SET_QUESTIONNAIRE_FINAL_STATUS = "UPDATE fragebogen SET final=? WHERE idFragebogen=?";
    public static final String SQL_RENAME_QUESTIONNAIRE = "UPDATE fragebogen SET Name=? WHERE idFragebogen=?";

    public static final String SQL_GET_CATEGORY = "SELECT Kategorie FROM kategorie WHERE Kategorie=?";
    public static final String SQL_CREATE_CATEGORY = "INSERT INTO kategorie VALUES(NULL, ?)";
    public static final String SQL_GET_CATEGORY_ID = "SELECT idKategorie FROM kategorie WHERE Kategorie=?";
    public static final String SQL_GET_CATEGORIES = "SELECT * FROM kategorie";

    public static final String SQL_GET_MULTIPLE_CHOICE_ID = "SELECT multipleChoiceId FROM MultipleChoice WHERE FrageMC=?";
    public static final String SQL_CREATE_MULTIPLE_CHOICE = "INSERT INTO MultipleChoice VALUES(NULL, ?, ?)";

    public static final String SQL_GET_SHORT_ANSWER_ID = "SELECT idFreieFragen FROM FreieFragen WHERE FrageFF=?";
    public static final String SQL_CREATE_SHORT_ANSWER = "INSERT INTO FreieFragen VALUES(NULL, ? , ?)";

    public static final String SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_IDS = "SELECT idRelMCA FROM MC_has_A WHERE idMultipleChoice=?";
    public static final String SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_ID = "SELECT idRelMCA FROM MC_has_A WHERE idMultipleChoice=? AND AntwortNr=?";
    public static final String SQL_CREATE_MULTIPLE_CHOICE_ANSWERS_RELATION = "INSERT INTO MC_has_A VALUES(NULL, ?, ?)";
    public static final String SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_ID = "DELETE FROM MC_has_A WHERE idRelMCA =?";
    public static final String SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION = "DELETE FROM MC_has_A WHERE AntwortNr=? AND idMultipleChoice=?";

    public static final String SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID = "SELECT idRelFBMC FROM FB_HAS_MC WHERE idFragebogen= ? AND idMultipleChoice=?";
    public static final String SQL_SET_FLAGS_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "UPDATE FB_HAS_MC SET Flags=? WHERE idRelFBMC=?";
    public static final String SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "UPDATE FB_HAS_MC SET Position=? WHERE idRelFBMC=?";
    public static final String SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "INSERT INTO Fb_has_MC VALUES (NULL, ?, ?, ?, ?)";

    public static final String SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID = "SELECT idRelFBFF FROM FB_HAS_FF WHERE idFragebogen=? AND idFreieFragen=?";
    public static final String SQL_SET_FLAGS_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "UPDATE FB_HAS_FF SET Flags=? WHERE idRelFBFF=?";
    public static final String SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "UPDATE FB_HAS_FF SET Position=? WHERE idRelFBFF=?";
    public static final String SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "INSERT INTO Fb_has_FF VALUES (NULL, ?, ?, ?, ?)";

    public static final String SQL_DELETE_ANSWERS = "DELETE FROM Antworten WHERE AntwortNr NOT IN (SELECT AntwortNr FROM MC_has_a)" +
            " AND NOT(antwort='ja') AND NOT(antwort='nein') " +
            "AND NOT(antwort='#####') AND NOT(antwort='0') AND NOT(antwort='1') AND NOT(antwort='2') AND NOT(antwort='3') AND NOT(antwort='4') " +
            "AND NOT(antwort='5') AND NOT(antwort='6') AND NOT(antwort='7') AND NOT(antwort='8') AND NOT(antwort='9') AND NOT(antwort='10')";
    public static final String SQL_GET_ANSWER_ID = "SELECT AntwortNr FROM Antworten WHERE Antwort=?";
    public static final String SQL_CREATE_ANSWER = "INSERT INTO Antworten VALUES(NULL, ?)";
    public static final String SQL_GET_ANSWERS = "SELECT Antwort FROM fragebogen JOIN "
            + "fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen JOIN "
            + "multiplechoice mc1 ON fb_has_mc.idMultipleChoice=mc1.idMultipleChoice JOIN "
            + "kategorie ON mc1.idKategorie=kategorie.idKategorie JOIN "
            + "mc_has_a ON mc1.idMultipleChoice=mc_has_a.idMultipleChoice JOIN "
            + "antworten ON mc_has_a.AntwortNr=antworten.AntwortNr WHERE mc1.FrageMC=? "
            + "UNION SELECT Antwort FROM fragebogen JOIN "
            + "fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen JOIN "
            + "freiefragen ff1 ON fb_has_ff.idFreieFragen=ff1.idFreieFragen JOIN "
            + "kategorie ON ff1.idKategorie=kategorie.idKategorie JOIN freiefragen "
            + "JOIN ff_has_a ON ff1.idFreieFragen=ff_has_a.idFreieFragen JOIN "
            + "antworten ON ff_has_a.AntwortNr=antworten.AntwortNr WHERE ff1.FrageFF=?";

    public static final String SQL_GET_SHORT_ANSWERS_FLAGS = "SELECT Flags FROM Fb_has_FF WHERE idFragebogen=? AND idFreieFragen=?";
    public static final String SQL_GET_MULTIPLE_CHOICE_FLAGS = "SELECT Flags FROM Fb_has_MC WHERE idMultipleChoice=? AND idMultipleChoice=?";
    public static final String SQL_UPDATE_SHORT_ANSWERS_FLAGS = "UPDATE Fb_has_FF SET Flags=?  WHERE idFragebogen=? AND idFreieFragen=?";
    public static final String SQL_UPDATE_MULTIPLE_CHOICE_FLAGS = "UPDATE Fb_has_MC SET Flags=?  WHERE idFragebogen=? AND idMultipleChoice=?";

    public static final String SQL_GET_MAX_MULTIPLE_CHOICE_POSITION = "SELECT MAX(fb_has_mc.Position) AS position FROM fragebogen JOIN fb_has_mc ON fragebogen.idFragebogen=fb_has_mc.idFragebogen WHERE fragebogen.idFragebogen=?";
    public static final String SQL_GET_MAX_SHORT_ANSWER_POSITION = "SELECT MAX(fb_has_ff.Position) AS position FROM fragebogen JOIN fb_has_ff ON fragebogen.idFragebogen=fb_has_ff.idFragebogen WHERE fragebogen.idFragebogen=?";

    public static final String SQL_COLUMN_LABEL_MAX_QUESTIONNAIRE_ID = "MAX(idFragebogen)";
}
