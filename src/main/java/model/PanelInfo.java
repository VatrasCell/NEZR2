package model;

public class PanelInfo {
	private boolean headline;
	private boolean bHeadlines;
	private Question question;
	
	/**
	 * 
	 */
	public PanelInfo() {
		headline = false;
		bHeadlines = false;
		question = null;
	}

	/**
	 * @return the headline
	 */
	public boolean hasHeadline() {
		return headline;
	}

	/**
	 * @param headline the headline to set
	 */
	public void setHeadline(boolean headline) {
		this.headline = headline;
	}

	/**
	 * @return the bHeadlines
	 */
	public boolean hasBHeadlines() {
		return bHeadlines;
	}

	/**
	 * @param bHeadlines the bHeadlines to set
	 */
	public void setbHeadlines(boolean bHeadlines) {
		this.bHeadlines = bHeadlines;
	}
	/**
	 * @return the frage
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * @param question the frage to set
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}
	
}
