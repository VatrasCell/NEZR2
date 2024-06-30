package de.vatrascell.nezr.multipleChoiceQuestion;

import de.vatrascell.nezr.category.Category;
import de.vatrascell.nezr.headline.Headline;
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
@Table(name = "multiple_choice")
public class MultipleChoiceQuestion {

    @Id
    private long multipleChoiceId;
    private String question;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "headline_id")
    private Headline headline;

}
