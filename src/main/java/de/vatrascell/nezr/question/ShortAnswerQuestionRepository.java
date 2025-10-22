package de.vatrascell.nezr.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortAnswerQuestionRepository extends JpaRepository<ShortAnswerQuestion, Long> {

    @Override
    List<ShortAnswerQuestion> findAll();

    @Query("SELECT new de.vatrascell.nezr.question.ShortAnswerQuestion(" +
            "sa.question, sa.shortAnswerId, q.creationDate, qhsa.position, " +
            "c, sa.headline, qhsa.qSaRelationId) " +
            "FROM Questionnaire q " +
            "JOIN QuestionnaireHasShortAnswerRelation qhsa ON qhsa.questionnaire = q " +
            "JOIN qhsa.shortAnswerQuestion sa ON qhsa.shortAnswerQuestion = sa " +
            "LEFT JOIN sa.headline h " +
            "JOIN sa.category c " +
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
}
