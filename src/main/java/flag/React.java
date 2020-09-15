package flag;

import model.QuestionType;

public class React extends Flag {
	private final String ANSWER = "A";
	private QuestionType questionType;
	private int questionId;
	private int answerPos;
	
	/**
	 * @param questionType
	 * @param questionId
	 * @param answerPos
	 */
	public React(QuestionType questionType, int questionId, int answerPos) {
		super();
		this.questionType = questionType;
		this.questionId = questionId;
		this.answerPos = answerPos;
	}

	/**
	 * @return the questionType
	 */
	public QuestionType getQuestionType() {
		return questionType;
	}
	
	/**
	 * @param questionType the questionType to set
	 */
	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}
	
	/**
	 * @return the questionId
	 */
	public int getQuestionId() {
		return questionId;
	}
	
	/**
	 * @param questionId the questionId to set
	 */
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	
	/**
	 * @return the answerId
	 */
	public int getAnswerPos() {
		return answerPos;
	}
	
	/**
	 * @param answerPos the answerId to set
	 */
	public void setAnswerPos(int answerPos) {
		this.answerPos = answerPos;
	}
	
	@Override
	public String toString() {
		return questionType.toString() + questionId + ANSWER + answerPos;
	}
	
	
}
