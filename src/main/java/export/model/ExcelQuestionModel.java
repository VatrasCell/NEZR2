package export.model;

import model.Question;
import model.SubmittedAnswer;

import java.util.List;

public class ExcelQuestionModel {

    private Question question;

    private List<String> answerOptions;

    private int fistCellPosition;
    private int lastCellPosition;

    public String getCategory() {
        return question.getCategory().getName();
    }

    public String getQuestionValue() {
        return question.getQuestion();
    }

    public void setSubmittedAnswer(SubmittedAnswer submittedAnswer) {
        this.question.setSubmittedAnswer(submittedAnswer);
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<String> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<String> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public int getFistCellPosition() {
        return fistCellPosition;
    }

    public void setFistCellPosition(int fistCellPosition) {
        this.fistCellPosition = fistCellPosition;
    }

    public int getLastCellPosition() {
        return lastCellPosition;
    }

    public void setLastCellPosition(int lastCellPosition) {
        this.lastCellPosition = lastCellPosition;
    }

    public boolean isMergeCell() {
        return (lastCellPosition - fistCellPosition > 0);
    }
}
