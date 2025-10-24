package de.vatrascell.nezr.answerOption;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
