package flag;

import model.QuestionType;

import java.util.Objects;

public class React extends Flag {
	private final String ANSWER = "A";
	private QuestionType questionType;
	private int questionId;
	private int answerPos;

	public React(QuestionType questionType, int questionId, int answerPos) {
		super();
		this.questionType = questionType;
		this.questionId = questionId;
		this.answerPos = answerPos;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getAnswerPos() {
		return answerPos;
	}

	public void setAnswerPos(int answerPos) {
		this.answerPos = answerPos;
	}
	
	@Override
	public String toString() {
		return questionType.getQuestionType() + questionId + ANSWER + answerPos;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		React react = (React) o;
		return getQuestionId() == react.getQuestionId() &&
				getAnswerPos() == react.getAnswerPos() &&
				getQuestionType() == react.getQuestionType();
	}

	@Override
	public int hashCode() {
		return Objects.hash(ANSWER, getQuestionType(), getQuestionId(), getAnswerPos());
	}
}
