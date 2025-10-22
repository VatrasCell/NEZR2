package de.vatrascell.nezr.flag;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "flag_list_multiple_choice")
public class FlagListMultipleChoice {

    @Id
    private int qMcRelationId;
    private boolean isList;
    private boolean isMultipleChoice;
    private boolean isYesNoQuestion;
    private boolean isSingleLine;
    private boolean isRequired;
    private boolean isEvaluationQuestion;
}
