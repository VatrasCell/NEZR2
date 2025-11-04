package de.vatrascell.nezr.react;

import de.vatrascell.nezr.question.MultipleChoiceQuestion;
import de.vatrascell.nezr.question.ShortAnswerQuestion;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "react")
public class React {

    @Id
    private long reactId;
    @ManyToOne
    @JoinColumn(name = "short_answer_id")
    private ShortAnswerQuestion shortAnswerQuestion;
    @ManyToOne
    @JoinColumn(name = "multiple_choice_id")
    private MultipleChoiceQuestion multipleChoiceQuestion;
    private int answerPosition;
}
