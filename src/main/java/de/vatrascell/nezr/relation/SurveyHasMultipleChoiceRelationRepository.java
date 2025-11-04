package de.vatrascell.nezr.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyHasMultipleChoiceRelationRepository extends JpaRepository<SurveyHasMultipleChoiceRelation, Long> {

    @Query("SELECT shmc FROM SurveyHasMultipleChoiceRelation shmc WHERE shmc.survey.surveyId = :surveyId AND shmc.multipleChoiceQuestion.multipleChoiceId = :questionId")
    Optional<SurveyHasMultipleChoiceRelation> findBySurveyIdAndQuestionId(@Param("surveyId") int surveyId, @Param("questionId") long questionId);

    @Modifying
    @Query(value = "INSERT INTO survey_has_multiple_choice (survey_id, multiple_choice_id, position) VALUES (:surveyId, :questionId, 0)", nativeQuery = true)
    void createRelation(@Param("surveyId") int surveyId, @Param("questionId") long questionId);
}