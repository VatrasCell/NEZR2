package application;

public class SqlStatement {

    private static final String SQL_MAX = "MAX(%s)";

    //Table location
    public static final String SQL_GET_LOCATION_ID = "SELECT location_id FROM location WHERE name=?";
    public static final String SQL_GET_LOCATION_NAMES = "SELECT location.name FROM location";

    public static final String SQL_COLUMN_LOCATION_ID = "location_id";
    public static final String SQL_COLUMN_LOCATION_NAME = "location.name";

    //Table questionnaire
    public static final String SQL_ACTIVATE_QUESTIONNAIRE = "UPDATE questionnaire SET is_active=TRUE WHERE questionnaire_id=?";
    public static final String SQL_GET_ACTIVE_QUESTIONNAIRE = "SELECT questionnaire_id, creation_date, questionnaire.name, location.name FROM questionnaire JOIN location ON location.location_id = questionnaire.location_id WHERE is_active=TRUE";
    public static final String SQL_DEACTIVATE_OTHER_QUESTIONNAIRES = "UPDATE questionnaire SET is_active=FALSE WHERE NOT questionnaire_id=?";
    public static final String SQL_DEACTIVATE_QUESTIONNAIRE = "UPDATE questionnaire SET is_active=FALSE WHERE questionnaire_id=?";
    public static final String SQL_GET_QUESTIONNAIRES_BY_LOCATION_ID = "SELECT * FROM questionnaire WHERE location_id=?";
    public static final String SQL_CREATE_QUESTIONNAIRE = "INSERT INTO questionnaire VALUES(NULL, ?, ?, FALSE, ?, FALSE)";
    public static final String SQL_GET_LAST_QUESTIONNAIRE_ID = "SELECT MAX(questionnaire_id) FROM questionnaire";
    public static final String SQL_IS_QUESTIONNAIRE_FINAL = "SELECT questionnaire_id FROM questionnaire WHERE is_final=TRUE AND questionnaire_id=?";
    public static final String SQL_SET_QUESTIONNAIRE_FINAL_STATUS = "UPDATE questionnaire SET is_final=? WHERE questionnaire_id=?";
    public static final String SQL_RENAME_QUESTIONNAIRE = "UPDATE questionnaire SET name=? WHERE questionnaire_id=?";
    public static final String SQL_DELETE_QUESTIONNAIRE = "DELETE FROM questionnaire WHERE questionnaire_id=?";

    public static final String SQL_COLUMN_QUESTIONNAIRE_ID = "questionnaire_id";
    public static final String SQL_COLUMN_MAX_QUESTIONNAIRE_ID = String.format(SQL_MAX, SQL_COLUMN_QUESTIONNAIRE_ID);
    public static final String SQL_COLUMN_CREATION_DATE = "creation_date";
    public static final String SQL_COLUMN_QUESTIONNAIRE_NAME = "questionnaire.name";

    //Table category
    public static final String SQL_CREATE_CATEGORY = "INSERT INTO category VALUES(NULL, ?)";
    public static final String SQL_GET_CATEGORY_ID = "SELECT category_id FROM category WHERE name=?";
    public static final String SQL_GET_CATEGORIE_NAMES = "SELECT category.name FROM category";

    public static final String SQL_COLUMN_CATEGORY_ID = "category_id";
    public static final String SQL_COLUMN_CATEGORY_NAME = "category.name";

    //Table multiple_choice
    public static final String SQL_GET_MULTIPLE_CHOICE_ID = "SELECT multiple_choice_id FROM multiple_choice WHERE question=?";
    public static final String SQL_CREATE_MULTIPLE_CHOICE = "INSERT INTO multiple_choice VALUES(NULL, ?, ?)";
    public static final String SQL_DELETE_MULTIPLE_CHOICE = "DELETE FROM multiple_choice WHERE multiple_choice_id=?";

    public static final String SQL_COLUMN_QUESTION = "question";
    public static final String SQL_COLUMN_MULTIPLE_CHOICE_ID = "multiple_choice_id";

    //Table q_has_mc
    public static final String SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID = "SELECT q_mc_relation_id FROM q_has_mc WHERE questionnaire_id= ? AND multiple_choice_id=?";
    public static final String SQL_GET_OTHER_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_IDS = "SELECT q_mc_relation_id FROM q_has_mc WHERE NOT questionnaire_id= ? AND multiple_choice_id=?";
    public static final String SQL_SET_FLAGS_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "UPDATE q_has_mc SET flags=? WHERE q_mc_relation_id=?";
    public static final String SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "UPDATE q_has_mc SET position=? WHERE q_mc_relation_id=?";
    public static final String SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "INSERT INTO q_has_mc VALUES (NULL, ?, ?, ?, ?)";
    public static final String SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "DELETE FROM q_has_mc WHERE multiple_choice_id=? AND questionnaire_id=?";
    public static final String SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID = "SELECT multiple_choice_id FROM q_has_mc WHERE questionnaire_id=?";

    public static final String SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID = "q_mc_relation_id";
    public static final String SQL_COLUMN_POSITION = "position";

    //Table short_answer
    public static final String SQL_GET_SHORT_ANSWER_ID = "SELECT short_answer_id FROM short_answer WHERE question=?";
    public static final String SQL_GET_SHORT_ANSWER_ID_BY_QUESTIONNAIRE_ID = "SELECT short_answer_id FROM short_answer JOIN q_has_sa ON short_answer.short_answer_id=q_has_sa.short_answer_id WHERE question=? AND questionnaire_id=?";
    public static final String SQL_CREATE_SHORT_ANSWER = "INSERT INTO short_answer VALUES(NULL, ? , ?)";
    public static final String SQL_DELETE_SHORT_ANSWER = "DELETE FROM short_answer WHERE short_answer_id=?";

    public static final String SQL_COLUMN_SHORT_ANSWER_ID = "short_answer_id";

    //Table q_has_sa
    public static final String SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID = "SELECT q_sa_relation_id FROM q_has_sa WHERE questionnaire_id=? AND short_answer_id=?";
    public static final String SQL_SET_FLAGS_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "UPDATE q_has_sa SET flags=? WHERE q_sa_relation_id=?";
    public static final String SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "UPDATE q_has_sa SET position=? WHERE q_sa_relation_id=?";
    public static final String SQL_GET_OTHER_SHORT_ANSWER_QUESTIONNAIRE_RELATION_IDS = "SELECT q_sa_relation_id FROM q_has_sa WHERE NOT questionnaire_id= ? AND short_answer_id=?";
    public static final String SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "INSERT INTO q_has_sa VALUES (NULL, ?, ?, ?, ?)";
    public static final String SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "DELETE FROM q_has_sa WHERE short_answer_id=? AND questionnaire_id=?";
    public static final String SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID = "SELECT short_answer_id FROM q_has_sa WHERE questionnaire_id=?";

    //Table survey
    public static final String SQL_CREATE_SURVEY = "INSERT INTO survey VALUES(NULL, CURDATE(), ?)";
    public static final String SQL_GET_MAX_SURVEY_ID = "SELECT MAX(survey_id) FROM survey";

    //Table answer
    public static final String SQL_DELETE_ANSWERS = "DELETE FROM answer WHERE answer_id NOT IN (SELECT answer_id FROM mc_has_a) " +
            "AND NOT IN (SELECT answer_id FROM sa_has_a) " +
            "AND NOT(name='ja') AND NOT(name='nein') " +
            "AND NOT(name='#####') AND NOT(name='0') AND NOT(name='1') AND NOT(name='2') AND NOT(name='3') AND NOT(name='4') " +
            "AND NOT(name='5') AND NOT(name='6') AND NOT(name='7') AND NOT(name='8') AND NOT(name='9') AND NOT(name='10')";
    public static final String SQL_GET_ANSWER_ID = "SELECT answer_id FROM answer WHERE name=?";
    public static final String SQL_CREATE_ANSWER = "INSERT INTO answer VALUES(NULL, ?)";
    public static final String SQL_GET_ANSWERS = "SELECT answer.name FROM questionnaire JOIN "
            + "q_has_mc ON questionnaire.questionnaire_id=q_has_mc.questionnaire_id JOIN "
            + "multiple_choice mc1 ON q_has_mc.multiple_choice_id=mc1.multiple_choice_id JOIN "
            + "category ON mc1.category_id=category.category_id JOIN "
            + "mc_has_a ON mc1.multiple_choice_id=mc_has_a.multiple_choice_id JOIN "
            + "answer ON mc_has_a.answer_id=answer.answer_id WHERE mc1.question=? "
            + "UNION SELECT name FROM questionnaire JOIN "
            + "q_has_sa ON questionnaire.questionnaire_id=q_has_sa.questionnaire_id JOIN "
            + "short_answer ff1 ON q_has_sa.short_answer_id=ff1.short_answer_id JOIN "
            + "category ON ff1.category_id=category.category_id JOIN short_answer "
            + "JOIN sa_has_a ON ff1.short_answer_id=sa_has_a.short_answer_id JOIN "
            + "answer ON sa_has_a.answer_id=answer.answer_id WHERE ff1.question=?";
    public static final String SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS = "SELECT answer.answer_id, answer.name FROM q_has_mc JOIN multiple_choice mc ON q_has_mc.multiple_choice_id=mc.multiple_choice_id JOIN mc_has_a ON mc.multiple_choice_id=mc_has_a.multiple_choice_id JOIN answer "
            + "ON mc_has_a.answer_id=answer.answer_id WHERE mc.multiple_choice_id=? AND q_has_mc.questionnaire_id=?";

    public static final String SQL_COLUMN_ANSWER_ID = "answer_id";
    public static final String SQL_COLUMN_ANSWER_NAME = "answer.name";

    //Table mc_has_a
    public static final String SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_IDS = "SELECT mc_a_relation_id FROM mc_has_a WHERE multiple_choice_id=?";
    public static final String SQL_GET_MULTIPLE_CHOICE_ANSWERS_RELATION_ID = "SELECT mc_a_relation_id FROM mc_has_a WHERE multiple_choice_id=? AND answer_id=?";
    public static final String SQL_CREATE_MULTIPLE_CHOICE_ANSWERS_RELATION = "INSERT INTO mc_has_a VALUES(NULL, ?, ?)";
    public static final String SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_ID = "DELETE FROM mc_has_a WHERE mc_a_relation_id =?";
    public static final String SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION = "DELETE FROM mc_has_a WHERE answer_id=? AND multiple_choice_id=?";
    public static final String SQL_DELETE_MULTIPLE_CHOICE_ANSWERS_RELATION_BY_QUESTION_ID = "DELETE FROM mc_has_a WHERE multiple_choice_id=?";

    public static final String SQL_COLUMN_MULTIPLE_CHOICE_ANSWER_RELATION_ID = "mc_a_relation_id";

    //Table sa_has_a
    public static final String SQL_DELETE_SHORT_ANSWER_HAS_ANSWERS_RELATION_BY_QUESTION_ID = "DELETE FROM sa_has_a WHERE short_answer_id=?";

    //Table s_has_mc
    public static final String SQL_GET_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID = "SELECT s_mc_relation_id FROM s_has_mc WHERE survey_id=? AND multiple_choice_id=? AND answer_id=?";
    public static final String SQL_CREATE_SURVEY_HAS_MULTIPLE_CHOICE_RELATION = "INSERT INTO s_has_mc VALUES(NULL,?, ?, ?)";

    //Table s_has_sa
    public static final String SQL_GET_SURVEY_HAS_SHORT_ANSWER_RELATION_ID = "SELECT s_sa_relation_id FROM s_has_sa WHERE survey_id=? AND short_answer_id=? AND answer_id=?";
    public static final String SQL_CREATE_SURVEY_HAS_SHORT_ANSWER_RELATION = "INSERT INTO s_has_sa VALUES(NULL,?, ?, ?)";

    //Flags
    public static final String SQL_GET_SHORT_ANSWERS_FLAGS = "SELECT flags FROM q_has_sa WHERE questionnaire_id=? AND short_answer_id=?";
    public static final String SQL_GET_MULTIPLE_CHOICE_FLAGS = "SELECT flags FROM q_has_mc WHERE questionnaire_id=? AND multiple_choice_id=?";
    public static final String SQL_UPDATE_SHORT_ANSWERS_FLAGS = "UPDATE q_has_sa SET flags=?  WHERE questionnaire_id=? AND short_answer_id=?";
    public static final String SQL_UPDATE_MULTIPLE_CHOICE_FLAGS = "UPDATE q_has_mc SET flags=?  WHERE questionnaire_id=? AND multiple_choice_id=?";

    public static final String SQL_COLUMN_FLAGS = "flags";

    public static final String SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE = "SELECT flags, multiple_choice_id FROM q_has_mc WHERE flags LIKE '%__?A%' AND questionnaire_id=?";
    public static final String SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER = "SELECT flags, short_answer_id FROM q_has_sa WHERE flags LIKE '%__?A%' AND questionnaire_id=?";

    //Max Position
    public static final String SQL_GET_MAX_MULTIPLE_CHOICE_POSITION = "SELECT MAX(q_has_mc.position) AS position FROM questionnaire JOIN q_has_mc ON questionnaire.questionnaire_id=q_has_mc.questionnaire_id WHERE questionnaire.questionnaire_id=?";
    public static final String SQL_GET_MAX_SHORT_ANSWER_POSITION = "SELECT MAX(q_has_sa.position) AS position FROM questionnaire JOIN q_has_sa ON questionnaire.questionnaire_id=q_has_sa.questionnaire_id WHERE questionnaire.questionnaire_id=?";

    public static final String SQL_GET_HEADLINES = "SELECT mc1.question, mc1.multiple_choice_id, questionnaire.creation_date, q_has_mc.position, q_has_mc.flags, category.name, answer.name FROM questionnaire JOIN q_has_mc ON questionnaire.questionnaire_id=q_has_mc.questionnaire_id JOIN multiple_choice mc1 ON q_has_mc.multiple_choice_id=mc1.multiple_choice_id JOIN category ON mc1.category_id=category.category_id JOIN mc_has_a ON mc1.multiple_choice_id=mc_has_a.multiple_choice_id JOIN answer ON mc_has_a.answer_id=answer.answer_id WHERE questionnaire.questionnaire_id=? AND answer.name='#####'";
    public static final String SQL_GET_MULTIPLE_CHOICE_QUESTION = "SELECT mc1.question, mc1.multiple_choice_id, questionnaire.creation_date, q_has_mc.position, q_has_mc.flags, category.name FROM questionnaire JOIN q_has_mc ON questionnaire.questionnaire_id=q_has_mc.questionnaire_id JOIN multiple_choice mc1 ON q_has_mc.multiple_choice_id=mc1.multiple_choice_id JOIN category ON mc1.category_id=category.category_id WHERE questionnaire.questionnaire_id=?";
    public static final String SQL_GET_SHORT_ANSWER_QUESTION = "SELECT ff1.question, ff1.short_answer_id, questionnaire.creation_date, q_has_sa.position, q_has_sa.flags, category.name FROM questionnaire JOIN q_has_sa ON questionnaire.questionnaire_id=q_has_sa.questionnaire_id JOIN short_answer ff1 ON q_has_sa.short_answer_id=ff1.short_answer_id JOIN category ON ff1.category_id=category.category_id WHERE questionnaire.questionnaire_id=?";
}
