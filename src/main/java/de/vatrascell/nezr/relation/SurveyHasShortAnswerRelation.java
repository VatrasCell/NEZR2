package de.vatrascell.nezr.relation;

import de.vatrascell.nezr.question.ShortAnswerQuestion;
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
@Table(name = "survey_has_short_answer")
public class SurveyHasShortAnswerRelation {

    @Id
    private long sSaRelationId;
    private int position;
    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;
    @ManyToOne
    @JoinColumn(name = "short_answer_id")
    private ShortAnswerQuestion shortAnswerQuestion;
}
