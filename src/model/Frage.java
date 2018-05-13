package model;

import java.util.Comparator;
import java.util.Vector;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Frage implements Comparable<Frage>, Comparator<Frage> {
	private String frage;
	private int frageID;
	private String art;
	private String kategorie;
	private String datum;
	private String flags;
	private int Position;
	private String ueberschrift = "";
	private Vector<String> antwort_moeglichkeit = new Vector<String>();
	private Vector<String> antwort = new Vector<String>();
	// private JLabel frageLabel;
	private Vector<CheckBox> antwortenMC = new Vector<>();
	private Vector<TextField> antwortenFF = new Vector<>();
	private Vector<ScrollPane> antwortenLIST = new Vector<>();
	private Vector<TextArea> antwortenTEXT = new Vector<>();
	private Frage target;
	private int fragebogenID;
	/**
	 * @return the frage
	 */
	public String getFrage() {
		return frage;
	}
	/**
	 * @param frage the frage to set
	 */
	public void setFrage(String frage) {
		this.frage = frage;
	}
	/**
	 * @return the frageID
	 */
	public int getFrageID() {
		return frageID;
	}
	/**
	 * @param frageID the frageID to set
	 */
	public void setFrageID(int frageID) {
		this.frageID = frageID;
	}
	/**
	 * @return the art
	 */
	public String getArt() {
		return art;
	}
	/**
	 * @param art the art to set
	 */
	public void setArt(String art) {
		this.art = art;
	}
	/**
	 * @return the kategorie
	 */
	public String getKategorie() {
		return kategorie;
	}
	/**
	 * @param kategorie the kategorie to set
	 */
	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}
	/**
	 * @return the datum
	 */
	public String getDatum() {
		return datum;
	}
	/**
	 * @param datum the datum to set
	 */
	public void setDatum(String datum) {
		this.datum = datum;
	}
	/**
	 * @return the flags
	 */
	public String getFlags() {
		return flags;
	}
	/**
	 * @param flags the flags to set
	 */
	public void setFlags(String flags) {
		this.flags = flags;
	}
	/**
	 * @return the position
	 */
	public int getPosition() {
		return Position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		Position = position;
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
	/**
	 * @return the antwort_moeglichkeit
	 */
	public Vector<String> getAntwort_moeglichkeit() {
		return antwort_moeglichkeit;
	}
	/**
	 * @param antwort_moeglichkeit the antwort_moeglichkeit to set
	 */
	public void setAntwort_moeglichkeit(Vector<String> antwort_moeglichkeit) {
		this.antwort_moeglichkeit = antwort_moeglichkeit;
	}
	
	public void addAntwort_moeglichkeit(String antwort_moeglichkeit) {
		this.antwort_moeglichkeit.add(antwort_moeglichkeit);
	}
	/**
	 * @return the antwort
	 */
	public Vector<String> getAntwort() {
		return antwort;
	}
	/**
	 * @param antwort the antwort to set
	 */
	public void setAntwort(Vector<String> antwort) {
		this.antwort = antwort;
	}
	/**
	 * @return the antwortenMC
	 */
	public Vector<CheckBox> getAntwortenMC() {
		return antwortenMC;
	}
	/**
	 * @param antwortenMC the antwortenMC to set
	 */
	public void setAntwortenMC(Vector<CheckBox> antwortenMC) {
		this.antwortenMC = antwortenMC;
	}
	/**
	 * @return the antwortenFF
	 */
	public Vector<TextField> getAntwortenFF() {
		return antwortenFF;
	}
	/**
	 * @param antwortenFF the antwortenFF to set
	 */
	public void setAntwortenFF(Vector<TextField> antwortenFF) {
		this.antwortenFF = antwortenFF;
	}
	/**
	 * @return the antwortenLIST
	 */
	public Vector<ScrollPane> getAntwortenLIST() {
		return antwortenLIST;
	}
	/**
	 * @param antwortenLIST the antwortenLIST to set
	 */
	public void setAntwortenLIST(Vector<ScrollPane> antwortenLIST) {
		this.antwortenLIST = antwortenLIST;
	}
	/**
	 * @return the antwortenTEXT
	 */
	public Vector<TextArea> getAntwortenTEXT() {
		return antwortenTEXT;
	}
	/**
	 * @param antwortenTEXT the antwortenTEXT to set
	 */
	public void setAntwortenTEXT(Vector<TextArea> antwortenTEXT) {
		this.antwortenTEXT = antwortenTEXT;
	}
	/**
	 * @return the target
	 */
	public Frage getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(Frage target) {
		this.target = target;
	}
	/**
	 * @return the fragebogenID
	 */
	public int getFragebogenID() {
		return fragebogenID;
	}
	/**
	 * @param fragebogenID the fragebogenID to set
	 */
	public void setFragebogenID(int fragebogenID) {
		this.fragebogenID = fragebogenID;
	}
	
	@Override
	public int compareTo(Frage o) {

		if(this.getPosition() < o.getPosition()) {
			return -1;
		} else if (this.getPosition() > o.getPosition()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public int compare(Frage o1, Frage o2) {
		return o1.compareTo(o2);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Frage [frage=" + frage + ", art=" + art + ", kategorie=" + kategorie + ", flags=" + flags
				+ ", Position=" + Position + ", ueberschrift=" + ueberschrift + ", antwort_moeglichkeit="
				+ antwort_moeglichkeit + ", target=" + target + "]";
	}		
}
