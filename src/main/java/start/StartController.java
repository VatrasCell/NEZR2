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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import model.Frage;
import model.PanelInfo;
import survey.SurveyController;
import survey.SurveyService;

public class StartController {

	@FXML
	Label lbl_fragebogen;

	@FXML
	Label lbl_warning;
	
	@FXML
	Pane pane;
	
	@FXML
	Button btn_start;

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
		String value;
		switch (GlobalVars.standort) {
		case "Rügen":
			value = "../test/img/logo_nezr.png";
			break;
		case "Bayerischer Wald":
			value = "../test/img/logo_bw.png";
			break;
		case "Saarschleife":
			value = "../test/img/logo_saar.png";
			break;
		case "Schwarzwald":
			value = "../test/img/logo_sw.png";
			break;
		case "Lipno":
			value = "../test/img/logo_lipno_de.png";
			break;

		default:
			value = "../test/img/logo_default.png";
			break;
		}
		
		String image = this.getClass().getResource(value).toExternalForm();
		pane.setStyle("-fx-background-image: url('" + image + "');" +
				"-fx-background-repeat: no-repeat;" +
	  			"-fx-background-attachment: fixed;" +
				"-fx-background-size: 10% auto;" +
	  			"-fx-background-position: 98% 5%;");
		
		setStartText();
		lbl_fragebogen.textProperty().bind(fragebogenText);
		lbl_warning.textProperty().bind(fragebogenWarn);
		lbl_warning.setStyle("-fx-text-fill: #c90000;");
	}

	public static void setStartText() {
		fragebogenText
				.set(GlobalVars.activFragebogen == null ? "" : "Fragebogen: " + GlobalVars.activFragebogen.getName());
		fragebogenWarn.set(GlobalVars.activFragebogen == null ? "kein Fragebogen ausgewählt"
				: !GlobalVars.activFragebogen.getOrt().equals(GlobalVars.standort)
						? "Fragebogen ist nicht für diesen Standort optimiert"
						: "");
	}

	public static void makeFragebogen(Vector<Frage> fragen, boolean isPreview) {
		
		SurveyController.setPreview(isPreview);
		
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
				scene = FXMLLoader.load(StartController.class.getResource("../survey/SurveyView2.fxml"));
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

	private static PanelInfo addQuestionToPanel(Frage frage, Pane screen, PanelInfo info) {
		VBox vBox = (VBox) screen.lookup("#vbox");

		// set headline
		if (!frage.getUeberschrift().equals("") && !info.hasHeadline()) {
			Label lbl_headline = (Label) screen.lookup("#lbl_headline");
			lbl_headline.setText(removeMark(frage.getUeberschrift()));
			info.setHeadline(true);
		}

		//add question
		vBox.getChildren().add(createQuestionLabel(screen, frage));

		if (frage.getArt() == "FF") {
			vBox.getChildren().add(createFFNode(frage));
		} else if (frage.getArt() == "MC") {
			if (frage.getFlags().is(SymbolType.LIST)) {
				vBox.getChildren().add(createMCListView(frage));
			} else {
				vBox.getChildren().add(createMCCheckboxen(frage, info));
			}

		}
		info.setFrage(frage);
		return info;
	}

	private static Label createQuestionLabel(Pane screen, Frage frage) {
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
	private static <T extends Control> T createFFNode(Frage frage) {
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

	private static ListView<String> createMCListView(Frage frage) {
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
	
	private static HBox createMCCheckboxen(Frage frage, PanelInfo info) {
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
		//hBox.setSpacing(32);
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

			// set headlines for choice lists
			if (frage.getFlags().is(SymbolType.B)) {

				if(!info.hasBHeadlines()) {
					Label label = new Label();
					String value = "D\nD";
					switch (chckbxSda.getText()) {
					case "0":
						value = "keine\nAussage";
						break;
					case "1":
						value = "sehr\nschlecht";
						break;
					case "10":
						value = "sehr\ngut";
						break;
					default:
						label.setVisible(false);
					}
					
					label.setText(value);
					label.setTextAlignment(TextAlignment.CENTER);
					//label.setStyle("-fx-font-size: 14.0px;");
					VBox vBox = new VBox(4);
					vBox.getChildren().add(label);
					vBox.getChildren().add(chckbxSda);
					hBox.getChildren().add(vBox);
				} else {
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
		
		info.setbHeadlines(true);
		frage.setAntwortenMC(checkboxs);
		return hBox;
	}

	private static String removeMark(String text) {
		Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
		Matcher ms = MY_PATTERNs.matcher(text);
		if (ms.find()) {
			text = text.substring(0, ms.start());
		}

		return text;
	}

	private static String addRequiredTag(String text, boolean required) {
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
	private static int getY(int x, String s, Vector<Frage> fragen) {
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
		ScreenController.activate(model.Scene.LOGIN, "toAdmin", true);
	}

	@FXML
	private void next() {
		Vector<Frage> fragen = SurveyService.getFragen(GlobalVars.activFragebogen);
		makeFragebogen(fragen, false);
		GlobalVars.page = 0;
		ScreenController.activate("survey_0");
		// fragen.forEach((Frage frage) -> System.out.println(frage));
	}
}
