package de.vatrascell.nezr.relation;

import de.vatrascell.nezr.admin.Questionnaire;
import de.vatrascell.nezr.question.ShortAnswerQuestion;
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
@Table(name = "questionnaire_has_short_answer")
public class QuestionnaireHasShortAnswerRelation {

    @Id
    private long qSaRelationId;
    private int position;
    @ManyToOne
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;
    @ManyToOne
    @JoinColumn(name = "short_answer_id")
    private ShortAnswerQuestion shortAnswerQuestion;
}
