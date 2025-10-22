package de.vatrascell.nezr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerOption {

    public static final String ID = "id";
    public static final String NAME = "name";

    private Integer answerOptionId;
    private String name;

    public AnswerOption(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
