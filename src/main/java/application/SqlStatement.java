package application;

public class SqlStatement {

    private static final String SQL_MAX = "MAX(%s)";
    private static final String SQL_COUNT = "COUNT(%s)";

    public static final String SQL_COLUMN_NAME = "name";

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
    public static final String SQL_COLUMN_IS_ACTIVE = "is_active";
    public static final String SQL_COLUMN_IS_FINAL = "is_final";

    //Table category
    public static final String SQL_CREATE_CATEGORY = "INSERT INTO category VALUES(NULL, ?)";
    public static final String SQL_GET_CATEGORY_BY_NAME = "SELECT * FROM category WHERE name=?";
    public static final String SQL_GET_CATEGORIES = "SELECT * FROM category";

    public static final String SQL_COLUMN_CATEGORY_ID = "category_id";
    public static final String SQL_COLUMN_CATEGORY_NAME = "category.name";

    //Table multiple_choice
    public static final String SQL_GET_MULTIPLE_CHOICE_ID = "SELECT multiple_choice_id FROM multiple_choice WHERE question=?";
    public static final String SQL_CREATE_MULTIPLE_CHOICE = "INSERT INTO multiple_choice VALUES(NULL, ?, ?, NULL)";
    public static final String SQL_DELETE_MULTIPLE_CHOICE = "DELETE FROM multiple_choice WHERE multiple_choice_id=?";
    public static final String SQL_SET_CATEGORY_ON_MULTIPLE_CHOICE = "UPDATE multiple_choice SET category_id=? WHERE multiple_choice_id=?";
    public static final String SQL_SET_HEADLINE_ON_MULTIPLE_CHOICE = "UPDATE multiple_choice SET headline_id=? WHERE multiple_choice_id=?";

    public static final String SQL_COLUMN_QUESTION = "question";
    public static final String SQL_COLUMN_MULTIPLE_CHOICE_ID = "multiple_choice_id";

    //Table q_has_mc
    public static final String SQL_GET_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID = "SELECT q_mc_relation_id FROM q_has_mc WHERE questionnaire_id= ? AND multiple_choice_id=?";
    public static final String SQL_GET_OTHER_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_IDS = "SELECT q_mc_relation_id FROM q_has_mc WHERE NOT questionnaire_id= ? AND multiple_choice_id=?";
    public static final String SQL_SET_POSITION_ON_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "UPDATE q_has_mc SET position=? WHERE q_mc_relation_id=?";
    public static final String SQL_CREATE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "INSERT INTO q_has_mc VALUES (NULL, ?, ?, ?, ?)";
    public static final String SQL_DELETE_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION = "DELETE FROM q_has_mc WHERE multiple_choice_id=? AND questionnaire_id=?";
    public static final String SQL_GET_MULTIPLE_CHOICE_IDS_BY_QUESTIONNAIRE_ID = "SELECT multiple_choice_id FROM q_has_mc WHERE questionnaire_id=?";

    public static final String SQL_COLUMN_MULTIPLE_CHOICE_QUESTIONNAIRE_RELATION_ID = "q_mc_relation_id";
    public static final String SQL_COLUMN_POSITION = "position";

    //Table short_answer
    public static final String SQL_GET_SHORT_ANSWER_ID = "SELECT short_answer_id FROM short_answer WHERE question=?";
    public static final String SQL_GET_SHORT_ANSWER_ID_BY_QUESTIONNAIRE_ID = "SELECT short_answer_id FROM short_answer JOIN q_has_sa ON short_answer.short_answer_id=q_has_sa.short_answer_id WHERE question=? AND questionnaire_id=?";
    public static final String SQL_CREATE_SHORT_ANSWER = "INSERT INTO short_answer VALUES(NULL, ? , ?, NULL)";
    public static final String SQL_DELETE_SHORT_ANSWER = "DELETE FROM short_answer WHERE short_answer_id=?";
    public static final String SQL_SET_CATEGORY_ON_SHORT_ANSWER = "UPDATE short_answer SET category_id=? WHERE short_answer_id=?";
    public static final String SQL_SET_HEADLINE_ON_SHORT_ANSWER = "UPDATE short_answer SET headline_id=? WHERE short_answer_id=?";

    public static final String SQL_COLUMN_SHORT_ANSWER_ID = "short_answer_id";

    //Table q_has_sa
    public static final String SQL_GET_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID = "SELECT q_sa_relation_id FROM q_has_sa WHERE questionnaire_id=? AND short_answer_id=?";
    public static final String SQL_SET_POSITION_ON_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "UPDATE q_has_sa SET position=? WHERE q_sa_relation_id=?";
    public static final String SQL_GET_OTHER_SHORT_ANSWER_QUESTIONNAIRE_RELATION_IDS = "SELECT q_sa_relation_id FROM q_has_sa WHERE NOT questionnaire_id= ? AND short_answer_id=?";
    public static final String SQL_CREATE_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "INSERT INTO q_has_sa VALUES (NULL, ?, ?, ?, ?)";
    public static final String SQL_DELETE_SHORT_ANSWER_QUESTIONNAIRE_RELATION = "DELETE FROM q_has_sa WHERE short_answer_id=? AND questionnaire_id=?";
    public static final String SQL_GET_SHORT_ANSWER_IDS_BY_QUESTIONNAIRE_ID = "SELECT short_answer_id FROM q_has_sa WHERE questionnaire_id=?";

    public static final String SQL_COLUMN_SHORT_ANSWER_QUESTIONNAIRE_RELATION_ID = "q_sa_relation_id";

    //Table survey
    public static final String SQL_CREATE_SURVEY = "INSERT INTO survey VALUES(NULL, CURDATE(), ?)";
    public static final String SQL_GET_MAX_SURVEY_ID = "SELECT MAX(survey_id) FROM survey";
    public static final String SQL_GET_SURVEY_COUNT = "SELECT COUNT(survey_id) FROM survey";
    public static final String SQL_GET_MULTIPLE_CHOICE_SURVEY_ID_AND_ANSWER = "SELECT s.survey_id AS survey_id, ao.name AS name FROM survey s JOIN s_has_mc smc ON s.survey_id = smc.survey_id JOIN s_has_ao sao ON smc.s_mc_relation_id = sao.s_has_mc_id JOIN answer_option ao ON sao.answer_option_id = ao.answer_option_id WHERE s.questionnaire_id=? AND smc.multiple_choice_id=? AND (s.creation_date BETWEEN ? AND ?)";
    public static final String SQL_GET_SHORT_ANSWER_SURVEY_ID_AND_ANSWER = "SELECT s.survey_id AS survey_id, ssa.answer AS answer FROM survey s JOIN s_has_sa ssa ON s.survey_id = ssa.survey_id WHERE s.questionnaire_id=? AND ssa.short_answer_id=? AND (s.creation_date BETWEEN ? AND ?)";
    public static final String SQL_GET_MULTIPLE_CHOICE_SURVEY_ID_BY_ANSWER = "SELECT s.survey_id AS survey_id FROM survey s JOIN s_has_mc smc ON s.survey_id = smc.survey_id JOIN s_has_ao sao ON smc.s_mc_relation_id = sao.s_has_mc_id WHERE s.questionnaire_id=? AND smc.multiple_choice_id=? AND sao.answer_option_id=? AND (s.creation_date BETWEEN ? AND ?)";

    public static final String SQL_COLUMN_SURVEY_ID = "survey_id";
    public static final String SQL_COLUMN_ANSWER = "answer";
    public static final String SQL_COLUMN_SURVEY_ID_COUNT = String.format(SQL_COUNT, SQL_COLUMN_SURVEY_ID);
    public static final String SQL_COLUMN_SURVEY_ID_MAX = String.format(SQL_MAX, SQL_COLUMN_SURVEY_ID);

    //Table answer
    public static final String SQL_DELETE_UNBINDED_ANSWERS = "DELETE FROM answer WHERE answer_id NOT IN (SELECT answer_id FROM mc_has_a) " +
            "AND NOT IN (SELECT answer_id FROM sa_has_a) " +
            "AND NOT(name='ja') AND NOT(name='nein') " +
            "AND NOT(name='#####') AND NOT(name='0') AND NOT(name='1') AND NOT(name='2') AND NOT(name='3') AND NOT(name='4') " +
            "AND NOT(name='5') AND NOT(name='6') AND NOT(name='7') AND NOT(name='8') AND NOT(name='9') AND NOT(name='10')";
    public static final String SQL_GET_ANSWER_ID = "SELECT answer_id FROM answer WHERE name=?";
    public static final String SQL_CREATE_ANSWER = "INSERT INTO answer VALUES(NULL, ?)";
    public static final String SQL_GET_ANSWER_OPTIONS = "SELECT ao.answer_option_id AS answer_option_id, ao.name FROM answer_option ao JOIN mc_has_ao rel ON ao.answer_option_id = rel.answer_option_id WHERE multiple_choice_id = ?";
    public static final String SQL_GET_ANSWER_OPTION = "SELECT * FROM answer_option WHERE name = ?";
    public static final String SQL_GET_MULTIPLE_CHOICE_QUESTION_ANSWERS = "SELECT answer.answer_id, answer.name FROM q_has_mc JOIN multiple_choice mc ON q_has_mc.multiple_choice_id=mc.multiple_choice_id JOIN mc_has_a ON mc.multiple_choice_id=mc_has_a.multiple_choice_id JOIN answer "
            + "ON mc_has_a.answer_id=answer.answer_id WHERE mc.multiple_choice_id=? AND q_has_mc.questionnaire_id=?";

    public static final String SQL_COLUMN_ANSWER_OPTION_ID = "answer_option_id";
    public static final String SQL_COLUMN_ANSWER_OPTION_NAME = "ao.name";

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
    public static final String SQL_GET_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID = "SELECT s_mc_relation_id FROM s_has_mc WHERE survey_id=? AND multiple_choice_id=?";
    public static final String SQL_CREATE_SURVEY_HAS_MULTIPLE_CHOICE_RELATION = "INSERT INTO s_has_mc VALUES(NULL, ?, ?)";

    public static final String SQL_COLUMN_SURVEY_HAS_MULTIPLE_CHOICE_RELATION_ID = "s_mc_relation_id";

    //Table s_has_ao
    public static final String SQL_GET_SURVEY_HAS_ANSWER_OPTION_RELATION_ID = "SELECT s_has_ao_id FROM s_has_ao WHERE answer_option_id=? AND s_has_mc_id=?";
    public static final String SQL_CREATE_SURVEY_HAS_ANSWER_OPTION_RELATION = "INSERT INTO s_has_ao VALUES(NULL, ?, ?)";

    public static final String SQL_COLUMN_SURVEY_HAS_ANSWER_OPTION_RELATION_ID = "s_has_ao_id";

    //Table s_has_sa
    public static final String SQL_GET_SURVEY_HAS_SHORT_ANSWER_RELATION_ID = "SELECT s_sa_relation_id FROM s_has_sa WHERE survey_id=? AND short_answer_id=?";
    public static final String SQL_CREATE_SURVEY_HAS_SHORT_ANSWER_RELATION = "INSERT INTO s_has_sa VALUES(NULL,?, ?, ?)";

    //Table headline
    public static final String SQL_GET_HEADLINES = "SELECT * FROM headline";
    public static final String SQL_GET_HEADLINE_BY_ID = "SELECT * FROM headline WHERE headline_id=?";
    public static final String SQL_CREATE_HEADLINE = "INSERT INTO headline VALUES(NULL, ?)";
    public static final String SQL_GET_HEADLINE_BY_NAME = "SELECT * FROM headline WHERE name=?";
    public static final String SQL_GET_HEADLINE_ID = "SELECT headline_id FROM headline WHERE name=?";

    public static final String SQL_COLUMN_HEADLINE_ID = "headline_id";
    public static final String SQL_COLUMN_HEADLINE_ID2 = "headline_headline_id";

    //FlagList
    public static final String SQL_GET_FLAG_LIST_MC_BY_ID = "SELECT * FROM flag_list_mc WHERE flag_list_mc_id=?";
    public static final String SQL_GET_FLAG_LIST_SA_BY_ID = "SELECT * FROM flag_list_sa WHERE flag_list_sa_id=?";

    public static final String SQL_UPDATE_FLAG_LIST_MC = "UPDATE flag_list_mc SET is_evaluation_question=?, is_required=?, is_multiple_choice=?, is_list=?, is_yes_no_question=?, is_single_line=? WHERE flag_list_mc_id=?";
    public static final String SQL_UPDATE_FLAG_LIST_SA = "UPDATE flag_list_sa SET is_required=?, is_text_area=? WHERE flag_list_sa_id=?";

    public static final String SQL_SET_FLAG_LIST_MC_REQUIRED = "UPDATE flag_list_mc SET is_required=TRUE WHERE flag_list_mc_id=?";
    public static final String SQL_SET_FLAG_LIST_SA_REQUIRED = "UPDATE flag_list_sa SET is_required=TRUE WHERE flag_list_sa_id=?";

    public static final String SQL_GET_FLAG_LIST_ID_ON_SHORT_ANSWER = "SELECT flag_list_id FROM flag_list WHERE questionnaire_id = ? AND short_answer_id = ?";
    public static final String SQL_GET_FLAG_LIST_ID_ON_MULTIPLE_CHOICE = "SELECT flag_list_id FROM flag_list WHERE questionnaire_id = ? AND multiple_choice_id = ?";

    public static final String SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_MULTIPLE_CHOICE = "SELECT  flag_list_id, qhmc.multiple_choice_id, qhmc.q_mc_relation_id FROM q_has_mc qhmc JOIN q_has_react qhr ON qhmc.q_mc_relation_id = qhr.q_has_mc_id JOIN react r ON qhr.react_id = r.react_id WHERE r.short_answer_id = ? OR r.multiple_choice_id = ? AND  questionnaire_id = ?";
    public static final String SQL_GET_TARGET_QUESTION_FLAG_AND_ID_FOR_SHORT_ANSWER = "SELECT  flag_list_id, qhsa.short_answer_id, qhsa.q_sa_relation_id FROM q_has_sa qhsa JOIN q_has_react qhr ON qhsa.q_sa_relation_id = qhr.q_has_sa_id JOIN react r ON qhr.react_id = r.react_id WHERE r.short_answer_id = ? OR r.multiple_choice_id = ? AND  questionnaire_id = ?";

    public static final String SQL_COLUMN_FLAG_LIST_ID = "flag_list_id";
    public static final String SQL_COLUMN_FLAG_LIST_MC_ID = "flag_list_mc_id";
    public static final String SQL_COLUMN_FLAG_LIST_SA_ID = "flag_list_sa_id";
    public static final String SQL_COLUMN_IS_EVALUATION_QUESTION = "is_evaluation_question";
    public static final String SQL_COLUMN_IS_REQUIRED = "is_required";
    public static final String SQL_COLUMN_IS_MULTIPLE_CHOICE = "is_multiple_choice";
    public static final String SQL_COLUMN_IS_LIST = "is_list";
    public static final String SQL_COLUMN_IS_TEXT_AREA = "is_text_area";
    public static final String SQL_COLUMN_IS_YES_NO_QUESTION = "is_yes_no_question";
    public static final String SQL_COLUMN_IS_SINGLE_LINE = "is_single_line";

    //--
    public static final String SQL_GET_SHORT_ANSWERS_FLAGS = "SELECT flags FROM q_has_sa WHERE questionnaire_id=? AND short_answer_id=?";
    public static final String SQL_GET_MULTIPLE_CHOICE_FLAGS = "SELECT flags FROM q_has_mc WHERE questionnaire_id=? AND multiple_choice_id=?";
    public static final String SQL_UPDATE_SHORT_ANSWERS_FLAGS = "UPDATE q_has_sa SET flags=?  WHERE questionnaire_id=? AND short_answer_id=?";
    public static final String SQL_UPDATE_MULTIPLE_CHOICE_FLAGS = "UPDATE q_has_mc SET flags=?  WHERE questionnaire_id=? AND multiple_choice_id=?";
    //--

    //Validation
    public static final String SQL_GET_VALIDATION_BY_SA_REL_ID = "SELECT * FROM validation v JOIN q_has_sa qsa ON v.validation_id = qsa.validation_id WHERE qsa.q_sa_relation_id=?";


    public static final String SQL_COLUMN_VALIDATION_ID = "validation_id";
    public static final String SQL_COLUMN_IS_NUMBERS = "is_numbers";
    public static final String SQL_COLUMN_IS_LETTERS = "is_letters";
    public static final String SQL_COLUMN_IS_ALPHANUMERIC = "is_alphanumeric";
    public static final String SQL_COLUMN_IS_ALL_CHARS = "is_all_chars";
    public static final String SQL_COLUMN_IS_REGEX = "is_regex";
    public static final String SQL_COLUMN_HAS_LENGTH = "has_length";
    public static final String SQL_COLUMN_REGEX = "regex";
    public static final String SQL_COLUMN_MIN_LENGTH = "min_length";
    public static final String SQL_COLUMN_MAX_LENGTH = "max_length";
    public static final String SQL_COLUMN_LENGTH = "length";

    //React
    public static final String SQL_GET_SHORT_ANSWER_REACTS = "SELECT r.react_id, r.short_answer_id, r.multiple_choice_id, r.answer_position FROM react r JOIN sa_has_react sar ON r.react_id = sar.react_id WHERE sar.q_has_sa_id = ?";
    public static final String SQL_GET_MULTIPLE_CHOICE_REACTS = "SELECT r.react_id, r.short_answer_id, r.multiple_choice_id, r.answer_position FROM react r JOIN mc_has_react mcr ON r.react_id = mcr.react_id WHERE mcr.q_has_mc_id = ?";

    public static final String SQL_COLUMN_REACT_ID = "react_id";
    public static final String SQL_COLUMN_ANSWER_POSITION = "answer_position";

    //Max Position
    public static final String SQL_GET_MAX_MULTIPLE_CHOICE_POSITION = "SELECT MAX(q_has_mc.position) AS position FROM questionnaire JOIN q_has_mc ON questionnaire.questionnaire_id=q_has_mc.questionnaire_id WHERE questionnaire.questionnaire_id=?";
    public static final String SQL_GET_MAX_SHORT_ANSWER_POSITION = "SELECT MAX(q_has_sa.position) AS position FROM questionnaire JOIN q_has_sa ON questionnaire.questionnaire_id=q_has_sa.questionnaire_id WHERE questionnaire.questionnaire_id=?";

    public static final String SQL_GET_MULTIPLE_CHOICE_QUESTION = "SELECT mc1.question, mc1.multiple_choice_id, questionnaire.creation_date, q_has_mc.position, category.name, mc1.headline_id, q_has_mc.q_mc_relation_id FROM questionnaire JOIN q_has_mc ON questionnaire.questionnaire_id=q_has_mc.questionnaire_id JOIN multiple_choice mc1 ON q_has_mc.multiple_choice_id=mc1.multiple_choice_id JOIN category ON mc1.category_id=category.category_id WHERE questionnaire.questionnaire_id=?";
    public static final String SQL_GET_SHORT_ANSWER_QUESTION = "SELECT ff1.question, ff1.short_answer_id, questionnaire.creation_date, q_has_sa.position, category.name, ff1.headline_headline_id, q_has_sa.q_sa_relation_id FROM questionnaire JOIN q_has_sa ON questionnaire.questionnaire_id=q_has_sa.questionnaire_id JOIN short_answer ff1 ON q_has_sa.short_answer_id=ff1.short_answer_id JOIN category ON ff1.category_id=category.category_id WHERE questionnaire.questionnaire_id=?";
}
