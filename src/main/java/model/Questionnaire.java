package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;

public class Questionnaire {
	private int id;
	private String date;
	private String name;
	private String ort;
	private BooleanProperty isActive = new SimpleBooleanProperty();
	private BooleanProperty isFinal = new SimpleBooleanProperty();
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the ort
	 */
	public String getOrt() {
		return ort;
	}
	/**
	 * @param ort the ort to set
	 */
	public void setOrt(String ort) {
		this.ort = ort;
	}
	/**
	 * @return the active
	 */
	public ObservableBooleanValue isActive() {
		return isActive;
	}
	/**
	 * @param isActive the active to set
	 */
	public void setActive(boolean isActive) {
		this.isActive.set(isActive);
	}

	public ObservableBooleanValue isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal.set(isFinal);
	}

	@Override
	public String toString() {
		return "Questionnaire [id=" + id + ", date=" + date + ", name=" + name + ", ort=" + ort + ", activ=" + isActive
				+ ", isFinal=" + isFinal + "]";
	}
	
}
