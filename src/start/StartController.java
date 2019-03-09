package start;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.GlobalVars;
import application.ScreenController;
import flag.Number;
import flag.React;
import flag.SymbolType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Frage;
import model.PanelInfo;
import survey.SurveyService;

public class StartController {

	@FXML
	Label lbl_fragebogen;

	@FXML
	Label lbl_warning;

	@FXML
	ImageView imageView;

	private static StringProperty fragebogenText = new SimpleStringProperty();
	private static StringProperty fragebogenWarn = new SimpleStringProperty();

	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public StartController() {
		GlobalVars.activFragebogen = StartService.getActivFragebogen();
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		Image value;
		switch (GlobalVars.standort) {
		case "Rügen":
			value = new Image(".\\test\\img\\logo_nezr.png");
			break;
		case "Bayerischer Wald":
			value = new Image(".\\test\\img\\logo_bw.png");
			break;
		case "Saarschleife":
			value = new Image(".\\test\\img\\logo_saar.png");
			break;
		case "Schwarzwald":
			value = new Image(".\\test\\img\\logo_sw.png");
			break;
		case "Lipno":
			value = new Image(".\\test\\img\\logo_lipno_de.png");
			break;

		default:
			value = new Image(".\\test\\img\\logo_default.png");
			break;
		}
		imageView.setImage(value);
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);
		imageView.setCache(true); 
		setStartText();
		lbl_fragebogen.textProperty().bind(fragebogenText);
		lbl_warning.textProperty().bind(fragebogenWarn);
	}

	public static void setStartText() {
		fragebogenText
				.set(GlobalVars.activFragebogen == null ? "" : "Fragebogen: " + GlobalVars.activFragebogen.getName());
		fragebogenWarn.set(GlobalVars.activFragebogen == null ? "kein Fragebogen ausgewählt"
				: !GlobalVars.activFragebogen.getOrt().equals(GlobalVars.standort)
						? "Fragebogen ist nicht für diesen Standort optimiert"
						: "");
	}

	private void makeFragebogen(Vector<Frage> fragen) {
		Deque<Frage> stack = new ArrayDeque<Frage>();

		for (int v = fragen.size() - 1; v >= 0; v--) {
			stack.push(fragen.get(v));
		}

		List<ArrayList<Frage>> fragenJePanel = new ArrayList<ArrayList<Frage>>();
		List<Pane> allePanel = new ArrayList<Pane>();
		do {
			int questionsOnPanel = 0;
			String lastKat = "";
			// create panel
			Pane scene = null;
			try {
				scene = FXMLLoader.load(getClass().getResource("../survey/SurveyView2.fxml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (scene == null)
				throw new NullPointerException();
			PanelInfo info = new PanelInfo();
			fragenJePanel.add(new ArrayList<>());
			do {
				Frage frage = stack.peek();
				if(lastKat.equals("") || lastKat.equals(frage.getKategorie())) {
					frage = stack.pop();
					info = addQuestionToPanel(frage, scene, info);
					fragenJePanel.get(allePanel.size()).add(info.getFrage());
					lastKat = frage.getKategorie();
					if (stack.isEmpty())
						break;
					
				} else {
					break;
				}
			} while (++questionsOnPanel < GlobalVars.proZeile);
			allePanel.add(scene);
		} while (!stack.isEmpty());

		for (int i = 0; i < allePanel.size(); ++i) {
			ProgressBar progressBar = (ProgressBar) allePanel.get(i).lookup("#progressBar");
			progressBar.setProgress((float) (i + 1) / (float) allePanel.size());

			Label lbl_count = (Label) allePanel.get(i).lookup("#lbl_count");
			lbl_count.setText("Frage " + (i + 1) + "/" + allePanel.size());
					
			ScreenController.addScreen("survey_" + i, allePanel.get(i));
		}
		
		//add reaction listener
		for (int y = 0; y < fragen.size(); y++) {

			List<React> reacts = fragen.get(y).getFlags().getAll(React.class);
			List<Number> numbers = fragen.get(y).getFlags().getAll(Number.class);
			for (React react : reacts) {
				fragen.get(y).setTarget(
						fragen.get(getY(react.getQuestionId(), react.getQuestionType().toString(), fragen)));
				fragen.get(y).setListener(react.getAnswerPos(), react.getQuestionType().toString());
			}
			for (Number number : numbers) {
				fragen.get(y).setListener(0, number.toString());
			}
		}

		GlobalVars.fragenJePanel = fragenJePanel;
		GlobalVars.countPanel = allePanel.size();
	}

	private PanelInfo addQuestionToPanel(Frage frage, Pane screen, PanelInfo info) {
		VBox vBox = (VBox) screen.lookup("#vbox");

		// set headline
		if (!frage.getUeberschrift().equals("") && !info.hasHeadline()) {
			Label lbl_headline = (Label) screen.lookup("#lbl_headline");
			lbl_headline.setText(removeMark(frage.getUeberschrift()));
			info.setHeadline(true);
		}

		// set headlines for choice lists
		if (frage.getFlags().is(SymbolType.B) && !info.hasBHeadlines()) {

			HBox hBox = new HBox();
			// hBox.setAlignment(Pos.CENTER);
			Label lblEins = new Label("sehr\nschlecht");
			lblEins.setId("lblEins");
			hBox.getChildren().add(lblEins);
			Label lblZehn = new Label("sehr gut");
			lblZehn.setId("lblZehn");
			hBox.getChildren().add(lblZehn);
			Label lblNull = new Label("keine\nAussage");
			lblNull.setId("lblNull");
			hBox.getChildren().add(lblNull);

			// TODO: add spacing

			vBox.getChildren().add(hBox);
			info.setbHeadlines(true);
		}

		//add question
		vBox.getChildren().add(createQuestionLabel(screen, frage));

		if (frage.getArt() == "FF") {
			vBox.getChildren().add(createFFNode(frage));
		} else if (frage.getArt() == "MC") {
			if (frage.getFlags().is(SymbolType.LIST)) {
				vBox.getChildren().add(createMCListView(frage));
			} else {
				vBox.getChildren().add(createMCCheckboxen(frage));
			}

		}
		info.setFrage(frage);
		return info;
	}

	private Label createQuestionLabel(Pane screen, Frage frage) {
		String questionTest = removeMark(frage.getFrage());

		questionTest = addRequiredTag(questionTest, frage.getFlags().is(SymbolType.REQUIRED));

		Label lblFrage = new Label(questionTest);
		// System.out.println("frageObj.get(y).frageid = " +
		// frageObj.get(y).getFrageID());
		lblFrage.setId("lblFrage_" + frage.getFrageID());

		if (frage.getFlags().hasMCReact()) {
			//lblFrage.setVisible(false);
		}

		// allePanel.get(z).add(lblFrage, "align center, span, wrap");
		frage.setScene(screen);
		frage.setFrageLabel(lblFrage);

		return lblFrage;
	}

	@SuppressWarnings("unchecked")
	private <T extends Control> T createFFNode(Frage frage) {
		if (frage.getFlags().is(SymbolType.TEXT)) {
			// F�gt eine Textarea ein
			TextArea textArea = new TextArea(); // anneSuperNeu
			// textArea.setPreferredSize(new Dimension(200, 50));
			// allePanel.get(z).add(textArea, "span, center");
			Vector<TextArea> textAreas = new Vector<TextArea>();
			textAreas.add(textArea);
			frage.setAntwortenTEXT(textAreas);
			return (T) textArea;
		} else {
			if (frage.getFlags().is(SymbolType.LIST)) {
				// ErrorLog.fehlerBerichtB("ERROR",
				// Datenbank.class + ": " +
				// Thread.currentThread().getStackTrace()[1].getLineNumber(), "Fehler");
			} else {
				// F�gt ein Textfeld ein
				TextField textField = new TextField();
				// textField.setPreferredSize(new Dimension(200, 50));

				if (frage.getFlags().hasFFReact()) {
					textField.setVisible(false);
				}

				// allePanel.get(z).add(textField, "wrap, span, center");
				Vector<TextField> textFields = new Vector<TextField>();
				textFields.add(textField);
				frage.setAntwortenFF(textFields);
				return (T) textField;
			}
		}

		return null;
	}

	private ListView<String> createMCListView(Frage frage) {
		// Erstellt eine Liste
		Vector<ListView<String>> antwortenLIST = new Vector<>();
		// scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		// allePanel.get(z).add(scrollPane, "span, center");
		ListView<String> liste = new ListView<String>();
		liste.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		liste.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};

			cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
				if (cell.isEmpty()) {
					return;
				}

				int index = cell.getIndex();
				if (liste.getSelectionModel().getSelectedIndices().contains(index)) {
					liste.getSelectionModel().clearSelection(index);
				} else {
					liste.getSelectionModel().select(index);
				}

				liste.requestFocus();

				e.consume();
			});

			return cell;
		});

		liste.setItems(FXCollections.observableArrayList(frage.getAntwort_moeglichkeit()));

		if (frage.getFlags().hasMCReact()) {
			//liste.setVisible(false);
		}

		antwortenLIST.add(liste);
		frage.setAntwortenLIST(antwortenLIST);
		return liste;	
	}
	
	private HBox createMCCheckboxen(Frage frage) {
		List<CheckBox> checkBoxen = new ArrayList<>();
		List<Integer> anzahlZeile = new ArrayList<Integer>();
		int intAntworten = frage.getAntwort_moeglichkeit().size();
		do {
			if (intAntworten > GlobalVars.proZeile) {
				anzahlZeile.add(GlobalVars.proZeile);
				intAntworten -= GlobalVars.proZeile;
			}
		} while (intAntworten > GlobalVars.proZeile);
		anzahlZeile.add(intAntworten);

		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		// Uebe die schleife aus, wenn count kleiner ist als die groesse der
		// antwortmoeglichkeiten
		Vector<CheckBox> checkboxs = new Vector<>();
		
		for (int count3 = 0; count3 < frage.getAntwort_moeglichkeit().size(); count3++) {

			// Erstellt eine Checkbox
			String antwort = frage.getAntwort_moeglichkeit().get(count3);

			String antwortAnzeige = "";
			if (antwort.length() >= 25) {
//				int index = antwort.indexOf(" ", 11);
//				if (index != -1) {
//					char[] string = antwort.toCharArray();
//					string[index] = '\n';
//					antwortAnzeige = new String(string);
//					antwortAnzeige = antwort;
//				}
			} else {
				antwortAnzeige = antwort;
			}

			CheckBox chckbxSda = new CheckBox(antwortAnzeige);
			// chckbxSda.setFont(new Font("Tahoma", Font.PLAIN, 28));
			// chckbxSda.setForeground(new Color(94, 56, 41));

			if (frage.getFlags().hasMCReact()) {
				chckbxSda.setVisible(false);
			}
			checkBoxen.add(chckbxSda);
			if (!frage.getFlags().is(SymbolType.MC)) {
				chckbxSda.selectedProperty().addListener(new ChangeListener<Boolean>() {
					public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
							Boolean new_val) {
						// System.out.println("!isMC" + ov + " - " + old_val + " - " + new_val);
						if (new_val) {
							for (int i = 0; i < checkBoxen.size(); i++) {
								if (!checkBoxen.get(i).getText().equals(chckbxSda.getText()))
									checkBoxen.get(i).setSelected(false);
							}
						}
					}
				});
				/*
				 * chckbxSda.addActionListener(new ActionListener() {
				 * 
				 * @Override public void actionPerformed(ActionEvent e) { MyCheckbox jCheckBox =
				 * (MyCheckbox) e.getSource(); boolean selected =
				 * jCheckBox.getModel().isSelected(); for(int i = 0; i < checkBoxen.size(); i++)
				 * { checkBoxen.get(i).setSelected(false); } jCheckBox.setSelected(selected);
				 * //String text = jCheckBox.getText();
				 * 
				 * } });
				 */
			} else {
				chckbxSda.selectedProperty().addListener(new ChangeListener<Boolean>() {
					public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
							Boolean new_val) {
						// System.out.println(ov + " - " + old_val + " - " + new_val);
					}
				});
			}

			if (frage.getFlags().is(SymbolType.B)) {
				/*
				 * TODO if(!frageObj.get(y).getFrageLabel().isVisible()) {
				 * lblNull.setVisible(false); lblEins.setVisible(false);
				 * lblZehn.setVisible(false); }
				 */
				if ((count3 == frage.getAntwort_moeglichkeit().size() - 1)) {
					// allePanel.get(z).add(chckbxSda, "");
					hBox.getChildren().add(chckbxSda);
					// JPanel empty = new JPanel();
					// empty.setVisible(false);
					// allePanel.get(z).add(empty, "wrap");
				} else {
					// allePanel.get(z).add(chckbxSda, "");
					hBox.getChildren().add(chckbxSda);
				}

			} else {
				if ((count3 % (GlobalVars.proZeile)) == (GlobalVars.proZeile - 1)) {
					// allePanel.get(z).add(chckbxSda, "");
					hBox.getChildren().add(chckbxSda);
					/*
					 * JPanel empty = new JPanel(); empty.setVisible(false);
					 * allePanel.get(z).add(empty, "wrap");
					 */
				} else {
					// allePanel.get(z).add(chckbxSda, "");
					hBox.getChildren().add(chckbxSda);
				}
			}
			checkboxs.addElement(chckbxSda);

			/*
			 * if(count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1) { for(int
			 * v = 0; v < anzahlZeile.size(); v++) { while(anzahlZeile.get(v) !=
			 * (GlobalVars.proZeile)) { anzahlZeile.set(v, anzahlZeile.get(v) + 1); JPanel
			 * empty = new JPanel(); empty.setVisible(false); if(anzahlZeile.get(v) ==
			 * (GlobalVars.proZeile)) { allePanel.get(z).add(empty, "wrap"); } else {
			 * allePanel.get(z).add(empty, ""); } } } }
			 */
		}
		frage.setAntwortenMC(checkboxs);
		return hBox;
	}

	private String removeMark(String text) {
		Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
		Matcher ms = MY_PATTERNs.matcher(text);
		if (ms.find()) {
			text = text.substring(0, ms.start());
		}

		return text;
	}

	private String addRequiredTag(String text, boolean required) {
		text = required ? text + " *" : text;
		// if(text.length() >= 40) {
		// int index = text.indexOf(" ", 15);
		// char[] string = text.toCharArray();
		// string[index] = '\n';
		//
		// return string.toString();
		// }
		return text;
	}

	/**
	 * Erstellt dynamisch alle Panels des Fragebogens mit ihren Elementen.
	 */
	private void makeFragebogen2(Vector<Frage> fragen) {
		try {
			// TODO
			List<ArrayList<Frage>> fragenJePanel = new ArrayList<ArrayList<Frage>>();
			/*------------------------- von Julian und Eric --------------------------*/
			// System.out.println((ScreenController.getMain().getHeight() * 3f / (744f -
			// 305f)));
			// System.out.println(ScreenController.getMain().getHeight() * 0.0069);
			// System.out.println(ScreenController.getMain().getHeight());
			GlobalVars.fragen = Math.round(((float) ScreenController.getMain().getHeight() * 0.0069f));

			Vector<Pane> allePanel = new Vector<>();

			int countPanel = 0;
			Vector<Integer> anzahl = new Vector<Integer>();
			int temp = 1;
			for (int v = 0; v < fragen.size(); v++) {
				if (v + 1 != fragen.size() && fragen.get(v).getPosition() == fragen.get(v + 1).getPosition()) {
					temp++;
				} else {
					if (temp > GlobalVars.fragen) {
						countPanel += (int) (Math.ceil(temp / GlobalVars.fragen));
						while (temp > GlobalVars.fragen) {
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

			for (int v = fragen.size() - 1; v >= 0; v--) {
				stack.push(fragen.get(v));
			}

			countPanel += fragen.get(fragen.size() - 1).getPosition();
			int maxCount = countPanel;

			for (int z = 1; z <= countPanel; z++) {
				Pane scene = FXMLLoader.load(getClass().getResource("../survey/SurveyView.fxml"));

				ProgressBar progressBar = (ProgressBar) scene.lookup("#progressBar");
				progressBar.setProgress((float) z / (float) countPanel);

				Label lbl_count = (Label) scene.lookup("#lbl_count");
				lbl_count.setText("Frage " + z + "/" + maxCount);

				allePanel.addElement(scene);
			}

			for (int z = 0; z < allePanel.size(); z++) {

				VBox vBox = (VBox) allePanel.get(z).lookup("#vbox");

				boolean isBHeader = false;
				boolean hasUeber = false;
				// setEverythingIsAwesome(true);
				ArrayList<Frage> frageObj = new ArrayList<Frage>();

				for (int y = 0; y < anzahl.get(z); y++) {
					frageObj.add(stack.pop());
				}

				for (int y = 0; y < frageObj.size(); y++) {
					if (!frageObj.get(y).getUeberschrift().equals("") && !hasUeber) {

						String ueberschrift = frageObj.get(y).getUeberschrift();
						Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
						Matcher ms = MY_PATTERNs.matcher(ueberschrift);
						if (ms.find()) {
							ueberschrift = ueberschrift.substring(0, ms.start());
						}

						Label lbl_headline = (Label) allePanel.get(z).lookup("#lbl_headline");
						lbl_headline.setText(ueberschrift);
						hasUeber = true;
					}

					if (!isBHeader && frageObj.get(y).getFlags().is(SymbolType.B)) {

						HBox hBox = new HBox();
						// hBox.setAlignment(Pos.CENTER);
						Label lblEins = new Label("sehr\nschlecht");
						lblEins.setId("lblEins");
						hBox.getChildren().add(lblEins);
						Label lblZehn = new Label("sehr gut");
						lblZehn.setId("lblZehn");
						hBox.getChildren().add(lblZehn);
						Label lblNull = new Label("keine\nAussage");
						lblNull.setId("lblNull");
						hBox.getChildren().add(lblNull);

						// TODO: add spacing

						vBox.getChildren().add(hBox);
						isBHeader = true;

					}

					// Fuegt eine Frage ein
					Vector<CheckBox> checkBoxen = new Vector<>();
					boolean isMC = false;
					String frage = frageObj.get(y).getFrage();
					Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
					Matcher ms = MY_PATTERNs.matcher(frage);
					if (ms.find()) {
						frage = frage.substring(0, ms.start());
					}
					if (frageObj.get(y).getFlags().is(SymbolType.MC)) {
						isMC = true;
					}

					String add = "";

					if (frageObj.get(y).getFlags().is(SymbolType.REQUIRED)) {
						add = " *";
					}

					String frageAnzeige;
					if (frage.length() >= 40) {
						int index = frage.indexOf(" ", 15);
						char[] string = frage.toCharArray();
						string[index] = '\n';
						// frageAnzeige = frage.replaceAll("\n", "<br/>");
						frageAnzeige = frage + add;

					} else {
						frageAnzeige = frage + add;
					}

					Label lblFrage = new Label(frageAnzeige);
					// System.out.println("frageObj.get(y).frageid = " +
					// frageObj.get(y).getFrageID());
					lblFrage.setId("lblFrage_" + frageObj.get(y).getFrageID());

					if (frageObj.get(y).getFlags().hasMCReact()) {
						lblFrage.setVisible(false);
					}

					// allePanel.get(z).add(lblFrage, "align center, span, wrap");
					frageObj.get(y).setScene(allePanel.get(z));
					frageObj.get(y).setFrageLabel(lblFrage);
					vBox.getChildren().add(lblFrage);

					if (frageObj.get(y).getArt() == "FF") {

						if (frageObj.get(y).getFlags().is(SymbolType.TEXT)) {
							// F�gt eine Textarea ein
							TextArea textArea = new TextArea(); // anneSuperNeu
							// textArea.setPreferredSize(new Dimension(200, 50));
							// allePanel.get(z).add(textArea, "span, center");
							Vector<TextArea> textAreas = new Vector<TextArea>();
							textAreas.add(textArea);
							frageObj.get(y).setAntwortenTEXT(textAreas);
							vBox.getChildren().add(textArea);
						} else {
							if (frageObj.get(y).getFlags().is(SymbolType.LIST)) {
								// ErrorLog.fehlerBerichtB("ERROR",
								// Datenbank.class + ": " +
								// Thread.currentThread().getStackTrace()[1].getLineNumber(), "Fehler");
							} else {
								// F�gt ein Textfeld ein
								TextField textField = new TextField();
								// textField.setPreferredSize(new Dimension(200, 50));

								if (frageObj.get(y).getFlags().hasFFReact()) {
									textField.setVisible(false);
								}

								// allePanel.get(z).add(textField, "wrap, span, center");
								Vector<TextField> textFields = new Vector<TextField>();
								textFields.add(textField);
								frageObj.get(y).setAntwortenFF(textFields);
								vBox.getChildren().add(textField);
							}
						}

					} else if (frageObj.get(y).getArt() == "MC") {

						if (frageObj.get(y).getFlags().is(SymbolType.LIST)) {
							// Erstellt eine Liste
							Vector<ListView<String>> antwortenLIST = new Vector<>();
							// scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
							// allePanel.get(z).add(scrollPane, "span, center");
							ListView<String> liste = new ListView<String>();
							liste.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

							liste.setCellFactory(lv -> {
								ListCell<String> cell = new ListCell<String>() {
									@Override
									protected void updateItem(String item, boolean empty) {
										super.updateItem(item, empty);
										setText(empty ? null : item);
									}
								};

								cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
									if (cell.isEmpty()) {
										return;
									}

									int index = cell.getIndex();
									if (liste.getSelectionModel().getSelectedIndices().contains(index)) {
										liste.getSelectionModel().clearSelection(index);
									} else {
										liste.getSelectionModel().select(index);
									}

									liste.requestFocus();

									e.consume();
								});

								return cell;
							});

							liste.setItems(
									FXCollections.observableArrayList(frageObj.get(y).getAntwort_moeglichkeit()));

							if (frageObj.get(y).getFlags().hasMCReact()) {
								liste.setVisible(false);
							}

							antwortenLIST.add(liste);
							vBox.getChildren().add(liste);
							frageObj.get(y).setAntwortenLIST(antwortenLIST);
							continue;
						}

						Vector<Integer> anzahlZeile = new Vector<Integer>();
						int intAntworten = frageObj.get(y).getAntwort_moeglichkeit().size();
						do {
							if (intAntworten > GlobalVars.proZeile) {
								anzahlZeile.addElement(GlobalVars.proZeile);
								intAntworten -= GlobalVars.proZeile;
							}
						} while (intAntworten > GlobalVars.proZeile);
						anzahlZeile.addElement(intAntworten);

						HBox hBox = new HBox();
						hBox.setAlignment(Pos.CENTER);
						// Uebe die schleife aus, wenn count kleiner ist als die groesse der
						// antwortmoeglichkeiten
						Vector<CheckBox> checkboxs = new Vector<>();
						for (int count3 = 0; count3 < frageObj.get(y).getAntwort_moeglichkeit().size(); count3++) {

							// Erstellt eine Checkbox
							String antwort = frageObj.get(y).getAntwort_moeglichkeit().get(count3);

							String antwortAnzeige = "";
							if (antwort.length() >= 25) {
								int index = antwort.indexOf(" ", 11);
								if (index != -1) {
									char[] string = antwort.toCharArray();
									string[index] = '\n';
									antwortAnzeige = new String(string);
									antwortAnzeige = antwort;
								}
							} else {
								antwortAnzeige = antwort;
							}

							CheckBox chckbxSda = new CheckBox(antwortAnzeige);
							// chckbxSda.setFont(new Font("Tahoma", Font.PLAIN, 28));
							// chckbxSda.setForeground(new Color(94, 56, 41));

							if (frageObj.get(y).getFlags().hasMCReact()) {
								chckbxSda.setVisible(false);
							}
							checkBoxen.add(chckbxSda);
							if (!isMC) {
								chckbxSda.selectedProperty().addListener(new ChangeListener<Boolean>() {
									public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
											Boolean new_val) {
										// System.out.println("!isMC" + ov + " - " + old_val + " - " + new_val);
										if (new_val) {
											for (int i = 0; i < checkBoxen.size(); i++) {
												if (!checkBoxen.get(i).getText().equals(chckbxSda.getText()))
													checkBoxen.get(i).setSelected(false);
											}
										}
									}
								});
								/*
								 * chckbxSda.addActionListener(new ActionListener() {
								 * 
								 * @Override public void actionPerformed(ActionEvent e) { MyCheckbox jCheckBox =
								 * (MyCheckbox) e.getSource(); boolean selected =
								 * jCheckBox.getModel().isSelected(); for(int i = 0; i < checkBoxen.size(); i++)
								 * { checkBoxen.get(i).setSelected(false); } jCheckBox.setSelected(selected);
								 * //String text = jCheckBox.getText();
								 * 
								 * } });
								 */
							} else {
								chckbxSda.selectedProperty().addListener(new ChangeListener<Boolean>() {
									public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
											Boolean new_val) {
										// System.out.println(ov + " - " + old_val + " - " + new_val);
									}
								});
							}

							if (frageObj.get(y).getFlags().is(SymbolType.B)) {
								/*
								 * TODO if(!frageObj.get(y).getFrageLabel().isVisible()) {
								 * lblNull.setVisible(false); lblEins.setVisible(false);
								 * lblZehn.setVisible(false); }
								 */
								if ((count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1)) {
									// allePanel.get(z).add(chckbxSda, "");
									hBox.getChildren().add(chckbxSda);
									// JPanel empty = new JPanel();
									// empty.setVisible(false);
									// allePanel.get(z).add(empty, "wrap");
								} else {
									// allePanel.get(z).add(chckbxSda, "");
									hBox.getChildren().add(chckbxSda);
								}

							} else {
								if ((count3 % (GlobalVars.proZeile)) == (GlobalVars.proZeile - 1)) {
									// allePanel.get(z).add(chckbxSda, "");
									hBox.getChildren().add(chckbxSda);
									/*
									 * JPanel empty = new JPanel(); empty.setVisible(false);
									 * allePanel.get(z).add(empty, "wrap");
									 */
								} else {
									// allePanel.get(z).add(chckbxSda, "");
									hBox.getChildren().add(chckbxSda);
								}
							}
							checkboxs.addElement(chckbxSda);

							/*
							 * if(count3 == frageObj.get(y).getAntwort_moeglichkeit().size() - 1) { for(int
							 * v = 0; v < anzahlZeile.size(); v++) { while(anzahlZeile.get(v) !=
							 * (GlobalVars.proZeile)) { anzahlZeile.set(v, anzahlZeile.get(v) + 1); JPanel
							 * empty = new JPanel(); empty.setVisible(false); if(anzahlZeile.get(v) ==
							 * (GlobalVars.proZeile)) { allePanel.get(z).add(empty, "wrap"); } else {
							 * allePanel.get(z).add(empty, ""); } } } }
							 */
						}
						vBox.getChildren().add(hBox);
						frageObj.get(y).setAntwortenMC(checkboxs);

					} else {
						// Error Nachricht, wenn das obere nicht zutrifft
						// ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " +
						// Thread.currentThread().getStackTrace()[1].getLineNumber(), "Befragung konnte
						// nicht erstellt/ angezeigt werden!");
					}

				}
				fragenJePanel.add(frageObj);

			}

			for (int y = 0; y < fragen.size(); y++) {

				List<React> reacts = fragen.get(y).getFlags().getAll(React.class);
				List<Number> numbers = fragen.get(y).getFlags().getAll(Number.class);
				for (React react : reacts) {
					fragen.get(y).setTarget(
							fragen.get(getY(react.getQuestionId(), react.getQuestionType().toString(), fragen)));
					fragen.get(y).setListener(react.getAnswerPos(), react.getQuestionType().toString());
				}
				for (Number number : numbers) {
					fragen.get(y).setListener(0, number.toString());
				}
			}

			for (int i = 0; i < allePanel.size(); ++i) {
				ScreenController.addScreen("survey_" + i, allePanel.get(i));
			}

			GlobalVars.fragenJePanel = fragenJePanel;
			GlobalVars.countPanel = countPanel;

		} catch (Exception e) {
			// ErrorLog.fehlerBerichtB("ERROR",getClass() + ": " +
			// Thread.currentThread().getStackTrace()[1].getLineNumber(), e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Gibt die Position des "FrageErstellen" Objektes in dem Vector "fragen" zurück
	 * welche die entsprechende Fragen- ID und Fragenart hat. F�r die Vorschau!
	 * <p>
	 * 
	 * @param x
	 *            int: Fragen- ID
	 * @param s
	 *            String: Fragenart
	 * @param fragen
	 *            Vector FrageErstellen: alle Fragen
	 * @return Postition im Vector "fragen" als int.
	 */
	private int getY(int x, String s, Vector<Frage> fragen) {
		// TODO

		for (int i = 0; i < fragen.size(); i++) {
			// System.out.println(fragen.get(i).getFrageID()+ " == " + x + " && " +
			// fragen.get(i).getArt() + " == " + s);
			if (x == fragen.get(i).getFrageID() && s.equals(fragen.get(i).getArt())) {
				return i;
			}
		}
		return -1;
	}

	@FXML
	private void adminLogin() {
		ScreenController.activate(model.Scene.Login.scene(), "toAdmin", true);
	}

	@FXML
	private void next() {
		Vector<Frage> fragen = SurveyService.getFragen(GlobalVars.activFragebogen);
		makeFragebogen(fragen);
		ScreenController.activate("survey_0");
		// fragen.forEach((Frage frage) -> System.out.println(frage));
	}
}
