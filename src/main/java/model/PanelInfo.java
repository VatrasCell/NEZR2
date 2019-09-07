package model;

public class PanelInfo {
	private boolean headline;
	private boolean bHeadlines;
	private Frage frage;
	
	/**
	 * 
	 */
	public PanelInfo() {
		headline = false;
		bHeadlines = false;
		frage = null;
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
	public Frage getFrage() {
		return frage;
	}

	/**
	 * @param frage the frage to set
	 */
	public void setFrage(Frage frage) {
		this.frage = frage;
	}
	
}
