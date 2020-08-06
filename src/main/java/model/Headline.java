package model;

/**
 * @author Julian
 * @version 1.0.0
 */
public class Headline {
	private int position;
	private String headline;
	
	
	
	/**
	 * @param position the position
	 * @param headline the headline
	 */
	public Headline(int position, String headline) {
		super();
		this.position = position;
		this.headline = headline;
	}
	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	/**
	 * @return the headline
	 */
	public String getHeadline() {
		return headline;
	}
	/**
	 * @param headline the headline to set
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
}
