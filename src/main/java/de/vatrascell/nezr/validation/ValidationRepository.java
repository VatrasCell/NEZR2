package de.vatrascell.nezr.validation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {

    @Query("SELECT v FROM Validation v WHERE v.validationId = " +
            "(SELECT qhsa.validation.validationId " +
            "FROM QuestionnaireHasShortAnswerRelation qhsa " +
            "WHERE qhsa.qSaRelationId = :relationId)")
    Optional<Validation> findByShortAnswerRelationId(@Param("relationId") int relationId);
}
