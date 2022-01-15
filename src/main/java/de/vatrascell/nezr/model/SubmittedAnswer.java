package de.vatrascell.nezr.model;

import java.util.ArrayList;
import java.util.List;

public class SubmittedAnswer {

    private List<AnswerOption> submittedAnswerOptions = new ArrayList<>();
    private String submittedAnswerText;

    public SubmittedAnswer() {
    }

    public SubmittedAnswer(List<AnswerOption> submittedAnswerOptions) {
        this.setSubmittedAnswerOptions(submittedAnswerOptions);
    }

    public SubmittedAnswer(String submittedAnswerText) {
        this.setSubmittedAnswerText(submittedAnswerText);
    }

    public List<AnswerOption> getSubmittedAnswerOptions() {
        return submittedAnswerOptions;
    }

    public void setSubmittedAnswerOptions(List<AnswerOption> submittedAnswerOptions) {
        this.submittedAnswerOptions = submittedAnswerOptions;
    }

    public void addSubmittedAnswerOption(AnswerOption submittedAnswerOption) {
        this.submittedAnswerOptions.add(submittedAnswerOption);
    }

    public void addSubmittedAnswerOptions(List<AnswerOption> submittedAnswerOptions) {
        this.submittedAnswerOptions.addAll(submittedAnswerOptions);
    }

    public String getSubmittedAnswerText() {
        return submittedAnswerText;
    }

    public void setSubmittedAnswerText(String submittedAnswerText) {
        if (submittedAnswerText != null) {
            this.submittedAnswerText = submittedAnswerText.replaceAll("<.*>", "").trim();
        } else {
            this.submittedAnswerText = null;
        }

    }
}
