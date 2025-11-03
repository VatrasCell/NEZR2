package de.vatrascell.nezr.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {

    @Query("SELECT new MultipleChoiceQuestion(" +
            "mc.question, mc.multipleChoiceId, q.creationDate, qhmc.position, " +
            "c, h, qhmc.qMcRelationId, f) " +
            "FROM Questionnaire q " +
            "JOIN QuestionnaireHasMultipleChoiceRelation qhmc ON qhmc.questionnaire = q " +
            "JOIN MultipleChoiceQuestion mc ON qhmc.multipleChoiceQuestion = mc " +
            "LEFT JOIN mc.headline h " +
            "LEFT JOIN FlagListMultipleChoice f ON f.qMcRelationId = qhmc.qMcRelationId " +
            "JOIN mc.category c " +
            "WHERE q.questionnaireId = :questionnaireId ")
    List<MultipleChoiceQuestion> findMultipleChoiceQuestionsByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    @Query(value = "SELECT answer.answer_id, answer.name FROM questionnaire_has_multiple_choice JOIN multiple_choice mc ON questionnaire_has_multiple_choice.multiple_choice_id=mc.multiple_choice_id JOIN mc_has_a ON mc.multiple_choice_id=mc_has_a.multiple_choice_id JOIN answer ON mc_has_a.answer_id=answer.answer_id WHERE mc.multiple_choice_id=:questionId AND questionnaire_has_multiple_choice.questionnaire_id=:questionnaireId", nativeQuery = true)
    List<Object[]> findMultipleChoiceQuestionAnswers(@Param("questionnaireId") int questionnaireId, @Param("questionId") int questionId);

    @Query(value = "SELECT multiple_choice_id FROM questionnaire_has_multiple_choice WHERE questionnaire_id=:questionnaireId", nativeQuery = true)
    List<Integer> findMultipleChoiceIdsByQuestionnaireId(@Param("questionnaireId") int questionnaireId);

    @Query(value = "SELECT q_mc_relation_id FROM questionnaire_has_multiple_choice WHERE NOT questionnaire_id=:questionnaireId AND multiple_choice_id=:questionId", nativeQuery = true)
    List<Integer> findOtherMultipleChoiceQuestionnaireRelationIds(@Param("questionnaireId") int questionnaireId, @Param("questionId") int questionId);

    @Query(value = "DELETE FROM multiple_choice WHERE multiple_choice_id=:questionId", nativeQuery = true)
    void deleteMultipleChoiceQuestion(@Param("questionId") int questionId);

    @Query(value = "DELETE FROM multiple_choice_has_answer_option WHERE multiple_choice_id=:questionId", nativeQuery = true)
    void deleteMultipleChoiceHasAnswerRelation(@Param("questionId") int questionId);

    @Query(value = "DELETE FROM questionnaire_has_multiple_choice WHERE multiple_choice_id=:questionId AND questionnaire_id=:questionnaireId", nativeQuery = true)
    void deleteMultipleChoiceQuestionnaireRelation(@Param("questionnaireId") int questionnaireId, @Param("questionId") int questionId);

    @Query("SELECT MAX(qhmc.position) FROM QuestionnaireHasMultipleChoiceRelation qhmc WHERE qhmc.questionnaire.questionnaireId = :questionnaireId")
    Integer findMaxPositionByQuestionnaireId(@Param("questionnaireId") Long questionnaireId);

    @Query("SELECT mchao.mcAoRelationId FROM MultipleChoiceHasAnswerOptionRelation mchao WHERE mchao.multipleChoiceQuestion.multipleChoiceId = :multipleChoiceId")
    List<Long> findRelationIdsByMultipleChoiceId(@Param("multipleChoiceId") Long multipleChoiceId);

    @Query("SELECT mchao.mcAoRelationId FROM MultipleChoiceHasAnswerOptionRelation mchao WHERE mchao.multipleChoiceQuestion.multipleChoiceId = :multipleChoiceId AND mchao.answerOption.answerOptionId = :answerId")
    Long findRelationIdByIds(@Param("multipleChoiceId") Long multipleChoiceId, @Param("answerId") Long answerId);

    @Query("SELECT qhmc.qMcRelationId FROM QuestionnaireHasMultipleChoiceRelation qhmc WHERE qhmc.questionnaire.questionnaireId = :questionnaireId AND qhmc.multipleChoiceQuestion.multipleChoiceId = :questionId")
    Long findRelationIdByQuestionnaireAndQuestion(@Param("questionnaireId") Long questionnaireId, @Param("questionId") Long questionId);

    @Query("SELECT mc.multipleChoiceId FROM MultipleChoiceQuestion mc WHERE mc.question = :question")
    Long findIdByQuestion(@Param("question") String question);

    @Modifying
    @Query("UPDATE QuestionnaireHasMultipleChoiceRelation qhmc SET qhmc.position = :position WHERE qhmc.qMcRelationId = :relationId")
    void updatePosition(@Param("position") int position, @Param("relationId") Long relationId);

    @Modifying
    @Query(value = "INSERT INTO questionnaire_has_multiple_choice (questionnaire_id, multiple_choice_id, position) VALUES (:questionnaireId, :multipleChoiceId, :position)", nativeQuery = true)
    void createRelation(@Param("questionnaireId") int questionnaireId, @Param("multipleChoiceId") int multipleChoiceId, @Param("position") int position);

    @Modifying
    @Query(value = "INSERT INTO multiple_choice (question, category_id) VALUES (:question, :categoryId)", nativeQuery = true)
    void createMultipleChoice(@Param("question") String question, @Param("categoryId") int categoryId);

    @Modifying
    @Query("UPDATE MultipleChoiceQuestion mc SET mc.category.categoryId = :categoryId WHERE mc.multipleChoiceId = :questionId")
    void updateCategory(@Param("categoryId") Long categoryId, @Param("questionId") Long questionId);

    @Modifying
    @Query("UPDATE MultipleChoiceQuestion mc SET mc.headline.headlineId = :headlineId WHERE mc.multipleChoiceId = :questionId")
    void updateHeadline(@Param("headlineId") Long headlineId, @Param("questionId") Long questionId);

    @Query(value = "SELECT q_mc_relation_id, multiple_choice_id FROM questionnaire_has_multiple_choice WHERE multiple_choice_id IN (SELECT target_multiple_choice_id FROM multiple_choice_has_react WHERE multiple_choice_id = :questionId) AND questionnaire_id = :questionnaireId", nativeQuery = true)
    List<Object[]> findTargetQuestionRelationsForMultipleChoice(@Param("questionId") int questionId, @Param("questionnaireId") int questionnaireId);
}
