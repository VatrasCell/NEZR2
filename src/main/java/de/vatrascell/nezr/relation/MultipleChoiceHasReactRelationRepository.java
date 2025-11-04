package de.vatrascell.nezr.relation;

import de.vatrascell.nezr.react.React;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceHasReactRelationRepository extends JpaRepository<MultipleChoiceHasReactRelation, Long> {

    @Query("SELECT mchr.react FROM MultipleChoiceHasReactRelation mchr WHERE mchr.questionnaireHasMultipleChoiceRelation.qMcRelationId = :relationId")
    List<React> findReactsByRelationId(@Param("relationId") long relationId);
}