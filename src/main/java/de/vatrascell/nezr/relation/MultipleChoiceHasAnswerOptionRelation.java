package de.vatrascell.nezr.relation;

import de.vatrascell.nezr.question.AnswerOption;
import de.vatrascell.nezr.question.MultipleChoiceQuestion;
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
@Table(name = "multiple_choice_has_answer_option")
public class MultipleChoiceHasAnswerOptionRelation {

    @Id
    private long mcAoRelationId;
    @ManyToOne
    @JoinColumn(name = "multiple_choice_id")
    private MultipleChoiceQuestion multipleChoiceQuestion;
    @ManyToOne
    @JoinColumn(name = "answer_option_id")
    private AnswerOption answerOption;
}
