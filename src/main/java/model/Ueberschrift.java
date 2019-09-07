package model;

/**
 * @author Julian
 * @version 1.0.0
 */
public class Ueberschrift {
	private int postition;
	private String ueberschrift;
	
	
	
	/**
	 * @param postition
	 * @param ueberschrift
	 */
	public Ueberschrift(int postition, String ueberschrift) {
		super();
		this.postition = postition;
		this.ueberschrift = ueberschrift;
	}
	/**
	 * @return the postition
	 */
	public int getPostition() {
		return postition;
	}
	/**
	 * @param postition the postition to set
	 */
	public void setPostition(int postition) {
		this.postition = postition;
	}
	/**
	 * @return the ueberschrift
	 */
	public String getUeberschrift() {
		return ueberschrift;
	}
	/**
	 * @param ueberschrift the ueberschrift to set
	 */
	public void setUeberschrift(String ueberschrift) {
		this.ueberschrift = ueberschrift;
	}
	
}
