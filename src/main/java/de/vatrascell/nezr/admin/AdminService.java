package de.vatrascell.nezr.admin;

import de.vatrascell.nezr.application.Database;
import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.util.DateUtil;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.model.Questionnaire;
import de.vatrascell.nezr.questionList.QuestionListService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static de.vatrascell.nezr.application.SqlStatement.SQL_ACTIVATE_QUESTIONNAIRE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_CREATION_DATE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_IS_ACTIVE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_IS_FINAL;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_LOCATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_MAX_QUESTIONNAIRE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_NAME;
import static de.vatrascell.nezr.application.SqlStatement.SQL_COLUMN_QUESTIONNAIRE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_CREATE_QUESTIONNAIRE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_DEACTIVATE_OTHER_QUESTIONNAIRES;
import static de.vatrascell.nezr.application.SqlStatement.SQL_DEACTIVATE_QUESTIONNAIRE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_DELETE_QUESTIONNAIRE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_LAST_QUESTIONNAIRE_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_LOCATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_GET_QUESTIONNAIRES_BY_LOCATION_ID;
import static de.vatrascell.nezr.application.SqlStatement.SQL_IS_QUESTIONNAIRE_FINAL;
import static de.vatrascell.nezr.application.SqlStatement.SQL_RENAME_QUESTIONNAIRE;
import static de.vatrascell.nezr.application.SqlStatement.SQL_SET_QUESTIONNAIRE_FINAL_STATUS;

@Service
@AllArgsConstructor
public class AdminService extends Database {

    private final QuestionListService questionListService;

    private final DateUtil dateUtil;

    public ArrayList<Questionnaire> getQuestionnaires(String location) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            ArrayList<Questionnaire> questionnaire = new ArrayList<>();
            int locationId = getLocationId(location);

            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_QUESTIONNAIRES_BY_LOCATION_ID);
            psSql.setInt(1, locationId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                Questionnaire fb = new Questionnaire();
                fb.setName(myRS.getString(SQL_COLUMN_NAME));
                fb.setDate(myRS.getString(SQL_COLUMN_CREATION_DATE));
                fb.setLocation(location);
                fb.setActive(myRS.getBoolean(SQL_COLUMN_IS_ACTIVE));
                fb.setId(myRS.getInt(SQL_COLUMN_QUESTIONNAIRE_ID));
                fb.setFinal(myRS.getBoolean(SQL_COLUMN_IS_FINAL));
                questionnaire.add(fb);
            }
            return questionnaire;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void activateQuestionnaire(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_ACTIVATE_QUESTIONNAIRE);
            psSql.setInt(1, questionnaireId);
            psSql.execute();

            myCon.prepareStatement(SQL_DEACTIVATE_OTHER_QUESTIONNAIRES);
            psSql.setInt(1, questionnaireId);
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disableQuestionnaire(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DEACTIVATE_QUESTIONNAIRE);
            psSql.setInt(1, questionnaireId);
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO refactor
    public boolean copyQuestionnaire(Questionnaire questionnaire, String location) {
        return false;
        /*questionnaire.setId(createQuestionnaire(questionnaire.getName(), de.vatrascell.nezr.location));
        List<Question> questions = QuestionListService.getQuestions(questionnaire.getId());
        for (Question de.vatrascell.nezr.question : Objects.requireNonNull(questions)) {
            if (de.vatrascell.nezr.question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
                QuestionService.saveShortAnswerQuestion(questionnaire.getId(), de.vatrascell.nezr.question);
            } else {
                QuestionService.saveMultipleChoice(questionnaire.getId(), de.vatrascell.nezr.question);
            }
        }
        return true;*/
    }

    public boolean deleteQuestionnaire(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            myCon.setAutoCommit(false);

            List<Integer> multipleChoiceIds = questionListService.getQuestionsByQuestionnaireId(questionnaireId, QuestionType.MULTIPLE_CHOICE);

            if (multipleChoiceIds != null) {
                multipleChoiceIds.forEach(questionId -> {
                    try {
                        questionListService.deleteQuestion(myCon, questionnaireId, questionId, QuestionType.MULTIPLE_CHOICE);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            List<Integer> shortAnswerIds = questionListService.getQuestionsByQuestionnaireId(questionnaireId, QuestionType.SHORT_ANSWER);

            if (shortAnswerIds != null) {
                shortAnswerIds.forEach(questionId -> {
                    try {
                        questionListService.deleteQuestion(myCon, questionnaireId, questionId, QuestionType.SHORT_ANSWER);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            deleteQuestionnaire(myCon, questionnaireId);

            myCon.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void deleteQuestionnaire(Connection connection, int questionnaireId) throws SQLException {
        PreparedStatement psSql = connection.prepareStatement(SQL_DELETE_QUESTIONNAIRE);
        psSql.setInt(1, questionnaireId);
        psSql.execute();
    }

    public boolean renameQuestionnaire(Questionnaire questionnaire) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_RENAME_QUESTIONNAIRE);
            psSql.setString(1, questionnaire.getName());
            psSql.setInt(2, questionnaire.getId());
            psSql.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setFinal(Questionnaire fb) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_QUESTIONNAIRE_FINAL_STATUS);
            psSql.setBoolean(1, true);
            psSql.setInt(2, fb.getId());
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUnFinal(Questionnaire fb) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_QUESTIONNAIRE_FINAL_STATUS);
            psSql.setBoolean(1, false);
            psSql.setInt(2, fb.getId());
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isFinal(Questionnaire fb) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_IS_QUESTIONNAIRE_FINAL);
            psSql.setInt(1, fb.getId());
            ResultSet myRS = psSql.executeQuery();

            return myRS.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int createQuestionnaire(String name) {
        return createQuestionnaire(name, GlobalVars.location.getName());
    }

    public int createQuestionnaire(String name, String location) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_QUESTIONNAIRE);
            psSql.setString(1, dateUtil.getCurrentDate());
            psSql.setString(2, name);
            psSql.setInt(3, getLocationId(location));
            psSql.execute();

            psSql = myCon.prepareStatement(SQL_GET_LAST_QUESTIONNAIRE_ID);
            ResultSet myRS = psSql.executeQuery();
            int id = -1;
            if (myRS.next()) {
                id = myRS.getInt(SQL_COLUMN_MAX_QUESTIONNAIRE_ID);
            }

            return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getLocationId(String location) {
        int locationId = -1;
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_LOCATION_ID);
            psSql.setString(1, location);
            ResultSet myRS = psSql.executeQuery();
            if (myRS.next()) {
                locationId = myRS.getInt(SQL_COLUMN_LOCATION_ID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locationId;
    }
}
