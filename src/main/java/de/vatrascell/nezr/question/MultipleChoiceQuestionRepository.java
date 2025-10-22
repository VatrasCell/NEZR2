package de.vatrascell.nezr.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {

    @Query("SELECT new de.vatrascell.nezr.question.MultipleChoiceQuestion(" +
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

    @Query("SELECT new de.vatrascell.nezr.question.AnswerOption(ao) " +
            "FROM MultipleChoiceQuestion mc " +
            "JOIN MultipleChoiceHasAnswerOptionRelation mchao ON mchao.multipleChoiceQuestion = mc " +
            "JOIN AnswerOption ao ON mchao.answerOption = ao " +
            "WHERE mc.multipleChoiceId = :questionId")
    List<AnswerOption> findAnswerOptionsWithQuestionIdsByQuestionId(@Param("questionId") Long questionId);

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
}
