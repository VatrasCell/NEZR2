package de.vatrascell.nezr.relation;

import de.vatrascell.nezr.react.React;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortAnswerHasReactRelationRepository extends JpaRepository<ShortAnswerHasReactRelation, Long> {

    @Query("SELECT sahr.react FROM ShortAnswerHasReactRelation sahr WHERE sahr.questionnaireHasShortAnswerRelation.qSaRelationId = :relationId")
    List<React> findReactsByRelationId(@Param("relationId") long relationId);
}