package de.vatrascell.nezr.answerOption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
    @Query("SELECT new AnswerOption(ao) " +
            "FROM MultipleChoiceQuestion mc " +
            "JOIN MultipleChoiceHasAnswerOptionRelation mchao ON mchao.multipleChoiceQuestion = mc " +
            "JOIN AnswerOption ao ON mchao.answerOption = ao " +
            "WHERE mc.multipleChoiceId = :questionId")
    List<AnswerOption> findAnswerOptionsByQuestionId(@Param("questionId") Long questionId);

    AnswerOption findByName(@Param("name") String name);

    @Modifying
    @Query("DELETE FROM AnswerOption ao WHERE ao.answerOptionId NOT IN " +
            "(SELECT DISTINCT mchao.answerOption.answerOptionId FROM MultipleChoiceHasAnswerOptionRelation mchao)")
    void deleteUnboundAnswerOptions();

    @Modifying
    @Query("DELETE FROM MultipleChoiceHasAnswerOptionRelation mchao WHERE mchao.mcAoRelationId = :relationId")
    void deleteMultipleChoiceAnswerOptionsRelation(@Param("relationId") Long relationId);

    @Modifying
    @Query("DELETE FROM MultipleChoiceHasAnswerOptionRelation mchao WHERE mchao.multipleChoiceQuestion.multipleChoiceId = :multipleChoiceId AND mchao.answerOption.answerOptionId = :answerId")
    void deleteRelationByIds(@Param("multipleChoiceId") Long multipleChoiceId, @Param("answerId") Long answerId);

    @Modifying
    @Query(value = "INSERT INTO multiple_choice_has_answer_option (multiple_choice_id, answer_option_id) VALUES (:multipleChoiceId, :answerId)", nativeQuery = true)
    void createMultipleChoiceAnswerOptionsRelation(@Param("multipleChoiceId") Long multipleChoiceId, @Param("answerId") Long answerId);
}
