package de.vatrascell.nezr.flag;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
}
