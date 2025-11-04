package de.vatrascell.nezr.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyHasShortAnswerRelationRepository extends JpaRepository<SurveyHasShortAnswerRelation, Long> {

    @Query("SELECT shsa FROM SurveyHasShortAnswerRelation shsa WHERE shsa.survey.surveyId = :surveyId AND shsa.shortAnswerQuestion.shortAnswerId = :questionId")
    Optional<SurveyHasShortAnswerRelation> findBySurveyIdAndQuestionId(@Param("surveyId") int surveyId, @Param("questionId") long questionId);

    @Modifying
    @Query(value = "INSERT INTO survey_has_short_answer (survey_id, short_answer_id, position, answer) VALUES (:surveyId, :questionId, 0, :answer)", nativeQuery = true)
    void createRelation(@Param("surveyId") int surveyId, @Param("questionId") long questionId, @Param("answer") String answer);
}