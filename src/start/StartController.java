package start;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.GlobalVars;
import application.ScreenController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Frage;
import survey.SurveyService;

public class StartController {
	
	@FXML
	Label lbl_fragebogen;
	
	@FXML
	Label lbl_warning;
	
	@FXML
	ImageView imageView;
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public StartController() {
		GlobalVars.activFragebogen = StartService.getActivFragebogen();
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		Image value;
		switch (GlobalVars.standort) {
		case "R\\ügen":
			value = new Image(".\\test\\img\\logo_nezr.png");
			break;
		case "Bayerischer Wald":
			value = new Image(".\\test\\img\\logo_bw.png");
			break;
		case "Saarschleife":
			value = new Image(".\\test\\img\\logo_saar.png");
			break;
		case "Schwarzwald":
			value = new Image(".\\test\\img\\logo_saar.png");
			break;
		case "Lipno":
			value = new Image(".\\test\\img\\logo_lipno_de.png");
			break;

		default:
			value = new Image(".\\test\\img\\logo_sw.png");
			break;
		}
		imageView.setImage(value);
		
		lbl_fragebogen.setText(
			GlobalVars.activFragebogen == null ? "" : "Fragebogen: " + GlobalVars.activFragebogen.getName()	
		); 
		
		lbl_warning.setText(
				GlobalVars.activFragebogen == null ? "kein Fragebogen ausgewählt" : 
				!GlobalVars.activFragebogen.getOrt().equals(GlobalVars.standort) ? "Fragebogen ist nicht für diesen Standort optimiert" : ""
		);
		
	}
	
	/**
	 * Erstellt dynamisch alle Panels des Fragebogens mit ihren Elementen.
	 */
	private void makeFragebogen(Vector<Frage> fragen) {
		try {
		// TODO 
		Vector<Vector<Frage>> fragenJePanel = new Vector<Vector<Frage>>();
		/*------------------------- von Julian und Eric --------------------------*/
		
		Vector<Scene> allePanel = new Vector<>();
		
		int countPanel = 0;
		Vector<Integer> anzahl = new Vector<Integer>();
		int temp = 1;
		for(int v = 0; v < fragen.size(); v++) {
			if(v + 1 != fragen.size() && fragen.get(v).getPosition() == fragen.get(v + 1).getPosition()) {
				temp++;
			} else {
				if(temp > GlobalVars.fragen) {
					countPanel += (int)(Math.ceil(temp / GlobalVars.fragen));
					while(temp > GlobalVars.fragen) {
						anzahl.addElement(GlobalVars.fragen);
						temp -= GlobalVars.fragen;
					}
					anzahl.addElement(temp);
				} else {
					anzahl.addElement(temp);
				}
				temp = 1;
			}
		}
		
		
		
		Deque<Frage> stack = new ArrayDeque<Frage>();
		
		for(int v = fragen.size() - 1; v >= 0; v--) {
			stack.push(fragen.get(v));
		}
		
		countPanel += fragen.get(fragen.size() - 1).getPosition();
		int maxCount = countPanel;
		
		
		for(int z = 1; z <= countPanel; z++) {
			Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../survey/SurveyView.fxml")));
			
			ProgressBar progressBar = (ProgressBar)scene.lookup("#progressBar");
			progressBar.setProgress((float)z/(float)countPanel);
			
			Label lbl_count = (Label)scene.lookup("#lbl_count");
			lbl_count.setText("Frage " + z + "/" + maxCount);
			
			allePanel.addElement(scene);
		}
		
		for(int z = 0; z < allePanel.size(); z++) {
			
			VBox vBox = (VBox)allePanel.get(z).lookup("#vbox");
			
			boolean isBHeader = false;
			boolean hasUeber = false;
			// setEverythingIsAwesome(true);
			Vector<Frage> frageObj = new Vector<Frage>();

			for(int y = 0; y < anzahl.get(z); y++) {
				frageObj.addElement(stack.pop());
			}
			
			for(int y = 0; y < frageObj.size(); y++) {
				if(!frageObj.get(y).getUeberschrift().equals("") && !hasUeber) {
					
					
					String ueberschrift = frageObj.get(y).getUeberschrift();
					Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
					Matcher ms = MY_PATTERNs.matcher(ueberschrift);
					if (ms.find()) {
						ueberschrift = ueberschrift.substring(0, ms.start());	
					}
					
					Label lbl_headline = (Label)allePanel.get(z).lookup("#lbl_headline");
					lbl_headline.setText(ueberschrift);
					hasUeber = true;
				}
				
				if(!isBHeader && frageObj.get(y).getFlags().indexOf("B") >= 0) {
					
					HBox hBox = new HBox();
					Label lblEins = new Label("sehr\nschlecht");
					hBox.getChildren().add(lblEins);
					Label lblZehn = new Label("sehr gut");
					hBox.getChildren().add(lblZehn);
					Label lblNull = new Label("keine\nAussage");
					hBox.getChildren().add(lblNull);
					
					//TODO: add spacing
					
					vBox.getChildren().add(hBox);
					isBHeader = true;
					
				}
				
				//Fuegt eine Frage ein
				Vector<CheckBox> checkBoxen = new Vector<>();
				boolean isMC = false;
				String frage = frageObj.get(y).getFrage();
				Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
				Matcher ms = MY_PATTERNs.matcher(frage);
				if (ms.find()) {
					frage = frage.substring(0, ms.start());	
				}
				if(frageObj.get(y).getFlags().indexOf("*") >= 0) {
					isMC = true;
				}
				
				String add = "";			
				
				if(frageObj.get(y).getFlags().indexOf("+") >= 0) {
					add = " *";
				}
				
				String frageAnzeige;
				if(frage.length() >= 40) {
					int index = frage.indexOf(" ", 15);
					char[] string = frage.toCharArray();
					string[index] = '\n';
					frageAnzeige = frage.replaceAll("\n", "<br/>");
					frageAnzeige = frageAnzeige + add;
					
				} else {
					frageAnzeige = frage + add;
				}
				
				Label lblFrage = new Label(frageAnzeige);
				
				Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
				Matcher mges = MY_PATTERN.matcher(frageObj.get(y).getFlags());
				if (mges.find()) {
					lblFrage.setVisible(false);
				}
				
				// allePanel.get(z).add(lblFrage, "align center, span, wrap");
				// frageObj.get(y).setFrageLabel(lblFrage);
				vBox.getChildren().add(lblFrage);
				
				
				if(frageObj.get(y).getArt() == "FF"){
					
					if(frageObj.get(y).getFlags().indexOf("TEXT") >= 0) {
						//F�gt eine Textarea ein
						TextArea textArea = new TextArea(); //anneSuperNeu
						//textArea.setPreferredSize(new Dimension(200, 50));
						//allePanel.get(z).add(textArea, "span, center");
						Vector<TextArea> textAreas = new Vector<TextArea>();
						textAreas.add(textArea);
						//frageObj.get(y).setAntwortenTEXT(textAreas);
						vBox.getChildren().add(textArea);
					} else {
						if(frageObj.get(y).getFlags().indexOf("LIST") >= 0) {
							//ErrorLog.fehlerBerichtB("ERROR",
							//		Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), "Fehler");
						} else {
							//F�gt ein Textfeld ein
							TextField textField = new TextField();
							//textField.setPreferredSize(new Dimension(200, 50));
							
							MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
							mges = MY_PATTERN.matcher(frageObj.get(y).getFlags());
							if (mges.find()) {
								textField.setVisible(false);
							}
							
							
							//allePanel.get(z).add(textField, "wrap, span, center");
							Vector<TextField> textFields = new Vector<TextField>();
							textFields.add(textField);
							//frageObj.get(y).setAntwortenFF(textFields);
							vBox.getChildren().add(textField);
						}
					}
					
				} else if (frageObj.get(y).getArt() == "MC") {
					
					if(frageObj.get(y).getFlags().indexOf("LIST") >= 0) {
						//Erstellt eine Liste
						Vector<ListView<String>> antwortenLIST = new Vector<>();
						// scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
						//allePanel.get(z).add(scrollPane, "span, center");
						ListView<String> liste = new ListView<String>();
						liste.setItems(FXCollections.observableArrayList(frageObj.get(y).getAntwort_moeglichkeit()));

						MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
						mges = MY_PATTERN.matcher(frageObj.get(y).getFlags());
						if (mges.find()) {
							//scrollPane.setVisible(false);
						}
						
						antwortenLIST.add(liste);
						vBox.getChildren().add(liste);
						//frageObj.get(y).setAntwortenLIST(antwortenLIST);
						continue;
					}
					
					Vector<Integer> anzahlZeile = new Vector<Integer>();
					int intAntworten = frageObj.get(y).getAntwort_moeglichkeit().size();
					do {
						if(intAntworten > GlobalVars.proZeile) {
							anzahlZeile.addElement(GlobalVars.proZeile);
							intAntworten -= GlobalVars.proZeile;
						}
					} while(intAntworten > GlobalVars.proZeile);
					anzahlZeile.addElement(intAntworten);
					
					HBox hBox = new HBox();
					//Uebe die schleife aus, wenn count kleiner ist als die groesse der antwortmoeglichkeiten
					Vector<CheckBox> checkboxs = new Vector<>();
					for(int count3 = 0; count3 < frageObj.get(y).getAntwort_moeglichkeit().size(); count3++){
						
						//Erstellt eine Checkbox
						String antwort = frageObj.get(y).getAntwort_moeglichkeit().get(count3);
						
						String antwortAnzeige = "";
						if(antwort.length() >= 25) {
							int index = antwort.indexOf(" ", 11);
							if(index != -1) {
								char[] string = antwort.toCharArray();
								string[index] = '\n';
								antwortAnzeige = new String(string);
								antwortAnzeige = antwort;
							}
						} else {
							antwortAnzeige = antwort;
						}
						
						CheckBox chckbxSda = new CheckBox(antwortAnzeige);
						//chckbxSda.setFont(new Font("Tahoma", Font.PLAIN, 28));
						//chckbxSda.setForeground(new Color(94, 56, 41));
						
						MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
						mges = MY_PATTERN.matcher(frageObj.get(y).getFlags());
						if (mges.find()) {
							chckbxSda.setVisible(false);
						}
						
						checkBoxen.add(chckbxSda);
						if(!isMC) {
							chckbxSda.selectedProperty().addListener(new ChangeListener<Boolean>() {
						        public void changed(ObservableValue<? extends Boolean> ov,
						                Boolean old_val, Boolean new_val) {
						                    System.out.println("!isMC" + ov + " - " + old_val + " - " + new_val);
						            }
						        });
							/*
							chckbxSda.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								MyCheckbox jCheckBox = (MyCheckbox) e.getSource();
							    boolean selected = jCheckBox.getModel().isSelected();
								for(int i = 0; i < checkBoxen.size(); i++) {
						        	checkBoxen.get(i).setSelected(false);
						        }
								jCheckBox.setSelected(selected);
						        //String text = jCheckBox.getText();
						        
							}
						});*/
						} else {
							chckbxSda.selectedProperty().addListener(new ChangeListener<Boolean>() {
						        public void changed(ObservableValue<? extends Boolean> ov,
						                Boolean old_val, Boolean new_val) {
						                    System.out.println(ov + " - " + old_val + " - " + new_val);
						            }
						        });
						}
						
						if(frageObj.get(y).getFlags().indexOf("B") >= 0) {
							/*
							if(!frageObj.get(y).getFrageLabel().isVisible()) {
								lblNull.setVisible(false);
								lblEins.setVisible(false);
								lblZehn.setVisible(false);
							}*/
							if((count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1)) {
								//allePanel.get(z).add(chckbxSda, "");
								hBox.getChildren().add(chckbxSda);
								//JPanel empty = new JPanel();
								//empty.setVisible(false);
								//allePanel.get(z).add(empty, "wrap");
							} else {
								//allePanel.get(z).add(chckbxSda, "");
								hBox.getChildren().add(chckbxSda);
							}
							
						} else {
							if((count3 % (GlobalVars.proZeile)) == (GlobalVars.proZeile - 1)) {
								//allePanel.get(z).add(chckbxSda, "");
								hBox.getChildren().add(chckbxSda);
								/*
								JPanel empty = new JPanel();
								empty.setVisible(false);
								allePanel.get(z).add(empty, "wrap");*/
							} else {
								//allePanel.get(z).add(chckbxSda, "");
								hBox.getChildren().add(chckbxSda);
							}
						}
						checkboxs.addElement(chckbxSda);
						
						/*
						if(count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1) {
							for(int v = 0; v < anzahlZeile.size(); v++) {
								while(anzahlZeile.get(v) != (GlobalVars.proZeile)) {
									anzahlZeile.set(v, anzahlZeile.get(v) + 1);
									JPanel empty = new JPanel();
									empty.setVisible(false);
									if(anzahlZeile.get(v) == (GlobalVars.proZeile)) {
										allePanel.get(z).add(empty, "wrap");
									} else {
										allePanel.get(z).add(empty, "");
									}
								}
							}
						}*/	
					}
					vBox.getChildren().add(hBox);
					// frageObj.get(y).setAntwortenMC(checkboxs);
					
				} else{
					//Error nachricht, wenn das obere nicht zutrift
					//ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), "Befragung konnte nicht erstellt/ angezeigt werden!");
				}	
				
			}
			fragenJePanel.addElement(frageObj);
			
		}	
		/*
		for(int y = 0; y < fragen.size(); y++) {
			
			Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
			Matcher mges = MY_PATTERN.matcher(fragen.get(y).getFlags());
			
			if (mges.find()) {

				Pattern MY_PATTERN1 = Pattern.compile("MC[0-9]+");
				Matcher m1 = MY_PATTERN1.matcher(mges.group(0));
				Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
				Matcher m2 = MY_PATTERN2.matcher(mges.group(0));
				Pattern MY_PATTERNint = Pattern.compile("INT[<>=]=[0-9]+");
				Matcher mint = MY_PATTERNint.matcher(fragen.get(y).getFlags());
				if (m1.find() && m2.find()) {
					try {
					fragen.get(y).setTarget(fragen.get(getY(Integer.parseInt(m1.group(0).substring(2)), "MC", fragen)));
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Fehler: " + Integer.parseInt(m1.group(0).substring(2)));
						e.printStackTrace();
					}
					fragen.get(y).setListener(Integer.parseInt(m2.group(0).substring(1)), "MC");
				}
				if (mint.find()) {
					fragen.get(y).setListener(0, mint.group(0));
				}
			}
		}
		
		for(int y = 0; y < fragen.size(); y++) {
			Pattern MY_PATTERN = Pattern.compile("FF[0-9]+A[0-9]+");
			Matcher mges = MY_PATTERN.matcher(fragen.get(y).getFlags());
			if (mges.find()) {
				Pattern MY_PATTERN1 = Pattern.compile("FF[0-9]+");
				Matcher m1 = MY_PATTERN1.matcher(mges.group(0));
				Pattern MY_PATTERN2 = Pattern.compile("A[0-9]+");
				Matcher m2 = MY_PATTERN2.matcher(mges.group(0));
				Pattern MY_PATTERNint = Pattern.compile("INT[<>=]=[0-9]+");
				Matcher mint = MY_PATTERNint.matcher(fragen.get(y).getFlags());
				if (m1.find() && m2.find()) {
					fragen.get(y).setTarget(fragen.get(getY(Integer.parseInt(m1.group(0).substring(2)), "FF", fragen)));
					fragen.get(y).setListener(Integer.parseInt(m2.group(0).substring(1)), "FF");
					
				}
				if (mint.find()) {
					fragen.get(y).setListener(0, mint.group(0));
				}
			}
		}
		*/
		
		for(int i = 0; i < allePanel.size(); ++i) {
			ScreenController.addScreen("survey_" + i, allePanel.get(i));
		}
		
		GlobalVars.countPanel = countPanel;
		
		} catch (Exception e) {
			// ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
		}
	}
	
	@FXML
	private void next() {
		Vector<Frage> fragen = SurveyService.getFragen(GlobalVars.activFragebogen);
		makeFragebogen(fragen);
		ScreenController.activate("survey_0");
		fragen.forEach((Frage frage) -> System.out.println(frage));
	}
}
