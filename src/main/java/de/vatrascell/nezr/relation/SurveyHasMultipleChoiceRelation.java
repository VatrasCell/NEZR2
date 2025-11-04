package de.vatrascell.nezr.relation;

import de.vatrascell.nezr.question.MultipleChoiceQuestion;
import de.vatrascell.nezr.survey.Survey;
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
@Table(name = "survey_has_multiple_choice")
public class SurveyHasMultipleChoiceRelation {

    @Id
    private long sMcRelationId;
    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;
    @ManyToOne
    @JoinColumn(name = "multiple_choice_id")
    private MultipleChoiceQuestion multipleChoiceQuestion;
}
