package de.vatrascell.nezr.question;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "answer_option")
public class AnswerOption {

    @Id
    private Integer answerOptionId;
    private String name;

    public AnswerOption(AnswerOption answerOption) {
        this.answerOptionId = answerOption.getAnswerOptionId();
        this.name = answerOption.getName();
    }
}
