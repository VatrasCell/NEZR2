package de.vatrascell.nezr.model;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.flag.FlagList;
import de.vatrascell.nezr.flag.Number;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Question implements Comparable<Question>, Comparator<Question> {

    public static final String QUESTION = "question";
    public static final String CATEGORY = "category";
    public static final String POSITION = "position";
    public static final String QUESTION_TYPE = "questionType";
    public static String EDIT = "edit";
    public static String DELETE = "delete";

    private String question;
    private Integer questionId;
    private QuestionType questionType;
    private Category category;
    private String date;
    private FlagList flags;
    private int position;
    private Headline headline;
    private SubmittedAnswer submittedAnswer = new SubmittedAnswer();
    private List<AnswerOption> answerOptions = new ArrayList<>();
    private Label questionLabel;
    private Pane scene;
    private List<CheckBox> answerCheckBoxes = new ArrayList<>();
    private TextField answerTextField;
    private ListView<AnswerOption> answerOptionListView;
    private TextArea answerTextArea;
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
        this.questionType = QuestionType.MULTIPLE_CHOICE;
        this.position = ++size;
        this.flags = new FlagList();
    }

    @Override
    public int compareTo(Question o) {
        return Integer.compare(this.getPosition(), o.getPosition());
    }

    @Override
    public int compare(Question o1, Question o2) {
        return o1.compareTo(o2);
    }

    @Override
    public String toString() {
        return question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question1 = (Question) o;
        return getPosition() == question1.getPosition() &&
                getQuestionnaireId() == question1.getQuestionnaireId() &&
                Objects.equals(getQuestion(), question1.getQuestion()) &&
                Objects.equals(getQuestionId(), question1.getQuestionId()) &&
                getQuestionType() == question1.getQuestionType() &&
                Objects.equals(getCategory(), question1.getCategory()) &&
                Objects.equals(getDate(), question1.getDate()) &&
                Objects.equals(getFlags(), question1.getFlags()) &&
                Objects.equals(getHeadline(), question1.getHeadline()) &&
                Objects.equals(getSubmittedAnswer(), question1.getSubmittedAnswer()) &&
                Objects.equals(getAnswerOptions(), question1.getAnswerOptions()) &&
                Objects.equals(getQuestionLabel(), question1.getQuestionLabel()) &&
                Objects.equals(getScene(), question1.getScene()) &&
                Objects.equals(getAnswerCheckBoxes(), question1.getAnswerCheckBoxes()) &&
                Objects.equals(getAnswerTextField(), question1.getAnswerTextField()) &&
                Objects.equals(getAnswerOptionListView(), question1.getAnswerOptionListView()) &&
                Objects.equals(getAnswerTextArea(), question1.getAnswerTextArea()) &&
                Objects.equals(getTarget(), question1.getTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestion(), getQuestionId(), getQuestionType(), getCategory(),
                getDate(), getFlags(), getPosition(), getHeadline(), getSubmittedAnswer(),
                getAnswerOptions(), getQuestionLabel(), getScene(), getAnswerCheckBoxes(),
                getAnswerTextField(), getAnswerOptionListView(), getAnswerTextArea(),
                getTarget(), getQuestionnaireId());
    }

    public String toDebugString() {
        return "Question [de.vatrascell.nezr.question=" + question + ", type=" + questionType + ", category=" + category + ", flags=" + flags
                + ", Position=" + position + ", headline=" + headline + ", answerOptions="
                + answerOptions + "]";
    }

    public void setListener(Number number) {

        answerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield changed from " + oldValue + " to " + newValue);
            if (answerTextField.getText().equals("")) {
                GlobalVars.everythingIsAwesome = true;
            } else {
                int i = number.getDigits();
                switch (number.getOperator()) {
                    case EQ:
                        try {
                            Integer.parseInt(answerTextField.getText());
                            GlobalVars.everythingIsAwesome = answerTextField.getText().length() == i;
                        } catch (NumberFormatException e) {
                            GlobalVars.everythingIsAwesome = false;
                        }
                        break;
                    case LTE:
                        try {
                            Integer.parseInt(answerTextField.getText());
                            GlobalVars.everythingIsAwesome = answerTextField.getText().length() <= i;
                        } catch (NumberFormatException e) {
                            GlobalVars.everythingIsAwesome = false;
                        }
                        break;
                    case GTE:
                        try {
                            Integer.parseInt(answerTextField.getText());
                            GlobalVars.everythingIsAwesome = answerTextField.getText().length() >= i;
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
						int i = Integer.parseInt(type.substring(5));
						String op = type.substring(3, 5);
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

    /**
     * Setzt dynamisch ChangeListener
     *
     * @param index int: Position im ArrayList
     * @param type  String: Fragentype
     */
    public void setListener(int index, QuestionType type) {
        if (type.equals(QuestionType.MULTIPLE_CHOICE)) {
            CheckBox checkbox = target.answerCheckBoxes.get(index);
            checkbox.selectedProperty().addListener((ov, old_val, new_val) -> {
                if (checkbox.isSelected()) {
                    scene.lookup("#lblFrage_" + getQuestionId()).setVisible(true);

                    answerTextField.setVisible(true);
                    answerCheckBoxes.forEach(checkBox -> checkBox.setVisible(true));
                    answerOptionListView.setVisible(true);

                    if (getFlags().isEvaluationQuestion()) {
                        scene.lookup("#lblNull").setVisible(true);
                        scene.lookup("#lblEins").setVisible(true);
                        scene.lookup("#lblZehn").setVisible(true);
                    }
                } else {
                    scene.lookup("#lblFrage_" + getQuestionId()).setVisible(false);

                    answerTextField.setVisible(false);
                    answerCheckBoxes.forEach(checkBox -> checkBox.setVisible(false));
                    answerOptionListView.setVisible(false);

                    if (getFlags().isEvaluationQuestion()) {
                        scene.lookup("#lblNull").setVisible(false);
                        scene.lookup("#lblEins").setVisible(false);
                        scene.lookup("#lblZehn").setVisible(false);
                    }
                }
            });
        } else if (type.equals(QuestionType.SHORT_ANSWER)) {

            TextField targetTextField = target.answerTextField;
            targetTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                // System.out.println("textfield changed from " + oldValue + " to " + newValue);
                if (targetTextField.getText().equals("")) {
                    scene.lookup("#lblFrage_" + getQuestionId()).setVisible(true);

                    answerTextField.setVisible(true);
                    answerCheckBoxes.forEach(checkBox -> checkBox.setVisible(true));
                    answerOptionListView.setVisible(true);

                } else {
                    // System.out.println(getFrageID() + " " + target.getFrageID());
                    scene.lookup("#lblFrage_" + getQuestionId()).setVisible(false);

                    answerTextField.setVisible(false);
                    answerCheckBoxes.forEach(checkBox -> checkBox.setVisible(false));
                    answerOptionListView.setVisible(false);
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
						questionLabel.setVisible(true);
						
						for(int i = 0; i < answerenFF.size(); i++) {
							answerenFF.get(i).setVisible(true);
						}
						for(int i = 0; i < answerenMC.size(); i++) {
							answerenMC.get(i).setVisible(true);
						}
						for(int i = 0; i < answerenLIST.size(); i++) {
							answerenLIST.get(i).setVisible(true);
						}
					} else {
						questionLabel.setVisible(false);
						
						for(int i = 0; i < answerenFF.size(); i++) {
							answerenFF.get(i).setVisible(false);
						}
						for(int i = 0; i < answerenMC.size(); i++) {
							answerenMC.get(i).setVisible(false);
						}
						for(int i = 0; i < answerenLIST.size(); i++) {
							answerenLIST.get(i).setVisible(false);
						}
					}
				}
			});
			*/
        }
    }
}
