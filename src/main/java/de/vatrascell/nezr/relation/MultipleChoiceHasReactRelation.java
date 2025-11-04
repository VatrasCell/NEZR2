package de.vatrascell.nezr.relation;

import de.vatrascell.nezr.react.React;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "multiple_choice_has_react")
public class MultipleChoiceHasReactRelation {

    @Id
    private long mcReactRelationId;
    @ManyToOne
    @JoinColumn(name = "react_id")
    private React react;
    @ManyToOne
    @JoinColumn(name = "q_mc_relation_id")
    private QuestionnaireHasMultipleChoiceRelation questionnaireHasMultipleChoiceRelation;
}
