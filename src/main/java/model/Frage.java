package model;

import application.GlobalVars;
import flag.FlagList;
import flag.SymbolType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Comparator;

public class Frage implements Comparable<Frage>, Comparator<Frage> {
	private String frage;
	private int frageID;
	private String art;
	private String kategorie;
	private String datum;
	private FlagList flags;
	private int position;
	private String ueberschrift = "";
	private ArrayList<String> antwort_moeglichkeit = new ArrayList<String>();
	private ArrayList<String> antwort = new ArrayList<String>();
	private Label frageLabel;
	private Pane scene;
	private ArrayList<CheckBox> antwortenMC = new ArrayList<>();
	private ArrayList<TextField> antwortenFF = new ArrayList<>();
	private ArrayList<ListView<String>> antwortenLIST = new ArrayList<>();
	private ArrayList<TextArea> antwortenTEXT = new ArrayList<>();
	private Frage target;
	private int fragebogenID;
	
	
	/**
	 * 
	 */
	public Frage() {
		super();
	}
	
	public Frage(int size) {
		this.frage = "";
		this.art = "MC";
		this.position = ++size;
		this.flags = new FlagList();
	}
	
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
	public FlagList getFlags() {
		return flags == null ? new FlagList() : flags;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags(FlagList flags) {
		this.flags = flags;
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
	public ArrayList<String> getAntwort_moeglichkeit() {
		return antwort_moeglichkeit;
	}
	/**
	 * @param antwort_moeglichkeit the antwort_moeglichkeit to set
	 */
	public void setAntwort_moeglichkeit(ArrayList<String> antwort_moeglichkeit) {
		this.antwort_moeglichkeit = antwort_moeglichkeit;
	}
	
	public void addAntwort_moeglichkeit(String antwort_moeglichkeit) {
		this.antwort_moeglichkeit.add(antwort_moeglichkeit);
	}
	/**
	 * @return the antwort
	 */
	public ArrayList<String> getAntwort() {
		return antwort;
	}
	/**
	 * @param antwort the antwort to set
	 */
	public void setAntwort(ArrayList<String> antwort) {
		this.antwort = antwort;
	}
	/**
	 * @return the antwortenMC
	 */
	public ArrayList<CheckBox> getAntwortenMC() {
		return antwortenMC;
	}
	/**
	 * @param antwortenMC the antwortenMC to set
	 */
	public void setAntwortenMC(ArrayList<CheckBox> antwortenMC) {
		this.antwortenMC = antwortenMC;
	}
	/**
	 * @return the antwortenFF
	 */
	public ArrayList<TextField> getAntwortenFF() {
		return antwortenFF;
	}
	/**
	 * @param antwortenFF the antwortenFF to set
	 */
	public void setAntwortenFF(ArrayList<TextField> antwortenFF) {
		this.antwortenFF = antwortenFF;
	}
	/**
	 * @return the antwortenLIST
	 */
	public ArrayList<ListView<String>> getAntwortenLIST() {
		return antwortenLIST;
	}
	/**
	 * @param antwortenLIST the antwortenLIST to set
	 */
	public void setAntwortenLIST(ArrayList<ListView<String>> antwortenLIST) {
		this.antwortenLIST = antwortenLIST;
	}
	/**
	 * @return the antwortenTEXT
	 */
	public ArrayList<TextArea> getAntwortenTEXT() {
		return antwortenTEXT;
	}
	/**
	 * @param antwortenTEXT the antwortenTEXT to set
	 */
	public void setAntwortenTEXT(ArrayList<TextArea> antwortenTEXT) {
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
	/**
	 * @return the scene
	 */
	public Pane getScene() {
		return scene;
	}
	/**
	 * @param scene the scene to set
	 */
	public void setScene(Pane scene) {
		this.scene = scene;
	}
	/**
	 * @return the frageLabel
	 */
	public Label getFrageLabel() {
		return frageLabel;
	}
	/**
	 * @param frageLabel the frageLabel to set
	 */
	public void setFrageLabel(Label frageLabel) {
		this.frageLabel = frageLabel;
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
		return frage;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((antwort == null) ? 0 : antwort.hashCode());
		result = prime * result + ((antwort_moeglichkeit == null) ? 0 : antwort_moeglichkeit.hashCode());
		result = prime * result + ((antwortenFF == null) ? 0 : antwortenFF.hashCode());
		result = prime * result + ((antwortenLIST == null) ? 0 : antwortenLIST.hashCode());
		result = prime * result + ((antwortenMC == null) ? 0 : antwortenMC.hashCode());
		result = prime * result + ((antwortenTEXT == null) ? 0 : antwortenTEXT.hashCode());
		result = prime * result + ((art == null) ? 0 : art.hashCode());
		result = prime * result + ((datum == null) ? 0 : datum.hashCode());
		result = prime * result + ((flags == null) ? 0 : flags.hashCode());
		result = prime * result + ((frage == null) ? 0 : frage.hashCode());
		result = prime * result + frageID;
		result = prime * result + ((frageLabel == null) ? 0 : frageLabel.hashCode());
		result = prime * result + fragebogenID;
		result = prime * result + ((kategorie == null) ? 0 : kategorie.hashCode());
		result = prime * result + position;
		result = prime * result + ((scene == null) ? 0 : scene.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((ueberschrift == null) ? 0 : ueberschrift.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Frage other = (Frage) obj;
		if (antwort == null) {
			if (other.antwort != null)
				return false;
		} else if (!antwort.equals(other.antwort))
			return false;
		if (antwort_moeglichkeit == null) {
			if (other.antwort_moeglichkeit != null)
				return false;
		} else if (!antwort_moeglichkeit.equals(other.antwort_moeglichkeit))
			return false;
		if (antwortenFF == null) {
			if (other.antwortenFF != null)
				return false;
		} else if (!antwortenFF.equals(other.antwortenFF))
			return false;
		if (antwortenLIST == null) {
			if (other.antwortenLIST != null)
				return false;
		} else if (!antwortenLIST.equals(other.antwortenLIST))
			return false;
		if (antwortenMC == null) {
			if (other.antwortenMC != null)
				return false;
		} else if (!antwortenMC.equals(other.antwortenMC))
			return false;
		if (antwortenTEXT == null) {
			if (other.antwortenTEXT != null)
				return false;
		} else if (!antwortenTEXT.equals(other.antwortenTEXT))
			return false;
		if (art == null) {
			if (other.art != null)
				return false;
		} else if (!art.equals(other.art))
			return false;
		if (datum == null) {
			if (other.datum != null)
				return false;
		} else if (!datum.equals(other.datum))
			return false;
		if (flags == null) {
			if (other.flags != null)
				return false;
		} else if (!flags.equals(other.flags))
			return false;
		if (frage == null) {
			if (other.frage != null)
				return false;
		} else if (!frage.equals(other.frage))
			return false;
		if (frageID != other.frageID)
			return false;
		if (frageLabel == null) {
			if (other.frageLabel != null)
				return false;
		} else if (!frageLabel.equals(other.frageLabel))
			return false;
		if (fragebogenID != other.fragebogenID)
			return false;
		if (kategorie == null) {
			if (other.kategorie != null)
				return false;
		} else if (!kategorie.equals(other.kategorie))
			return false;
		if (position != other.position)
			return false;
		if (scene == null) {
			if (other.scene != null)
				return false;
		} else if (!scene.equals(other.scene))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (ueberschrift == null) {
			if (other.ueberschrift != null)
				return false;
		} else if (!ueberschrift.equals(other.ueberschrift))
			return false;
		return true;
	}

	public String toDebugString() {
		return "Frage [frage=" + frage + ", art=" + art + ", kategorie=" + kategorie + ", flags=" + flags
				+ ", Position=" + position + ", ueberschrift=" + ueberschrift + ", antwort_moeglichkeit="
				+ antwort_moeglichkeit + "]";
	}	
	
	/**
	 * Setzt dynamisch ChangeListener
	 * @param index int: Position im ArrayList
	 * @param s String: Fragenart
	 */
	public void setListener(int index, String s) {
		if(s == "MC") {
			CheckBox checkbox = target.antwortenMC.get(index);
			checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
		        public void changed(ObservableValue<? extends Boolean> ov,
		                Boolean old_val, Boolean new_val) {
		        	if(checkbox.isSelected()) {
						scene.lookup("#lblFrage_" + getFrageID()).setVisible(true);
						
						for(int i = 0; i < antwortenFF.size(); i++) {
							antwortenFF.get(i).setVisible(true);
						}
						for(int i = 0; i < antwortenMC.size(); i++) {
							antwortenMC.get(i).setVisible(true);
						}
						for(int i = 0; i < antwortenLIST.size(); i++) {
							antwortenLIST.get(i).setVisible(true);
						}
						if(getFlags().is(SymbolType.B)) {
							scene.lookup("#lblNull").setVisible(true);
							scene.lookup("#lblEins").setVisible(true);
							scene.lookup("#lblZehn").setVisible(true);
						}
					} else {
						scene.lookup("#lblFrage_" + getFrageID()).setVisible(false);
						
						for(int i = 0; i < antwortenFF.size(); i++) {
							antwortenFF.get(i).setVisible(false);
						}
						for(int i = 0; i < antwortenMC.size(); i++) {
							antwortenMC.get(i).setVisible(false);
						}
						for(int i = 0; i < antwortenLIST.size(); i++) {
							antwortenLIST.get(i).setVisible(false);
						}
						if(getFlags().is(SymbolType.B)) {
							scene.lookup("#lblNull").setVisible(false);
							scene.lookup("#lblEins").setVisible(false);
							scene.lookup("#lblZehn").setVisible(false);
						}
					}
		            }
		        });
		} else if (s == "FF") {
			
			TextField textField = target.antwortenFF.get(index);
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
			    // System.out.println("textfield changed from " + oldValue + " to " + newValue);
			    if(textField.getText().equals("")) {
			    	scene.lookup("#lblFrage_" + getFrageID()).setVisible(true);
					
					for(int i = 0; i < antwortenFF.size(); i++) {
						antwortenFF.get(i).setVisible(true);
					}
					for(int i = 0; i < antwortenMC.size(); i++) {
						antwortenMC.get(i).setVisible(true);
					}
					for(int i = 0; i < antwortenLIST.size(); i++) {
						antwortenLIST.get(i).setVisible(true);
					}
				} else {
					// System.out.println(getFrageID() + " " + target.getFrageID());
					scene.lookup("#lblFrage_" + getFrageID()).setVisible(false);
					
					for(int i = 0; i < antwortenFF.size(); i++) {
						antwortenFF.get(i).setVisible(false);
					}
					for(int i = 0; i < antwortenMC.size(); i++) {
						antwortenMC.get(i).setVisible(false);
					}
					for(int i = 0; i < antwortenLIST.size(); i++) {
						antwortenLIST.get(i).setVisible(false);
					}
				}
			});
			/*
			textField.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					go();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					go();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					go();
				}
				
				private void go() {
					if(textField.getText().equals("")) {
						frageLabel.setVisible(true);
						
						for(int i = 0; i < antwortenFF.size(); i++) {
							antwortenFF.get(i).setVisible(true);
						}
						for(int i = 0; i < antwortenMC.size(); i++) {
							antwortenMC.get(i).setVisible(true);
						}
						for(int i = 0; i < antwortenLIST.size(); i++) {
							antwortenLIST.get(i).setVisible(true);
						}
					} else {
						frageLabel.setVisible(false);
						
						for(int i = 0; i < antwortenFF.size(); i++) {
							antwortenFF.get(i).setVisible(false);
						}
						for(int i = 0; i < antwortenMC.size(); i++) {
							antwortenMC.get(i).setVisible(false);
						}
						for(int i = 0; i < antwortenLIST.size(); i++) {
							antwortenLIST.get(i).setVisible(false);
						}
					}
				}
			});
			*/
		} else if (s.indexOf("INT") >= 0) {
			TextField textField = antwortenFF.get(0);
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
			    System.out.println("textfield changed from " + oldValue + " to " + newValue);
			    if(textField.getText().equals("")) {
					GlobalVars.everythingIsAwesome = true;
				} else {
					int i = Integer.parseInt(s.substring(5));
					String op = s.substring(3, 5);
					System.out.println(op);
					switch(op) {
					case "==":
						try {
							Integer.parseInt(textField.getText());
							if(textField.getText().length() == i) {
								GlobalVars.everythingIsAwesome = true;
							} else {
								GlobalVars.everythingIsAwesome = false;
							}
						} catch (NumberFormatException e) {
							GlobalVars.everythingIsAwesome = false;
						}
						break;
					case "<=":
						try {
							Integer.parseInt(textField.getText());
							if(textField.getText().length() <= i) {
								GlobalVars.everythingIsAwesome = true;
							} else {
								GlobalVars.everythingIsAwesome = false;
							}
						} catch (NumberFormatException e) {
							GlobalVars.everythingIsAwesome = false;
						}
						break;
					case ">=":
						try {
							Integer.parseInt(textField.getText());
							if(textField.getText().length() >= i) {
								GlobalVars.everythingIsAwesome = true;
							} else {
								GlobalVars.everythingIsAwesome = false;
							}
						} catch (NumberFormatException e) {
							GlobalVars.everythingIsAwesome = false;
						}
						break;
					}	
				}
			});
			/*
			textField.getDocument().addDocumentListener(new Numberlistener()  {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					go();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					go();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					go();
				}
				
				private void go() {
					if(textField.getText().equals("")) {
						Main.setEverythingIsAwesome(true);
					} else {
						int i = Integer.parseInt(s.substring(5));
						String op = s.substring(3, 5);
						System.out.println(op);
						switch(op) {
						case "==":
							try {
								Integer.parseInt(textField.getText());
								if(textField.getText().length() == i) {
									Main.setEverythingIsAwesome(true);
								} else {
									Main.setEverythingIsAwesome(false);
								}
							} catch (NumberFormatException e) {
								Main.setEverythingIsAwesome(false);
							}
							break;
						case "<=":
							try {
								Integer.parseInt(textField.getText());
								if(textField.getText().length() <= i) {
									Main.setEverythingIsAwesome(true);
								} else {
									Main.setEverythingIsAwesome(false);
								}
							} catch (NumberFormatException e) {
								Main.setEverythingIsAwesome(false);
							}
							break;
						case ">=":
							try {
								Integer.parseInt(textField.getText());
								if(textField.getText().length() >= i) {
									Main.setEverythingIsAwesome(true);
								} else {
									Main.setEverythingIsAwesome(false);
								}
							} catch (NumberFormatException e) {
								Main.setEverythingIsAwesome(false);
							}
							break;
						}	
					}
				}
			});*/
		}
	}	
}
