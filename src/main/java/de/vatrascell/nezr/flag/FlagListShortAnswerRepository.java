package de.vatrascell.nezr.flag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlagListShortAnswerRepository extends JpaRepository<FlagListShortAnswer, Long> {

    @Query("SELECT f FROM FlagListShortAnswer f WHERE f.qSaRelationId = :relationId")
    FlagListShortAnswer findByRelationId(@Param("relationId") int relationId);

    @Modifying
    @Query("UPDATE FlagListShortAnswer f SET f.isRequired = true WHERE f.qSaRelationId = :relationId")
    void setRequired(@Param("relationId") int relationId);

    @Modifying
    @Query("UPDATE FlagListShortAnswer f SET f.isRequired = :isRequired, f.isTextArea = :isTextArea WHERE f.qSaRelationId = :relationId")
    void updateFlagList(@Param("relationId") int relationId, @Param("isRequired") boolean isRequired, @Param("isTextArea") boolean isTextArea);

    @Modifying
    @Query(value = "INSERT INTO flag_list_short_answer (q_sa_relation_id, is_required, is_text_area) VALUES (:relationId, :isRequired, :isTextArea)", nativeQuery = true)
    void createFlagList(@Param("relationId") int relationId, @Param("isRequired") boolean isRequired, @Param("isTextArea") boolean isTextArea);
}
