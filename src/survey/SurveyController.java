package survey;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import application.Datenbank;
import application.GlobalVars;
import application.ScreenController;
import flag.Number;
import flag.SymbolType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Frage;


public class SurveyController {
	
	@FXML
	private void next() throws IOException {
		if(check()) {
			if(GlobalVars.page < GlobalVars.countPanel - 1) {
				GlobalVars.page++;
				ScreenController.activate("survey_" + GlobalVars.page);
			} else {
				SurveyService.saveUmfrage(GlobalVars.fragenJePanel);
				ScreenController.addScreen(model.Scene.Gratitude.scene(), 
						new Scene(FXMLLoader.load(getClass().getResource("../gratitude/GratitudeView.fxml"))));
				ScreenController.activate(model.Scene.Gratitude.scene());
			}
		} else {
			System.out.println("everythingIsNOTAwesome");
		}
	}
	
	@FXML
	private void pre() {
		if(GlobalVars.page > 0) {
			GlobalVars.page--;
			ScreenController.activate("survey_" + GlobalVars.page);
		} else {
			System.out.println("still page 1");
		}
	}
	
	@FXML
	private void exit() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Befragung abbrechen");
		alert.setHeaderText("Wollen Sie die Befragung wirklich verlassen?\n"
				+ "Alle Ihre eingetragenen Daten werden nicht gespeichert!");
		alert.setContentText("Fortfahren?");
		
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(
		   getClass().getResource("../application/application.css").toExternalForm());

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			ScreenController.activate(model.Scene.Start.scene());
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
		
	}
	
	@FXML
	private void exitPreVeiw() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Vorschau abbrechen");
		alert.setHeaderText("Wollen Sie die Vorschau wirklich verlassen?");
		alert.setContentText("Fortfahren?");
		
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(
		   getClass().getResource("../application/application.css").toExternalForm());

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			ScreenController.activate(model.Scene.Question.scene());
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
		
	}
	
	private boolean check() {
		GlobalVars.everythingIsAwesome = true;
		for(Frage frage : GlobalVars.fragenJePanel.get(GlobalVars.page)) {
            if(!checkInt(frage) || !checkPflichtfrage(frage)) {
            	GlobalVars.everythingIsAwesome = false;
            	break;
            }
		}
		
		
		if(GlobalVars.everythingIsAwesome) {
			for(Frage frage : GlobalVars.fragenJePanel.get(GlobalVars.page)) {
				if(frage.getArt() == "MC") {
					if(frage.getFlags().is(SymbolType.LIST)) {
						Vector<String> antwort = new Vector<String>();
						for(ListView<String> listView : frage.getAntwortenLIST()) {
							if(listView.isVisible()) {
								for(String value : listView.getSelectionModel().getSelectedItems()) {
									antwort.addElement(value);
								}
							}
						}
						frage.setAntwort(antwort);
					} else {
						Vector<String> antwort = new Vector<String>();
						for(CheckBox checkbox : frage.getAntwortenMC()) {
							if(checkbox.isSelected() && checkbox.isVisible()) {
								antwort.addElement(checkbox.getText());
							}
						}
						frage.setAntwort(antwort);
					}
				} else {
					if(frage.getFlags().is(SymbolType.TEXT)) {
						Vector<String> antwort = new Vector<String>();
						for(TextArea textArea : frage.getAntwortenTEXT()) {
							if(!textArea.getText().equals("") && textArea.isVisible()) {
								String string = textArea.getText();
								antwort.addElement(Datenbank.slashUnicode(string));
							}
						}
						frage.setAntwort(antwort);
					} else {
						Vector<String> antwort = new Vector<String>();
						for(TextField textField : frage.getAntwortenFF()) {
							if(!textField.getText().equals("") && textField.isVisible()) {
								String string = textField.getText();
								antwort.addElement(Datenbank.slashUnicode(string));
							}
						}
						frage.setAntwort(antwort);
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
	 * @param frage FrageErstellen: die Frage
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
	
	private boolean checkInt(Frage frage) {
		TextField textField = frage.getAntwortenFF().get(0);
		if(textField.getText().equals("")) {
			return true;
		}
		List<Number> numbers = frage.getFlags().getAll(Number.class);
		for (Number number : numbers) {
			switch (number.getOperator()) {
			case EQ:
				try {
					Integer.parseInt(frage.getAntwortenFF().get(0).getText());
					if(textField.getText().length() == number.getDigits()) {
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
					Integer.parseInt(frage.getAntwortenFF().get(0).getText());
					if(textField.getText().length() <= number.getDigits()) {
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
					Integer.parseInt(frage.getAntwortenFF().get(0).getText());
					if(textField.getText().length() >= number.getDigits()) {
						continue;
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
		return true;
	}
	
	/**
	 * Prueft, ob die gegebene Frage der Pfichtfrage genuegt.
	 * @param frage FrageErstellen: die Frage
	 * @param button MyButton: der Weiter Button
	 * @return boolean
	 */
	private boolean checkPflichtfrage(Frage frage) {
		if(frage.getFlags().is(SymbolType.REQUIRED) && frage.getFrageLabel().isVisible()) {
			if (frage.getArt() == "MC") {
				boolean selected = false;
				for (CheckBox checkbox : frage.getAntwortenMC()) {
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
				if (frage.getFlags().is(SymbolType.LIST)) {
					for (ListView<String> listView : frage.getAntwortenLIST()) {
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

				} else if (frage.getFlags().is(SymbolType.TEXT)) {
					for (TextArea myText : frage.getAntwortenTEXT()) {
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
					for (TextField textField : frage.getAntwortenFF()) {
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
}
