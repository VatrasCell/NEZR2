package de.vatrascell.nezr.question;

import de.vatrascell.nezr.admin.Questionnaire;
import de.vatrascell.nezr.answerOption.AnswerOption;
import de.vatrascell.nezr.category.Category;
import de.vatrascell.nezr.flag.FlagListMultipleChoice;
import de.vatrascell.nezr.headline.Headline;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.react.React;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "multiple_choice")
public class MultipleChoiceQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long multipleChoiceId;

    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headline_id")
    private Headline headline;

    // Beziehung zu AnswerOptions über die Zwischentabelle
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "multiple_choice_has_answer_option",
            joinColumns = @JoinColumn(name = "multiple_choice_id"),
            inverseJoinColumns = @JoinColumn(name = "answer_option_id")
    )
    private List<AnswerOption> answerOptions;

    // Beziehung zu Questionnaires über die Zwischentabelle
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "questionnaire_has_multiple_choice",
            joinColumns = @JoinColumn(name = "multiple_choice_id"),
            inverseJoinColumns = @JoinColumn(name = "questionnaire_id")
    )
    private List<Questionnaire> questionnaires;

    // Zusätzliche Felder für die Abfrage-Ergebnisse
    @Transient
    private LocalDateTime creationDate;

    @Transient
    private int position;

    @Transient
    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

    @Transient
    private long questionnaireId;

    @Transient
    private long qMcRelationId;

    @Transient
    private FlagListMultipleChoice flagListMultipleChoice;

    @Transient
    private List<React> reacts;

    // Konstruktor für die Repository-Abfrage
    public MultipleChoiceQuestion(String question, long multipleChoiceId, LocalDateTime creationDate,
                                  int position, Category category, Headline headline, long qMcRelationId,
                                  FlagListMultipleChoice flagListMultipleChoice) {
        this.question = question;
        this.multipleChoiceId = multipleChoiceId;
        this.creationDate = creationDate;
        this.position = position;
        this.qMcRelationId = qMcRelationId;
        this.category = category;
        this.headline = headline;
        this.flagListMultipleChoice = flagListMultipleChoice;
    }
}
