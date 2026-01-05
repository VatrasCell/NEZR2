package de.vatrascell.nezr.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortAnswerQuestionRepository extends JpaRepository<ShortAnswerQuestion, Long> {

    @Query("SELECT new ShortAnswerQuestion(" +
            "sa.question, sa.shortAnswerId, q.creationDate, qhsa.position, " +
            "c, sa.headline, qhsa.qSaRelationId, f, v) " +
            "FROM Questionnaire q " +
            "JOIN QuestionnaireHasShortAnswerRelation qhsa ON qhsa.questionnaire = q " +
            "JOIN qhsa.shortAnswerQuestion sa ON qhsa.shortAnswerQuestion = sa " +
            "LEFT JOIN sa.headline h " +
            "LEFT JOIN FlagListShortAnswer f ON f.qSaRelationId = qhsa.qSaRelationId " +
            "JOIN sa.category c " +
            "LEFT JOIN qhsa.validation v " +
            "WHERE q.questionnaireId = :questionnaireId")
    List<ShortAnswerQuestion> findShortAnswerQuestionsByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    @Query(value = "SELECT short_answer_id FROM questionnaire_has_short_answer WHERE questionnaire_id=:questionnaireId", nativeQuery = true)
    List<Integer> findShortAnswerIdsByQuestionnaireId(@Param("questionnaireId") int questionnaireId);

    @Query(value = "SELECT q_sa_relation_id FROM questionnaire_has_short_answer WHERE NOT questionnaire_id=:questionnaireId AND short_answer_id=:questionId", nativeQuery = true)
    List<Integer> findOtherShortAnswerQuestionnaireRelationIds(@Param("questionnaireId") int questionnaireId, @Param("questionId") int questionId);

    @Query(value = "DELETE FROM short_answer WHERE short_answer_id=:questionId", nativeQuery = true)
    void deleteShortAnswerQuestion(@Param("questionId") int questionId);

    @Query(value = "DELETE FROM questionnaire_has_short_answer WHERE short_answer_id=:questionId AND questionnaire_id=:questionnaireId", nativeQuery = true)
    void deleteShortAnswerQuestionnaireRelation(@Param("questionnaireId") int questionnaireId, @Param("questionId") int questionId);

    @Query("SELECT MAX(qhsa.position) FROM QuestionnaireHasShortAnswerRelation qhsa WHERE qhsa.questionnaire.questionnaireId = :questionnaireId")
    Integer findMaxPositionByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    @Query("SELECT qhsa.qSaRelationId FROM QuestionnaireHasShortAnswerRelation qhsa WHERE qhsa.questionnaire.questionnaireId = :questionnaireId AND qhsa.shortAnswerQuestion.shortAnswerId = :questionId")
    Long findRelationIdByQuestionnaireAndQuestion(@Param("questionnaireId") Long questionnaireId, @Param("questionId") Long questionId);

    @Query("SELECT sa.shortAnswerId FROM ShortAnswerQuestion sa WHERE sa.question = :question")
    Long findIdByQuestion(@Param("question") String question);

    @Modifying
    @Query("UPDATE QuestionnaireHasShortAnswerRelation qhsa SET qhsa.position = :position WHERE qhsa.qSaRelationId = :relationId")
    void updatePosition(@Param("position") int position, @Param("relationId") Long relationId);

    @Modifying
    @Query(value = "INSERT INTO questionnaire_has_short_answer (questionnaire_id, short_answer_id, position) VALUES (:questionnaireId, :shortAnswerId, :position)", nativeQuery = true)
    void createRelation(@Param("questionnaireId") int questionnaireId, @Param("shortAnswerId") int shortAnswerId, @Param("position") int position);

    @Modifying
    @Query(value = "INSERT INTO questionnaire_has_short_answer (questionnaire_id, short_answer_id, position, validation_id) VALUES (:questionnaireId, :shortAnswerId, :position, :validationId)", nativeQuery = true)
    void createRelationWithValidation(@Param("questionnaireId") int questionnaireId, @Param("shortAnswerId") int shortAnswerId, @Param("position") int position, @Param("validationId") Integer validationId);

    @Modifying
    @Query(value = "INSERT INTO short_answer (question, category_id) VALUES (:question, :categoryId)", nativeQuery = true)
    void createShortAnswer(@Param("question") String question, @Param("categoryId") Long categoryId);

    @Modifying
    @Query("UPDATE ShortAnswerQuestion sa SET sa.category.categoryId = :categoryId WHERE sa.shortAnswerId = :questionId")
    void updateCategory(@Param("categoryId") Long categoryId, @Param("questionId") Long questionId);

    @Modifying
    @Query("UPDATE ShortAnswerQuestion sa SET sa.headline.headlineId = :headlineId WHERE sa.shortAnswerId = :questionId")
    void updateHeadline(@Param("headlineId") Long headlineId, @Param("questionId") Long questionId);

    @Query(value = "SELECT q_sa_relation_id, short_answer_id FROM questionnaire_has_short_answer WHERE short_answer_id IN (SELECT target_short_answer_id FROM short_answer_has_react WHERE short_answer_id = :questionId) AND questionnaire_id = :questionnaireId", nativeQuery = true)
    List<Object[]> findTargetQuestionRelationsForShortAnswer(@Param("questionId") int questionId, @Param("questionnaireId") int questionnaireId);
}
