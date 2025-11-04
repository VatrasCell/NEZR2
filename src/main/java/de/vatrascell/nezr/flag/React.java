package de.vatrascell.nezr.flag;

import de.vatrascell.nezr.model.QuestionType;

import java.util.Objects;

public class React {
    private long id;
    private final String ANSWER = "A";
    private QuestionType questionType;
    private long questionId;
    private int answerPosition;

    @Default
    public React(long id, Long shortAnswerId, Long multipleChoiceId, int answerPosition) {
        super();
        this.id = id;
        if (shortAnswerId != null) {
            this.questionId = shortAnswerId;
            this.questionType = QuestionType.SHORT_ANSWER;
        } else {
            this.questionId = multipleChoiceId;
            this.questionType = QuestionType.MULTIPLE_CHOICE;
        }
        this.answerPosition = answerPosition;
    }

    public React(QuestionType questionType, long questionId, int answerPosition) {
        super();
        this.questionType = questionType;
        this.questionId = questionId;
        this.answerPosition = answerPosition;
    }

    public React(long id, QuestionType questionType, long questionId, int answerPosition) {
        super();
        this.id = id;
        this.questionType = questionType;
        this.questionId = questionId;
        this.answerPosition = answerPosition;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerPosition() {
        return answerPosition;
    }

    public void setAnswerPosition(int answerPosition) {
        this.answerPosition = answerPosition;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return questionType.getQuestionType() + questionId + ANSWER + answerPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        React react = (React) o;
        return getQuestionId() == react.getQuestionId() &&
                getAnswerPosition() == react.getAnswerPosition() &&
                getQuestionType() == react.getQuestionType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(ANSWER, getQuestionType(), getQuestionId(), getAnswerPosition());
    }
}
