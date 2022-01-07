package de.vatrascell.nezr.survey;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.gratitude.GratitudeController;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.model.SubmittedAnswer;
import de.vatrascell.nezr.question.QuestionController;
import de.vatrascell.nezr.start.StartController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;
import static de.vatrascell.nezr.application.controller.ScreenController.STYLESHEET;

@Component
//@FxmlView(SURVEY_PATH)
public class SurveyController {

    private static boolean isPreview;
    private static int pageCount;
    private static int pageNumber = 1;

    private final ScreenController screenController;

    @Autowired
    public SurveyController(ScreenController screenController) {
        this.screenController = screenController;
    }

    //@FXML
    //Pane pane;

    public static void setPageCount(int pageCount) {
        SurveyController.pageCount = pageCount;
    }

    /**
     * Initializes the controller class. This method is automatically called after
     * the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        String value;
        switch (GlobalVars.location) {
            case "R\u00FCgen":
                value = "images/img/logo_nezr.png";
                break;
            case "Bayerischer Wald":
                value = "images/img/logo_bw.png";
                break;
            case "Saarschleife":
                value = "images/img/logo_saar.png";
                break;
            case "Schwarzwald":
                value = "images/img/logo_sw.png";
                break;
            case "Lipno":
                value = "images/img/logo_lipno_de.png";
                break;

            default:
                value = "images/img/logo_default.png";
                break;
        }

        String image = getURL(value).toExternalForm();
        /*pane.setStyle("-fx-background-image: url('" + image + "');" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-attachment: fixed;" +
                "-fx-background-size: 10% auto;" +
                "-fx-background-position: 98% 5%;");*/
    }

    @FXML
    private void next() {
        if (true /*check()*/) {
            if (pageNumber < pageCount) {
                pageNumber++;
                //screenController.activate(SceneName.SURVEY + pageNumber);
            } else {
                if (!isPreview) {
                    //SurveyService.saveSurvey(GlobalVars.activeQuestionnaire.getId(), GlobalVars.questionsPerPanel);
                    System.out.println("save is disabled");
                    screenController.activate(GratitudeController.class);
                } else {
                    System.out.println("still page " + pageNumber);
                }
            }
        } else {
            System.out.println("everythingIsNOTAwesome");
        }
    }

    @FXML
    private void exit() {
        if (isPreview) {
            screenController.activate(QuestionController.class);
        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Befragung abbrechen");
            alert.setHeaderText("Wollen Sie die Befragung wirklich verlassen?\n"
                    + "Alle Ihre eingetragenen Daten werden nicht gespeichert!");
            alert.setContentText("Fortfahren?");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                screenController.activate(StartController.class);
            }
        }
    }

    @FXML
    private void pre() {
        if (pageNumber > 1) {
            pageNumber--;
            //screenController.activate("survey_" + pageNumber);
        } else {
            System.out.println("still page 1");
        }
    }

    private boolean check() {
        GlobalVars.everythingIsAwesome = true;
        if (GlobalVars.IGNORE_CHECK && GlobalVars.DEV_MODE) return true;
        for (Question question : GlobalVars.questionsPerPanel.get(GlobalVars.page)) {
            if (!checkInt(question) || !checkPflichtfrage(question)) {
                GlobalVars.everythingIsAwesome = false;
                break;
            }
        }


        if (GlobalVars.everythingIsAwesome) {
            for (Question question : GlobalVars.questionsPerPanel.get(GlobalVars.page)) {
                if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                    if (question.getFlags().isList()) {
                        List<AnswerOption> submittedAnswerOptions = new ArrayList<>();
                        ListView<AnswerOption> listView = question.getAnswerOptionListView();

                        if (listView.isVisible()) {
                            submittedAnswerOptions.addAll(listView.getSelectionModel().getSelectedItems());
                        }

                        SubmittedAnswer submittedAnswer = new SubmittedAnswer(submittedAnswerOptions);
                        question.setSubmittedAnswer(submittedAnswer);

                    } else {

                        ArrayList<AnswerOption> submittedAnswerOptions = new ArrayList<>();
                        for (CheckBox checkbox : question.getAnswerCheckBoxes()) {
                            if (checkbox.isSelected() && checkbox.isVisible()) {
                                submittedAnswerOptions.add((AnswerOption) checkbox.getUserData());
                            }
                        }

                        SubmittedAnswer submittedAnswer = new SubmittedAnswer(submittedAnswerOptions);
                        question.setSubmittedAnswer(submittedAnswer);
                    }
                } else {

                    if (question.getFlags().isTextArea()) {
                        TextArea textArea = question.getAnswerTextArea();
                        SubmittedAnswer submittedAnswer = new SubmittedAnswer();
                        if (!textArea.getText().isBlank() && textArea.isVisible()) {
                            submittedAnswer.setSubmittedAnswerText(textArea.getText());
                        }

                        question.setSubmittedAnswer(submittedAnswer);

                    } else {
                        SubmittedAnswer submittedAnswer = new SubmittedAnswer();
                        TextField textField = question.getAnswerTextField();
                        if (!textField.getText().isBlank() && textField.isVisible()) {
                            submittedAnswer.setSubmittedAnswerText(textField.getText());
                        }
                        question.setSubmittedAnswer(submittedAnswer);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prueft, ob die gegebene Frage dem de.vatrascell.nezr.flag INT genuegt.
     *
     * @param question FrageErstellen: die Frage
     * @return boolean
     */
	/*
	private boolean checkInt(Frage frage) {
		Pattern MY_PATTERNint = Pattern.compile("INT[<>=]=[0-9]+");
		Matcher mint = MY_PATTERNint.matcher(frage.getFlags());
		if (mint.find()) {
			String s = mint.group(0);
			int i = Integer.parseInt(s.substring(5));
			String op = s.substring(3, 5);
			
			TextField textField = frage.getAntwortenFF().get(0);
			if(textField.getText().equals("")) {
				return true;
			} else {
				switch(op) {
				case "==":
					try {
						Integer.parseInt(frage.getAntwortenFF().get(0).getText());
						if(textField.getText().length() == i) {
							return true;
						} else {
//							BalloonTip fehler = new BalloonTip(textField, "die Zahl ist nicht gleich " + i + " Zeichen lang");
//							fehler.getStyle();
//							fehler.setCloseButton(null);
//							TimingUtils.showTimedBalloon(fehler, 2000);
//							fehler.setVisible(true);
							return false;
						}
					} catch (NumberFormatException eeee) {
//						BalloonTip fehler = new BalloonTip(textField, "keine g\u00fcltige Zahl");
//						fehler.setCloseButton(null);
//						TimingUtils.showTimedBalloon(fehler, 2000);
//						fehler.setVisible(true);
						return false;
					}
				case "<=":
					try {
						Integer.parseInt(frage.getAntwortenFF().get(0).getText());
						if(textField.getText().length() <= i) {
							return true;
						} else {
//							BalloonTip fehler = new BalloonTip(textField, "die Zahl ist nicht kleiner oder gleich " + i + " Zeichen lang");
//							fehler.setCloseButton(null);
//							TimingUtils.showTimedBalloon(fehler, 2000);
//							fehler.setVisible(true);
							return false;
						}
					} catch (NumberFormatException ee) {
						//BalloonTip fehler = new BalloonTip(textField, "keine g\u00fcltige Zahl");
						//fehler.setCloseButton(null);
						//TimingUtils.showTimedBalloon(fehler, 2000);
						//fehler.setVisible(true);
						return false;
					}
				case ">=":
					try {
						Integer.parseInt(frage.getAntwortenFF().get(0).getText());
						if(textField.getText().length() >= i) {
							return true;
						} else {
							//BalloonTip fehler = new BalloonTip(textField, "die Zahl ist nicht gr\u00f6\u00dfer oder gleich " + i + " Zeichen lang");
							//fehler.setCloseButton(null);
							//TimingUtils.showTimedBalloon(fehler, 2000);
							//fehler.setVisible(true);
							return false;
						}
					} catch (NumberFormatException eee) {
						//BalloonTip fehler = new BalloonTip(textField, "keine g\u00fcltige Zahl");
						//fehler.setCloseButton(null);
						//TimingUtils.showTimedBalloon(fehler, 2000);
						//fehler.setVisible(true);
						return false;
					}
					default:
						return true;
				}	
			}
		} else {
			return true;
		}
	}*/
    private boolean checkInt(Question question) {
        return true;
        //TODO real time de.vatrascell.nezr.validation
        /*
        List<Number> numbers = de.vatrascell.nezr.question.getFlags().getAll(Number.class);
        for (Number number : numbers) {
            TextField textField = de.vatrascell.nezr.question.getAnswersFF().get(0);
            if (textField.getText().equals("")) {
                return true;
            }
            switch (number.getOperator()) {
                case EQ:
                    try {
                        Integer.parseInt(de.vatrascell.nezr.question.getAnswersFF().get(0).getText());
                        if (textField.getText().length() == number.getDigits()) {
                            continue;
                        } else {
//						BalloonTip fehler = new BalloonTip(textField, "die Zahl ist nicht gleich " + i + " Zeichen lang");
//						fehler.getStyle();
//						fehler.setCloseButton(null);
//						TimingUtils.showTimedBalloon(fehler, 2000);
//						fehler.setVisible(true);
                            return false;
                        }
                    } catch (NumberFormatException eeee) {
//					BalloonTip fehler = new BalloonTip(textField, "keine g\u00fcltige Zahl");
//					fehler.setCloseButton(null);
//					TimingUtils.showTimedBalloon(fehler, 2000);
//					fehler.setVisible(true);
                        return false;
                    }
                case LTE:
                    try {
                        Integer.parseInt(de.vatrascell.nezr.question.getAnswersFF().get(0).getText());
                        if (textField.getText().length() <= number.getDigits()) {
                            continue;
                        } else {
//						BalloonTip fehler = new BalloonTip(textField, "die Zahl ist nicht kleiner oder gleich " + i + " Zeichen lang");
//						fehler.setCloseButton(null);
//						TimingUtils.showTimedBalloon(fehler, 2000);
//						fehler.setVisible(true);
                            return false;
                        }
                    } catch (NumberFormatException ee) {
                        //BalloonTip fehler = new BalloonTip(textField, "keine g\u00fcltige Zahl");
                        //fehler.setCloseButton(null);
                        //TimingUtils.showTimedBalloon(fehler, 2000);
                        //fehler.setVisible(true);
                        return false;
                    }
                case GTE:
                    try {
                        Integer.parseInt(de.vatrascell.nezr.question.getAnswersFF().get(0).getText());
                        if (textField.getText().length() >= number.getDigits()) {
                        } else {
                            //BalloonTip fehler = new BalloonTip(textField, "die Zahl ist nicht gr\u00f6\u00dfer oder gleich " + i + " Zeichen lang");
                            //fehler.setCloseButton(null);
                            //TimingUtils.showTimedBalloon(fehler, 2000);
                            //fehler.setVisible(true);
                            return false;
                        }
                    } catch (NumberFormatException eee) {
                        //BalloonTip fehler = new BalloonTip(textField, "keine g\u00fcltige Zahl");
                        //fehler.setCloseButton(null);
                        //TimingUtils.showTimedBalloon(fehler, 2000);
                        //fehler.setVisible(true);
                        return false;
                    }
            }
        }
        return true;*/
    }

    /**
     * Prueft, ob die gegebene Frage der Pfichtfrage genuegt.
     *
     * @param question FrageErstellen: die Frage
     * @return boolean
     */
    private boolean checkPflichtfrage(Question question) {
        if (question.getFlags().isRequired() && question.getQuestionLabel().isVisible()) {
            if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                boolean selected = false;
                for (CheckBox checkbox : question.getAnswerCheckBoxes()) {
                    if (checkbox.isSelected()) {
                        selected = true;
                        break;
                    }
                }
                if (selected) {
                    return true;
                } else {
//					BalloonTip fehler = new BalloonTip(button, "Das ist eine Pflichtfrage!");
//					fehler.setCloseButton(null);
//					TimingUtils.showTimedBalloon(fehler, 2000);
//					fehler.setVisible(true);
                    return false;
                }
            } else {
                if (question.getFlags().isList()) {
                    ListView<AnswerOption> listView = question.getAnswerOptionListView();
                    if (listView.getSelectionModel().isEmpty()) {
//							BalloonTip fehler = new BalloonTip(button, "Das ist eine Pflichtfrage!");
//							fehler.setCloseButton(null);
//							TimingUtils.showTimedBalloon(fehler, 2000);
//							fehler.setVisible(true);
                        return false;
                    } else {
                        return true;
                    }

                } else if (question.getFlags().isTextArea()) {
                    TextArea myText = question.getAnswerTextArea();
                    if (myText.getText().isEmpty()) {
//							BalloonTip fehler = new BalloonTip(button, "Das ist eine Pflichtfrage!");
//							fehler.setCloseButton(null);
//							TimingUtils.showTimedBalloon(fehler, 2000);
//							fehler.setVisible(true);
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    boolean selected = false;
                    TextField textField = question.getAnswerTextField();
                    if (!textField.getText().equals("")) {
                        selected = true;
                    }

                    if (selected) {
                        return true;
                    } else {
//						BalloonTip fehler = new BalloonTip(button, "Das ist eine Pflichtfrage!");
//						fehler.setCloseButton(null);
//						TimingUtils.showTimedBalloon(fehler, 2000);
//						fehler.setVisible(true);
                        return false;
                    }
                }
            }
        } else {
            return true;
        }
    }

    public static boolean isPreview() {
        return isPreview;
    }

    public static void setPreview(boolean isPreview) {
        SurveyController.isPreview = isPreview;
    }

    @FXML
    private void exitPreView() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Vorschau abbrechen");
        alert.setHeaderText("Wollen Sie die Vorschau wirklich verlassen?");
        alert.setContentText("Fortfahren?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            screenController.activate(QuestionController.class);
        }

    }
}
