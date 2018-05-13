package start;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import application.GlobalVars;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
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
	
	private void createSurveyScreens(Vector<Frage> fragen) {
		for(Frage frage : fragen) {
			
		}
	}
	
	/**
	 * Erstellt dynamisch alle Panels des Fragebogens mit ihren Elementen.
	 */
	private void makeFragebogen(Vector<Frage> fragen) {
		try {
		// TODO 
		Vector<Vector<Frage>> fragenJePanel = new Vector<Vector<Frage>>();
		/*------------------------- von Julian und Eric --------------------------*/
		
		Vector<Screen> allePanel = new Vector<>();
		
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
			BgPanel panel_1 = new BgPanel(false);
			frame.getContentPane().add(panel_1, "panel_" + z);
			panel_1.setLayout(new BorderLayout(0, 0));
			
			BgPanel panel_2 = new BgPanel(true);
			panel_1.add(panel_2, BorderLayout.CENTER);
			panel_2.setLayout(createMig(GlobalsVars.proZeile, (int)Math.ceil(fragen.get(z - 1).getAntwort_moeglichkeit().size() / GlobalsVars.proZeile), 0, 0, new int[]{100}, new int[]{100}));
			
			BgPanel panel_3 = new BgPanel(false);
			panel_1.add(panel_3, BorderLayout.SOUTH);
			panel_1.setPreferredSize(new Dimension(screenSize.width, 150));
			panel_3.setLayout(createMig(3, 1, 100, 100, new int[] {98, 2}, new int[] {100}));
			
			MyProgressBar progressBar = new MyProgressBar(0, maxCount);
			progressBar.setValue(z);
			panel_2.add(progressBar, "span , align center, wrap");
			
			JLabel lblFragenr = new JLabel("Frage " + z + "/" + maxCount);
			lblFragenr.setFont(new Font("Tahoma", Font.PLAIN, 32));
			lblFragenr.setForeground(new Color(154, 188, 42));
			panel_2.add(lblFragenr, "span, align center, wrap");
			
			//Erstellt ein Abbruch Button
			MyButton btnAbbruch = new MyButton("x");
			btnAbbruch.addActionListener(bl);
			panel_3.add(btnAbbruch);
			
			//Erstellt ein Zurueck Button
			MyButton btnZurck = new MyButton("<");
			btnZurck.addActionListener(bl);
			if(z == 1) {
				btnZurck.setEnabled(false);
			}
			panel_3.add(btnZurck);
			
			//Erstellt ein Weiter Button
			btnWeiter = new MyButton(">");
			btnWeiter.addActionListener(bl);
			panel_3.add(btnWeiter);
			
			
			allePanel.addElement(panel_2);
		}
		
		for(int z = 0; z < allePanel.size(); z++) {
			boolean isBHeader = false;
			boolean hasUeber = false;
			setEverythingIsAwesome(true);
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
					
					MyLabel lblUeber = new MyLabel(ueberschrift);
					lblUeber.setFont(new Font("Tahoma", Font.PLAIN, 50));
					lblUeber.setForeground(new Color(236, 103, 8));
					allePanel.get(z).add(lblUeber, "align center, span, wrap");
					hasUeber = true;
				}
				
				if(!isBHeader && frageObj.get(y).getFlags().indexOf("B") >= 0) {
					
					lblEins = new MyLabel("<html><div align=\"center\">sehr<br>schlecht</div></html>"); //anneSuperNeu
					lblEins.setFont(new Font("Tahoma", Font.PLAIN, 20));
					lblEins.setForeground(new Color(95, 55, 43));
					lblEins.setVisible(true);
					allePanel.get(z).add(lblEins, "center");
					
					for(int m = 0; m < 8; m++) {
						JPanel empty = new JPanel();
						empty.setVisible(false);
						allePanel.get(z).add(empty, "");
					}
					
					lblZehn = new MyLabel("sehr gut");  //anneSuperNeu
					lblZehn.setFont(new Font("Tahoma", Font.PLAIN, 20));
					lblZehn.setForeground(new Color(95, 55, 43));
					lblZehn.setVisible(true);
					allePanel.get(z).add(lblZehn, "center");
					
					lblNull = new MyLabel("<html><div align=\"center\">keine<br>Aussage</div></html>"); //anneSuperNeu
					lblNull.setFont(new Font("Tahoma", Font.PLAIN, 20));
					lblNull.setForeground(new Color(95, 55, 43));
					lblNull.setVisible(true);
					allePanel.get(z).add(lblNull, "center, wrap");
					
					isBHeader = true;
				}
				
				//Fuegt eine Frage ein
				Vector<MyCheckbox> checkBoxen = new Vector<MyCheckbox>();
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
					frageAnzeige = "<html><div align=\"center\">" + frageAnzeige + add + "</div></html>";
					
				} else {
					frageAnzeige = frage + add;
				}
				
				MyLabel lblFrage = new MyLabel(frageAnzeige);
				lblFrage.setFont(new Font("Tahoma", Font.PLAIN, 40));
				lblFrage.setForeground(new Color(236, 103, 8));
				
				Pattern MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
				Matcher mges = MY_PATTERN.matcher(frageObj.get(y).getFlags());
				if (mges.find()) {
					lblFrage.setVisible(false);
				}
				
				allePanel.get(z).add(lblFrage, "align center, span, wrap");
				frageObj.get(y).setFrageLabel(lblFrage);
				
				if(frageObj.get(y).getArt() == "FF"){
					
					if(frageObj.get(y).getFlags().indexOf("TEXT") >= 0) {
						//F�gt eine Textarea ein
						MyTextArea textArea = new MyTextArea(10,50); //anneSuperNeu
						textArea.setPreferredSize(new Dimension(200, 50));
						allePanel.get(z).add(textArea, "span, center");
						Vector<MyTextArea> textAreas = new Vector<MyTextArea>();
						textAreas.add(textArea);
						frageObj.get(y).setAntwortenTEXT(textAreas);
					} else {
						if(frageObj.get(y).getFlags().indexOf("LIST") >= 0) {
							ErrorLog.fehlerBerichtB("ERROR",
									Datenbank.class + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), "Fehler");
						} else {
							//F�gt ein Textfeld ein
							MyTextField textField = new MyTextField();
							textField.setPreferredSize(new Dimension(200, 50));
							
							MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
							mges = MY_PATTERN.matcher(frageObj.get(y).getFlags());
							if (mges.find()) {
								textField.setVisible(false);
							}
							
							
							allePanel.get(z).add(textField, "wrap, span, center");
							Vector<MyTextField> textFields = new Vector<MyTextField>();
							textFields.add(textField);
							frageObj.get(y).setAntwortenFF(textFields);
						}
					}
					
				} else if (frageObj.get(y).getArt() == "MC") {
					
					if(frageObj.get(y).getFlags().indexOf("LIST") >= 0) {
						//Erstellt eine Liste
						Vector<JScrollPane> antwortenLIST = new Vector<JScrollPane>();
						JScrollPane scrollPane = new JScrollPane();
						scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
						allePanel.get(z).add(scrollPane, "span, center");
						MyList liste = new MyList(frageObj.get(y).getAntwort_moeglichkeit());
						scrollPane.setViewportView(liste);
						
						MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
						mges = MY_PATTERN.matcher(frageObj.get(y).getFlags());
						if (mges.find()) {
							scrollPane.setVisible(false);
						}
						
						antwortenLIST.add(scrollPane);
						frageObj.get(y).setAntwortenLIST(antwortenLIST);
						continue;
					}
					
					Vector<Integer> anzahlZeile = new Vector<Integer>();
					int intAntworten = frageObj.get(y).getAntwort_moeglichkeit().size();
					do {
						if(intAntworten > GlobalsVars.proZeile) {
							anzahlZeile.addElement(GlobalsVars.proZeile);
							intAntworten -= GlobalsVars.proZeile;
						}
					} while(intAntworten > GlobalsVars.proZeile);
					anzahlZeile.addElement(intAntworten);
					
					//Uebe die schleife aus, wenn count kleiner ist als die groesse der antwortmoeglichkeiten
					Vector<MyCheckbox> checkboxs = new Vector<MyCheckbox>();
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
								antwortAnzeige = antwort.replace("\n", "<br/>");
								antwortAnzeige = "<html><div align=\"center\">" + antwort + "</div></html>";
							}
						} else {
							antwortAnzeige = antwort;
						}
						MyCheckbox chckbxSda = new MyCheckbox(antwortAnzeige);
						chckbxSda.setFont(new Font("Tahoma", Font.PLAIN, 28));
						chckbxSda.setForeground(new Color(94, 56, 41));
						
						MY_PATTERN = Pattern.compile("MC[0-9]+A[0-9]+");
						mges = MY_PATTERN.matcher(frageObj.get(y).getFlags());
						if (mges.find()) {
							chckbxSda.setVisible(false);
						}
						
						checkBoxen.add(chckbxSda);
						if(!isMC) {
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
						});
						} else {
							chckbxSda.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									
								}
							});
						}
						
						if(frageObj.get(y).getFlags().indexOf("B") >= 0) {
							if(!frageObj.get(y).getFrageLabel().isVisible()) {
								lblNull.setVisible(false);
								lblEins.setVisible(false);
								lblZehn.setVisible(false);
							}
							if((count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1)) {
								allePanel.get(z).add(chckbxSda, "");
								
								//JPanel empty = new JPanel();
								//empty.setVisible(false);
								//allePanel.get(z).add(empty, "wrap");
							} else {
								allePanel.get(z).add(chckbxSda, "");
							}
							
						} else {
							if((count3 % (GlobalsVars.proZeile)) == (GlobalsVars.proZeile - 1)) {
								allePanel.get(z).add(chckbxSda, "");
								
								JPanel empty = new JPanel();
								empty.setVisible(false);
								allePanel.get(z).add(empty, "wrap");
							} else {
								allePanel.get(z).add(chckbxSda, "");
							}
						}
						checkboxs.addElement(chckbxSda);
						
						if(count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1) {
							for(int v = 0; v < anzahlZeile.size(); v++) {
								while(anzahlZeile.get(v) != (GlobalsVars.proZeile)) {
									anzahlZeile.set(v, anzahlZeile.get(v) + 1);
									JPanel empty = new JPanel();
									empty.setVisible(false);
									if(anzahlZeile.get(v) == (GlobalsVars.proZeile)) {
										allePanel.get(z).add(empty, "wrap");
									} else {
										allePanel.get(z).add(empty, "");
									}
								}
							}
						}	
					}
					frageObj.get(y).setAntwortenMC(checkboxs);
				} else{
					//Error nachricht, wenn das obere nicht zutrift
					ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), "Befragung konnte nicht erstellt/ angezeigt werden!");
				}	
			}
			fragenJePanel.addElement(frageObj);
		}	
		
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
		
		} catch (Exception e) {
			// ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " + Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
		}
	}
	
	@FXML
	private void next() {
		Vector<Frage> fragen = SurveyService.getFragen(GlobalVars.activFragebogen);
		createSurveyScreens(fragen);
		fragen.forEach((Frage frage) -> System.out.println(frage));
	}
}
