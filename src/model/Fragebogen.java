package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;

public class Fragebogen {
	private int id;
	private String date;
	private String name;
	private String ort;
	private BooleanProperty activ = new SimpleBooleanProperty();
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
	 * @return the activ
	 */
	public ObservableBooleanValue isActiv() {
		return activ;
	}
	/**
	 * @param activ the activ to set
	 */
	public void setActiv(boolean activ) {
		this.activ.set(activ);
	}
	public ObservableBooleanValue isFinal() {
		return isFinal;
	}
	public void setFinal(boolean isFinal) {
		this.isFinal.set(isFinal);
	}
	@Override
	public String toString() {
		return "Fragebogen [id=" + id + ", date=" + date + ", name=" + name + ", ort=" + ort + ", activ=" + activ
				+ ", isFinal=" + isFinal + "]";
	}
	
}
