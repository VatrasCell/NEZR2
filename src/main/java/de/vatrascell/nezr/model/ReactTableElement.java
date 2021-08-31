package de.vatrascell.nezr.model;

import de.vatrascell.nezr.flag.React;

public class ReactTableElement {
    private String question;
    private String answer;
    private String comment;
    private React flag;

    private final String REACT_DESCRIPTION = "...die Antwortmöglichkeit \"%s\" der Frage \"%s\" ausgewählt wurde.";

    public ReactTableElement(Question question, int answerPos, React flag) {
        super();
        this.question = question.getQuestion();
        this.answer = question.getAnswerOptions().get(answerPos).getValue();
        this.comment = String.format(REACT_DESCRIPTION, answer, this.question);
        this.flag = flag;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public React getFlag() {
        return flag;
    }

    public void setFlag(React flag) {
        this.flag = flag;
    }

}
