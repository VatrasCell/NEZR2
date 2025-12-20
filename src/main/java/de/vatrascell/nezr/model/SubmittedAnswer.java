package de.vatrascell.nezr.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmittedAnswer {

    private List<AnswerOption> submittedAnswerOptions = new ArrayList<>();
    private StringProperty submittedAnswerText = new SimpleStringProperty();

    public void addSubmittedAnswerOption(AnswerOption answerOption) {
        submittedAnswerOptions.add(answerOption);
    }

    public void addSubmittedAnswerOptions(List<AnswerOption> submittedAnswerOptions) {
        this.submittedAnswerOptions.addAll(submittedAnswerOptions);
    }

    public void setSubmittedAnswerText(String submittedAnswerText) {
        this.submittedAnswerText.set(submittedAnswerText);
    }
}
