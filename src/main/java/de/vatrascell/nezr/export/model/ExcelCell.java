package de.vatrascell.nezr.export.model;

import java.util.ArrayList;

/**
 * @author Eric
 * @version 1.0.0
 */
public class ExcelCell {
	private int surveyId;
	private ArrayList<String> answers;

	public ExcelCell() {
	}

	public ExcelCell(int surveyId, ArrayList<String> answers) {
		this.surveyId = surveyId;
		this.answers = answers;
	}

	public int getSurveyId() {
		return this.surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	public ArrayList<String> getAnswers() {
		return this.answers;
	}

	public void setAnswers(ArrayList<String> answers) {
		this.answers = answers;
	}
}
