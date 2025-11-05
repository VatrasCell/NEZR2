package de.vatrascell.nezr.question;

import de.vatrascell.nezr.admin.Questionnaire;
import de.vatrascell.nezr.category.Category;
import de.vatrascell.nezr.flag.FlagListShortAnswer;
import de.vatrascell.nezr.headline.Headline;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.react.React;
import de.vatrascell.nezr.validation.Validation;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "short_answer")
public class ShortAnswerQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shortAnswerId;

    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headline_id")
    private Headline headline;

    // Beziehung zu Questionnaires über die Zwischentabelle
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "questionnaire_has_short_answer",
            joinColumns = @JoinColumn(name = "short_answer_id"),
            inverseJoinColumns = @JoinColumn(name = "questionnaire_id")
    )
    private List<Questionnaire> questionnaires;

    // Zusätzliche Felder für die Abfrage-Ergebnisse
    @Transient
    private LocalDateTime creationDate;

    @Transient
    private int position;

    @Transient
    private QuestionType questionType = QuestionType.SHORT_ANSWER;

    @Transient
    private long questionnaireId;

    @Transient
    private long qSaRelationId;

    @Transient
    private FlagListShortAnswer flagListShortAnswer;

    @Transient
    @OneToOne(fetch = FetchType.LAZY)
    private Validation validation;

    @Transient
    private List<React> reacts;

    // Konstruktor für die Repository-Abfrage
    public ShortAnswerQuestion(String question, long shortAnswerId, LocalDateTime creationDate,
                               int position, Category category, Headline headline, long qSaRelationId,
                               FlagListShortAnswer flagListShortAnswer, Validation validation) {
        this.question = question;
        this.shortAnswerId = shortAnswerId;
        this.creationDate = creationDate;
        this.position = position;
        this.qSaRelationId = qSaRelationId;
        this.category = category;
        this.headline = headline;
        this.validation = validation;
        this.flagListShortAnswer = flagListShortAnswer;
    }
}
