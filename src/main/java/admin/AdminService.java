package admin;

import application.Database;
import application.GlobalFuncs;
import application.GlobalVars;
import flag.SymbolType;
import model.Answer;
import model.Question;
import model.QuestionType;
import model.Questionnaire;
import question.QuestionService;
import questionList.QuestionListService;
import survey.SurveyService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static application.SqlStatement.SQL_ACTIVATE_QUESTIONNAIRE;
import static application.SqlStatement.SQL_COLUMN_LOCATION_ID;
import static application.SqlStatement.SQL_COLUMN_MAX_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_CREATE_QUESTIONNAIRE;
import static application.SqlStatement.SQL_DEACTIVATE_OTHER_QUESTIONNAIRES;
import static application.SqlStatement.SQL_DEACTIVATE_QUESTIONNAIRE;
import static application.SqlStatement.SQL_DELETE_QUESTIONNAIRE;
import static application.SqlStatement.SQL_GET_LAST_QUESTIONNAIRE_ID;
import static application.SqlStatement.SQL_GET_QUESTIONNAIRES_BY_LOCATION_ID;
import static application.SqlStatement.SQL_GET_LOCATION_ID;
import static application.SqlStatement.SQL_IS_QUESTIONNAIRE_FINAL;
import static application.SqlStatement.SQL_RENAME_QUESTIONNAIRE;
import static application.SqlStatement.SQL_SET_QUESTIONNAIRE_FINAL_STATUS;

public class AdminService extends Database {

    public static ArrayList<Questionnaire> getQuestionnaires(String location) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            ArrayList<Questionnaire> questionnaire = new ArrayList<>();
            int locationId = getLocationId(location);

            PreparedStatement psSql = myCon.prepareStatement(SQL_GET_QUESTIONNAIRES_BY_LOCATION_ID);
            psSql.setInt(1, locationId);
            ResultSet myRS = psSql.executeQuery();

            while (myRS.next()) {
                Questionnaire fb = new Questionnaire();
                fb.setName(myRS.getString("name"));
                fb.setDate(myRS.getString("date"));
                fb.setOrt(location);
                fb.setActive(myRS.getBoolean("is_active")); // anneNeu
                fb.setId(myRS.getInt("questionnaire_id")); // anneNeuFlorian
                fb.setFinal(myRS.getBoolean("is_final"));
                questionnaire.add(fb);
            }
            return questionnaire;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void activateQuestionnaire(int questionnaireId) {
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

    public static void disableQuestionnaire(int questionnaireId) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DEACTIVATE_QUESTIONNAIRE);
            psSql.setInt(1, questionnaireId);
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean copyQuestionnaire(Questionnaire questionnaire, String location) {
        questionnaire.setId(createQuestionnaire(questionnaire.getName(), location));
        List<Question> questions = SurveyService.getQuestions(questionnaire.getId());
        for (Question question : Objects.requireNonNull(questions)) {
            if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
                QuestionService.saveShortAnswerQuestion(questionnaire.getId(), question);
            } else {
                if (question.getFlags().is(SymbolType.B)) {
                    QuestionService.saveEvaluationQuestion(questionnaire.getId(), question);
                } else {
                    ArrayList<Answer> answers = new ArrayList<>();
                    for (String answerValue : question.getAnswerOptions()) {
                        Answer answer = new Answer();
                        answer.setId(QuestionService.provideAnswerId(answerValue));
                        answer.setValue(answerValue);
                        answers.add(answer);

                    }
                    QuestionService.saveMultipleChoice(questionnaire.getId(), question, answers);
                }
            }
        }
        return true;
    }

    public static boolean deleteQuestionnaire(int questionnaireId) {
        List<Integer> multipleChoiceIds = QuestionListService.getMultipleChoiceQuestionsByQuestionnaireId(questionnaireId);

        if (multipleChoiceIds != null) {
            multipleChoiceIds.forEach(questionId -> QuestionListService.deleteQuestion(questionnaireId, questionId, QuestionType.MULTIPLE_CHOICE));
        }

        List<Integer> shortAnswerIds = QuestionListService.getShortAnswerQuestionsByQuestionnaireId(questionnaireId);

        if (shortAnswerIds != null) {
            shortAnswerIds.forEach(questionId -> QuestionListService.deleteQuestion(questionnaireId, questionId, QuestionType.SHORT_ANSWER));
        }

        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_DELETE_QUESTIONNAIRE);
            psSql.setInt(1, questionnaireId);
            psSql.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean renameQuestionnaire(Questionnaire fb) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_RENAME_QUESTIONNAIRE);
            psSql.setString(1, fb.getName());
            psSql.setInt(2, fb.getId());
            psSql.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setFinal(Questionnaire fb) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_QUESTIONNAIRE_FINAL_STATUS);
            psSql.setBoolean(1, true);
            psSql.setInt(2, fb.getId());
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setUnFinal(Questionnaire fb) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_SET_QUESTIONNAIRE_FINAL_STATUS);
            psSql.setBoolean(1, false);
            psSql.setInt(2, fb.getId());
            psSql.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFinal(Questionnaire fb) {
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

    public static int createQuestionnaire(String name) {
        return createQuestionnaire(name, GlobalVars.location);
    }

    public static int createQuestionnaire(String name, String location) {
        try (Connection myCon = DriverManager.getConnection(url, user, pwd)) {
            PreparedStatement psSql = myCon.prepareStatement(SQL_CREATE_QUESTIONNAIRE);
            psSql.setString(1, GlobalFuncs.getCurrentDate());
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

    public static int getLocationId(String location) {
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
