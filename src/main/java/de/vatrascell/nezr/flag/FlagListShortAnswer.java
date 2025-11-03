package de.vatrascell.nezr.flag;

import de.vatrascell.nezr.validation.Validation;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "flag_list_short_answer")
public class FlagListShortAnswer {

    @Id
    private int qSaRelationId;
    private boolean isRequired;
    private boolean isTextArea;
    @ManyToOne
    @JoinTable(
            name = "questionnaire_has_short_answer",
            joinColumns = @JoinColumn(name = "q_sa_relation_id"),
            inverseJoinColumns = @JoinColumn(name = "validation_id")
    )
    private Validation validation;
}
