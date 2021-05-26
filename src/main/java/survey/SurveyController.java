package survey;

import application.GlobalVars;
import application.ScreenController;
import flag.Number;
import flag.SymbolType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import model.Question;
import model.QuestionType;
import model.SceneName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static application.GlobalFuncs.getURL;
import static application.ScreenController.STYLESHEET;


public class SurveyController {

    private static boolean isPreview;

    @FXML
    Pane pane;

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
        pane.setStyle("-fx-background-image: url('" + image + "');" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-attachment: fixed;" +
                "-fx-background-size: 10% auto;" +
                "-fx-background-position: 98% 5%;");
    }

    @FXML
    private void next() throws IOException {
        if (check()) {
            if (GlobalVars.page < GlobalVars.countPanel - 1) {
                GlobalVars.page++;
                ScreenController.activate(SceneName.SURVEY + GlobalVars.page);
            } else {
                if (!isPreview) {
                    SurveyService.saveSurvey(GlobalVars.activeQuestionnaire.getId(), GlobalVars.questionsPerPanel);
                    ScreenController.addScreen(SceneName.GRATITUDE, getURL(SceneName.GRATITUDE_PATH));
                    ScreenController.activate(SceneName.GRATITUDE);
                } else {
                    System.out.println("still page " + GlobalVars.page);
                }
            }
        } else {
            System.out.println("everythingIsNOTAwesome");
        }
    }

    @FXML
    private void pre() throws IOException {
        if (GlobalVars.page > 0) {
            GlobalVars.page--;
            ScreenController.activate("survey_" + GlobalVars.page);
        } else {
            System.out.println("still page 1");
        }
    }

    @FXML
    private void exit() throws IOException {
        if (isPreview) {
            ScreenController.activate(SceneName.QUESTION);
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
                ScreenController.activate(SceneName.START);
            }
        }
    }

    @FXML
    private void exitPreVeiw() throws IOException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Vorschau abbrechen");
        alert.setHeaderText("Wollen Sie die Vorschau wirklich verlassen?");
        alert.setContentText("Fortfahren?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ScreenController.activate(SceneName.QUESTION);
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
                        ArrayList<String> antwort = new ArrayList<>();
                        for (ListView<String> listView : question.getAnswersLIST()) {
                            if (listView.isVisible()) {
                                antwort.addAll(listView.getSelectionModel().getSelectedItems());
                            }
                        }
                        question.setAnswer(antwort);
                    } else {
                        ArrayList<String> antwort = new ArrayList<>();
                        for (CheckBox checkbox : question.getAnswersMC()) {
                            if (checkbox.isSelected() && checkbox.isVisible()) {
                                antwort.add(checkbox.getText());
                            }
                        }
                        question.setAnswer(antwort);
                    }
                } else {
                    if (question.getFlags().isTextArea()) {
                        ArrayList<String> antwort = new ArrayList<>();
                        for (TextArea textArea : question.getAnswersTEXT()) {
                            if (!textArea.getText().equals("") && textArea.isVisible()) {
                                String string = textArea.getText();
                                antwort.add(string);
                            }
                        }
                        question.setAnswer(antwort);
                    } else {
                        ArrayList<String> antwort = new ArrayList<>();
                        for (TextField textField : question.getAnswersFF()) {
                            if (!textField.getText().equals("") && textField.isVisible()) {
                                String string = textField.getText();
                                antwort.add(string);
                            }
                        }
                        question.setAnswer(antwort);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prueft, ob die gegebene Frage dem flag INT genuegt.
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
        //TODO real time validation
        /*
        List<Number> numbers = question.getFlags().getAll(Number.class);
        for (Number number : numbers) {
            TextField textField = question.getAnswersFF().get(0);
            if (textField.getText().equals("")) {
                return true;
            }
            switch (number.getOperator()) {
                case EQ:
                    try {
                        Integer.parseInt(question.getAnswersFF().get(0).getText());
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
                        Integer.parseInt(question.getAnswersFF().get(0).getText());
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
                        Integer.parseInt(question.getAnswersFF().get(0).getText());
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
                for (CheckBox checkbox : question.getAnswersMC()) {
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
                    for (ListView<String> listView : question.getAnswersLIST()) {
                        if (listView.getSelectionModel().isEmpty()) {
//							BalloonTip fehler = new BalloonTip(button, "Das ist eine Pflichtfrage!");
//							fehler.setCloseButton(null);
//							TimingUtils.showTimedBalloon(fehler, 2000);
//							fehler.setVisible(true);
                            return false;
                        } else {
                            return true;
                        }
                    }

                } else if (question.getFlags().isTextArea()) {
                    for (TextArea myText : question.getAnswersTEXT()) {
                        if (myText.getText().isEmpty()) {
//							BalloonTip fehler = new BalloonTip(button, "Das ist eine Pflichtfrage!");
//							fehler.setCloseButton(null);
//							TimingUtils.showTimedBalloon(fehler, 2000);
//							fehler.setVisible(true);
                            return false;
                        } else {
                            return true;
                        }
                    }
                } else {
                    boolean selected = false;
                    for (TextField textField : question.getAnswersFF()) {
                        if (!textField.getText().equals("")) {
                            selected = true;
                            break;
                        }
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
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * @return the isPreview
     */
    public static boolean isPreview() {
        return isPreview;
    }

    /**
     * @param isPreview the isPreview to set
     */
    public static void setPreview(boolean isPreview) {
        SurveyController.isPreview = isPreview;
    }
}
