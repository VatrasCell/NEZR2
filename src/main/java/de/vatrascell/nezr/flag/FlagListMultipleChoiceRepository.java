package de.vatrascell.nezr.flag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlagListMultipleChoiceRepository extends JpaRepository<FlagListMultipleChoice, Long> {

    @Query("SELECT f FROM FlagListMultipleChoice f WHERE f.qMcRelationId = :relationId")
    FlagListMultipleChoice findByRelationId(@Param("relationId") int relationId);

    @Modifying
    @Query("UPDATE FlagListMultipleChoice f SET f.isRequired = true WHERE f.qMcRelationId = :relationId")
    void setRequired(@Param("relationId") int relationId);

    @Modifying
    @Query("UPDATE FlagListMultipleChoice f SET f.isEvaluationQuestion = :isEvaluationQuestion, f.isRequired = :isRequired, f.isMultipleChoice = :isMultipleChoice, f.isList = :isList, f.isYesNoQuestion = :isYesNoQuestion, f.isSingleLine = :isSingleLine WHERE f.qMcRelationId = :relationId")
    void updateFlagList(@Param("relationId") int relationId, @Param("isEvaluationQuestion") boolean isEvaluationQuestion, @Param("isRequired") boolean isRequired, @Param("isMultipleChoice") boolean isMultipleChoice, @Param("isList") boolean isList, @Param("isYesNoQuestion") boolean isYesNoQuestion, @Param("isSingleLine") boolean isSingleLine);

    @Modifying
    @Query(value = "INSERT INTO flag_list_multiple_choice (q_mc_relation_id, is_evaluation_question, is_required, is_multiple_choice, is_list, is_yes_no_question, is_single_line) VALUES (:relationId, :isEvaluationQuestion, :isRequired, :isMultipleChoice, :isList, :isYesNoQuestion, :isSingleLine)", nativeQuery = true)
    void createFlagList(@Param("relationId") int relationId, @Param("isEvaluationQuestion") boolean isEvaluationQuestion, @Param("isRequired") boolean isRequired, @Param("isMultipleChoice") boolean isMultipleChoice, @Param("isList") boolean isList, @Param("isYesNoQuestion") boolean isYesNoQuestion, @Param("isSingleLine") boolean isSingleLine);
}
