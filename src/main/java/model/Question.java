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

public class Question implements Comparable<Question>, Comparator<Question> {
	private String question;
	private int questionId;
	private String questionType; //art
	private String category;
	private String date;
	private FlagList flags;
	private int position;
	private String headline = "";
	private ArrayList<String> answerOptions = new ArrayList<>();
	private ArrayList<String> answer = new ArrayList<>();
	private Label questionLabel;
	private Pane scene;
	private ArrayList<CheckBox> answersMC = new ArrayList<>();
	private ArrayList<TextField> answersFF = new ArrayList<>();
	private ArrayList<ListView<String>> answersLIST = new ArrayList<>();
	private ArrayList<TextArea> answersTEXT = new ArrayList<>();
	private Question target;
	private int questionnaireId;
	
	
	/**
	 * 
	 */
	public Question() {
		super();
	}
	
	public Question(int size) {
		this.question = "";
		this.questionType = "MC";
		this.position = ++size;
		this.flags = new FlagList();
	}
	
	/**
	 * @return the frage
	 */
	public String getQuestion() {
		return question;
	}
	/**
	 * @param question the frage to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	/**
	 * @return the frageID
	 */
	public int getQuestionId() {
		return questionId;
	}
	/**
	 * @param questionId the frageID to set
	 */
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	/**
	 * @return the art
	 */
	public String getQuestionType() {
		return questionType;
	}
	/**
	 * @param questionType the art to set
	 */
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	/**
	 * @return the kategorie
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the kategorie to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the datum
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the datum to set
	 */
	public void setDate(String date) {
		this.date = date;
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
	public String getHeadline() {
		return headline;
	}
	/**
	 * @param headline the ueberschrift to set
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	/**
	 * @return the antwort_moeglichkeit
	 */
	public ArrayList<String> getAnswerOptions() {
		return answerOptions;
	}
	/**
	 * @param answerOptions the antwort_moeglichkeit to set
	 */
	public void setAnswerOptions(ArrayList<String> answerOptions) {
		this.answerOptions = answerOptions;
	}
	
	public void addAntwort_moeglichkeit(String antwort_moeglichkeit) {
		this.answerOptions.add(antwort_moeglichkeit);
	}
	/**
	 * @return the antwort
	 */
	public ArrayList<String> getAnswer() {
		return answer;
	}
	/**
	 * @param answer the antwort to set
	 */
	public void setAnswer(ArrayList<String> answer) {
		this.answer = answer;
	}
	/**
	 * @return the antwortenMC
	 */
	public ArrayList<CheckBox> getAnswersMC() {
		return answersMC;
	}
	/**
	 * @param answersMC the antwortenMC to set
	 */
	public void setAnswersMC(ArrayList<CheckBox> answersMC) {
		this.answersMC = answersMC;
	}
	/**
	 * @return the antwortenFF
	 */
	public ArrayList<TextField> getAnswersFF() {
		return answersFF;
	}
	/**
	 * @param answersFF the antwortenFF to set
	 */
	public void setAnswersFF(ArrayList<TextField> answersFF) {
		this.answersFF = answersFF;
	}
	/**
	 * @return the antwortenLIST
	 */
	public ArrayList<ListView<String>> getAnswersLIST() {
		return answersLIST;
	}
	/**
	 * @param answersLIST the antwortenLIST to set
	 */
	public void setAnswersLIST(ArrayList<ListView<String>> answersLIST) {
		this.answersLIST = answersLIST;
	}
	/**
	 * @return the antwortenTEXT
	 */
	public ArrayList<TextArea> getAnswersTEXT() {
		return answersTEXT;
	}
	/**
	 * @param answersTEXT the antwortenTEXT to set
	 */
	public void setAnswersTEXT(ArrayList<TextArea> answersTEXT) {
		this.answersTEXT = answersTEXT;
	}
	/**
	 * @return the target
	 */
	public Question getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(Question target) {
		this.target = target;
	}
	/**
	 * @return the fragebogenID
	 */
	public int getQuestionnaireId() {
		return questionnaireId;
	}
	/**
	 * @param questionnaireId the fragebogenID to set
	 */
	public void setQuestionnaireId(int questionnaireId) {
		this.questionnaireId = questionnaireId;
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
	public Label getQuestionLabel() {
		return questionLabel;
	}
	/**
	 * @param questionLabel the frageLabel to set
	 */
	public void setQuestionLabel(Label questionLabel) {
		this.questionLabel = questionLabel;
	}
	@Override
	public int compareTo(Question o) {

		if(this.getPosition() < o.getPosition()) {
			return -1;
		} else if (this.getPosition() > o.getPosition()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public int compare(Question o1, Question o2) {
		return o1.compareTo(o2);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return question;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + ((answerOptions == null) ? 0 : answerOptions.hashCode());
		result = prime * result + ((answersFF == null) ? 0 : answersFF.hashCode());
		result = prime * result + ((answersLIST == null) ? 0 : answersLIST.hashCode());
		result = prime * result + ((answersMC == null) ? 0 : answersMC.hashCode());
		result = prime * result + ((answersTEXT == null) ? 0 : answersTEXT.hashCode());
		result = prime * result + ((questionType == null) ? 0 : questionType.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((flags == null) ? 0 : flags.hashCode());
		result = prime * result + ((question == null) ? 0 : question.hashCode());
		result = prime * result + questionId;
		result = prime * result + ((questionLabel == null) ? 0 : questionLabel.hashCode());
		result = prime * result + questionnaireId;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + position;
		result = prime * result + ((scene == null) ? 0 : scene.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((headline == null) ? 0 : headline.hashCode());
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
		Question other = (Question) obj;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (answerOptions == null) {
			if (other.answerOptions != null)
				return false;
		} else if (!answerOptions.equals(other.answerOptions))
			return false;
		if (answersFF == null) {
			if (other.answersFF != null)
				return false;
		} else if (!answersFF.equals(other.answersFF))
			return false;
		if (answersLIST == null) {
			if (other.answersLIST != null)
				return false;
		} else if (!answersLIST.equals(other.answersLIST))
			return false;
		if (answersMC == null) {
			if (other.answersMC != null)
				return false;
		} else if (!answersMC.equals(other.answersMC))
			return false;
		if (answersTEXT == null) {
			if (other.answersTEXT != null)
				return false;
		} else if (!answersTEXT.equals(other.answersTEXT))
			return false;
		if (questionType == null) {
			if (other.questionType != null)
				return false;
		} else if (!questionType.equals(other.questionType))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (flags == null) {
			if (other.flags != null)
				return false;
		} else if (!flags.equals(other.flags))
			return false;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		if (questionId != other.questionId)
			return false;
		if (questionLabel == null) {
			if (other.questionLabel != null)
				return false;
		} else if (!questionLabel.equals(other.questionLabel))
			return false;
		if (questionnaireId != other.questionnaireId)
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
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
		if (headline == null) {
			if (other.headline != null)
				return false;
		} else if (!headline.equals(other.headline))
			return false;
		return true;
	}

	public String toDebugString() {
		return "Frage [frage=" + question + ", art=" + questionType + ", kategorie=" + category + ", flags=" + flags
				+ ", Position=" + position + ", ueberschrift=" + headline + ", antwort_moeglichkeit="
				+ answerOptions + "]";
	}	
	
	/**
	 * Setzt dynamisch ChangeListener
	 * @param index int: Position im ArrayList
	 * @param s String: Fragenart
	 */
	public void setListener(int index, String s) {
		if(s == "MC") {
			CheckBox checkbox = target.answersMC.get(index);
			checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
		        public void changed(ObservableValue<? extends Boolean> ov,
		                Boolean old_val, Boolean new_val) {
		        	if(checkbox.isSelected()) {
						scene.lookup("#lblFrage_" + getQuestionId()).setVisible(true);
						
						for(int i = 0; i < answersFF.size(); i++) {
							answersFF.get(i).setVisible(true);
						}
						for(int i = 0; i < answersMC.size(); i++) {
							answersMC.get(i).setVisible(true);
						}
						for(int i = 0; i < answersLIST.size(); i++) {
							answersLIST.get(i).setVisible(true);
						}
						if(getFlags().is(SymbolType.B)) {
							scene.lookup("#lblNull").setVisible(true);
							scene.lookup("#lblEins").setVisible(true);
							scene.lookup("#lblZehn").setVisible(true);
						}
					} else {
						scene.lookup("#lblFrage_" + getQuestionId()).setVisible(false);
						
						for(int i = 0; i < answersFF.size(); i++) {
							answersFF.get(i).setVisible(false);
						}
						for(int i = 0; i < answersMC.size(); i++) {
							answersMC.get(i).setVisible(false);
						}
						for(int i = 0; i < answersLIST.size(); i++) {
							answersLIST.get(i).setVisible(false);
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
			
			TextField textField = target.answersFF.get(index);
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
			    // System.out.println("textfield changed from " + oldValue + " to " + newValue);
			    if(textField.getText().equals("")) {
			    	scene.lookup("#lblFrage_" + getQuestionId()).setVisible(true);
					
					for(int i = 0; i < answersFF.size(); i++) {
						answersFF.get(i).setVisible(true);
					}
					for(int i = 0; i < answersMC.size(); i++) {
						answersMC.get(i).setVisible(true);
					}
					for(int i = 0; i < answersLIST.size(); i++) {
						answersLIST.get(i).setVisible(true);
					}
				} else {
					// System.out.println(getFrageID() + " " + target.getFrageID());
					scene.lookup("#lblFrage_" + getQuestionId()).setVisible(false);
					
					for(int i = 0; i < answersFF.size(); i++) {
						answersFF.get(i).setVisible(false);
					}
					for(int i = 0; i < answersMC.size(); i++) {
						answersMC.get(i).setVisible(false);
					}
					for(int i = 0; i < answersLIST.size(); i++) {
						answersLIST.get(i).setVisible(false);
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
			TextField textField = answersFF.get(0);
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
