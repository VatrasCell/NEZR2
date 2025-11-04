package de.vatrascell.nezr.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyHasAnswerOptionRelationRepository extends JpaRepository<SurveyHasAnswerOptionRelation, Long> {

    @Query("SELECT shao FROM SurveyHasAnswerOptionRelation shao WHERE shao.surveyHasMultipleChoiceRelation.sMcRelationId = :relationId AND shao.answerOption.answerOptionId = :answerId")
    Optional<SurveyHasAnswerOptionRelation> findByRelationIdAndAnswerId(@Param("relationId") long relationId, @Param("answerId") int answerId);

    @Modifying
    @Query(value = "INSERT INTO survey_has_answer_option (answer_option_id, s_mc_relation_id) VALUES (:answerId, :relationId)", nativeQuery = true)
    void createRelation(@Param("answerId") int answerId, @Param("relationId") long relationId);
}