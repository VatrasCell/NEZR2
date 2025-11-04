package de.vatrascell.nezr.survey;

import de.vatrascell.nezr.answerOption.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Modifying
    @Query(value = "INSERT INTO survey (questionnaire_id) VALUES (:questionnaireId)", nativeQuery = true)
    void createSurvey(@Param("questionnaireId") int questionnaireId);

    @Query("SELECT MAX(s.surveyId) FROM Survey s")
    Integer findMaxSurveyId();

    @Query("SELECT s FROM Survey s WHERE s.questionnaire.questionnaireId = :questionnaireId AND s.creationDate BETWEEN :fromDate AND :toDate ORDER BY s.creationDate")
    List<Survey> findSurveysByQuestionnaireIdAndDateRange(@Param("questionnaireId") int questionnaireId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

    @Query("SELECT shsa.answer FROM SurveyHasShortAnswerRelation shsa WHERE shsa.survey.surveyId = :surveyId AND shsa.shortAnswerQuestion.shortAnswerId = :questionId")
    String findShortAnswerBySurveyIdAndQuestionId(@Param("surveyId") int surveyId, @Param("questionId") long questionId);

    @Query("SELECT ao FROM SurveyHasAnswerOptionRelation shao JOIN shao.answerOption ao WHERE shao.surveyHasMultipleChoiceRelation.survey.surveyId = :surveyId AND shao.surveyHasMultipleChoiceRelation.multipleChoiceQuestion.multipleChoiceId = :questionId")
    List<AnswerOption> findMultipleChoiceAnswersBySurveyIdAndQuestionId(@Param("surveyId") int surveyId, @Param("questionId") long questionId);
}
