package start;

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
import model.PanelInfo;
import model.Question;
import model.QuestionType;
import model.SceneName;
import questionList.QuestionListService;
import survey.SurveyController;
import survey.SurveyService;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static application.GlobalFuncs.getURL;

public class StartController {

	@FXML
	Label lbl_fragebogen;

	@FXML
	Label lbl_warning;
	
	@FXML
	Pane pane;
	
	@FXML
	Button btn_start;

	private static StringProperty questionnaireText = new SimpleStringProperty();
	private static StringProperty questionnaireWarn = new SimpleStringProperty();

	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public StartController() {
		GlobalVars.activeQuestionnaire = StartService.getActiveQuestionnaire();
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		String value;
		switch (GlobalVars.location) {
		case "Rügen":
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
		
		String image = this.getClass().getClassLoader().getResource(value).toExternalForm();
		pane.setStyle("-fx-background-image: url('" + image + "');" +
				"-fx-background-repeat: no-repeat;" +
	  			"-fx-background-attachment: fixed;" +
				"-fx-background-size: 10% auto;" +
	  			"-fx-background-position: 98% 5%;");
		
		setStartText();
		lbl_fragebogen.textProperty().bind(questionnaireText);
		lbl_warning.textProperty().bind(questionnaireWarn);
		lbl_warning.setStyle("-fx-text-fill: #c90000;");
	}

	public static void setStartText() {
		questionnaireText
				.set(GlobalVars.activeQuestionnaire == null ? "" : "Fragebogen: " + GlobalVars.activeQuestionnaire.getName());
		questionnaireWarn.set(GlobalVars.activeQuestionnaire == null ? "kein Fragebogen ausgewählt"
				: !GlobalVars.activeQuestionnaire.getOrt().equals(GlobalVars.location)
						? "Fragebogen ist nicht für diesen Standort optimiert"
						: "");
	}

	public static void makeQuestionnaire(List<Question> questions, boolean isPreview) {
		
		SurveyController.setPreview(isPreview);
		
		Deque<Question> stack = new ArrayDeque<>();

		for (int v = questions.size() - 1; v >= 0; v--) {
			stack.push(questions.get(v));
		}

		List<ArrayList<Question>> fragenJePanel = new ArrayList<>();
		List<Pane> allePanel = new ArrayList<>();
		do {
			int questionsOnPanel = 0;
			String lastKat = "";
			// create panel
			Pane scene = null;
			try {
				scene = FXMLLoader.load(getURL(SceneName.SURVEY_PATH));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (scene == null)
				throw new NullPointerException();
			PanelInfo info = new PanelInfo();
			fragenJePanel.add(new ArrayList<>());
			do {
				Question question = stack.peek();
				if(lastKat.equals("") || lastKat.equals(question.getCategory())) {
					question = stack.pop();
					addQuestionToPanel(question, scene, info);
					fragenJePanel.get(allePanel.size()).add(info.getQuestion());
					lastKat = question.getCategory();
					if (stack.isEmpty())
						break;
					
				} else {
					break;
				}
			} while (++questionsOnPanel < GlobalVars.perColumn);
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
		for (int y = 0; y < questions.size(); y++) {

			List<React> reacts = questions.get(y).getFlags().getAll(React.class);
			List<Number> numbers = questions.get(y).getFlags().getAll(Number.class);
			for (React react : reacts) {
				questions.get(y).setTarget(
						questions.get(getY(react.getQuestionId(), react.getQuestionType(), questions)));
				questions.get(y).setListener(react.getAnswerPos(), react.getQuestionType());
			}
			for (Number number : numbers) {
				questions.get(y).setListener(number);
			}
		}

		GlobalVars.questionsPerPanel = fragenJePanel;
		GlobalVars.countPanel = allePanel.size();
	}

	private static void addQuestionToPanel(Question question, Pane screen, PanelInfo info) {
		VBox vBox = (VBox) screen.lookup("#vbox");

		// set headline
		if (!question.getHeadline().equals("") && !info.hasHeadline()) {
			Label lbl_headline = (Label) screen.lookup("#lbl_headline");
			lbl_headline.setText(removeMark(question.getHeadline().getName()));
			info.setHeadline(true);
		}

		//add question
		vBox.getChildren().add(createQuestionLabel(screen, question));

		if (question.getQuestionType().equals(QuestionType.SHORT_ANSWER)) {
			vBox.getChildren().add(createFFNode(question));
		} else if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
			if (question.getFlags().is(SymbolType.LIST)) {
				vBox.getChildren().add(createMCListView(question));
			} else {
				vBox.getChildren().add(createMCCheckboxen(question, info));
			}

		}
		info.setQuestion(question);
	}

	private static Label createQuestionLabel(Pane screen, Question question) {
		String questionTest = removeMark(question.getQuestion());

		questionTest = addRequiredTag(questionTest, question.getFlags().is(SymbolType.REQUIRED));

		Label lblFrage = new Label(questionTest);
		// System.out.println("frageObj.get(y).frageid = " +
		// frageObj.get(y).getFrageID());
		lblFrage.setId("lblFrage_" + question.getQuestionId());

		if (question.getFlags().hasMCReact()) {
			//lblFrage.setVisible(false);
		}

		// allePanel.get(z).add(lblFrage, "align center, span, wrap");
		question.setScene(screen);
		question.setQuestionLabel(lblFrage);

		return lblFrage;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Control> T createFFNode(Question question) {
		if (question.getFlags().is(SymbolType.TEXT)) {
			// F�gt eine Textarea ein
			TextArea textArea = new TextArea(); // anneSuperNeu
			// textArea.setPreferredSize(new Dimension(200, 50));
			// allePanel.get(z).add(textArea, "span, center");
			ArrayList<TextArea> textAreas = new ArrayList<>();
			textAreas.add(textArea);
			question.setAnswersTEXT(textAreas);
			return (T) textArea;
		} else {
			if (question.getFlags().is(SymbolType.LIST)) {
				// ErrorLog.fehlerBerichtB("ERROR",
				// Datenbank.class + ": " +
				// Thread.currentThread().getStackTrace()[1].getLineNumber(), "Fehler");
			} else {
				// F�gt ein Textfeld ein
				TextField textField = new TextField();
				// textField.setPreferredSize(new Dimension(200, 50));

				if (question.getFlags().hasFFReact()) {
					textField.setVisible(false);
				}

				// allePanel.get(z).add(textField, "wrap, span, center");
				ArrayList<TextField> textFields = new ArrayList<>();
				textFields.add(textField);
				question.setAnswersFF(textFields);
				return (T) textField;
			}
		}

		return null;
	}

	private static ListView<String> createMCListView(Question question) {
		// Erstellt eine Liste
		ArrayList<ListView<String>> antwortenLIST = new ArrayList<>();
		// scrollPane.getVerticalScrollBar().setUI(new MyScrollBarUI());
		// allePanel.get(z).add(scrollPane, "span, center");
		ListView<String> liste = new ListView<>();
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

		liste.setItems(FXCollections.observableArrayList(question.getAnswerOptions()));

		if (question.getFlags().hasMCReact()) {
			//liste.setVisible(false);
		}

		antwortenLIST.add(liste);
		question.setAnswersLIST(antwortenLIST);
		return liste;	
	}
	
	private static HBox createMCCheckboxen(Question question, PanelInfo info) {
		List<CheckBox> checkBoxen = new ArrayList<>();
		List<Integer> anzahlZeile = new ArrayList<>();
		int intAntworten = question.getAnswerOptions().size();
		do {
			if (intAntworten > GlobalVars.perColumn) {
				anzahlZeile.add(GlobalVars.perColumn);
				intAntworten -= GlobalVars.perColumn;
			}
		} while (intAntworten > GlobalVars.perColumn);
		anzahlZeile.add(intAntworten);

		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER);
		//hBox.setSpacing(32);
		// Uebe die schleife aus, wenn count kleiner ist als die groesse der
		// antwortmoeglichkeiten
		ArrayList<CheckBox> checkboxs = new ArrayList<>();
		
		for (int count3 = 0; count3 < question.getAnswerOptions().size(); count3++) {

			// Erstellt eine Checkbox
			String antwort = question.getAnswerOptions().get(count3);

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

			if (question.getFlags().hasMCReact()) {
				chckbxSda.setVisible(false);
			}
			checkBoxen.add(chckbxSda);
			if (!question.getFlags().is(SymbolType.MC)) {
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
			if (question.getFlags().is(SymbolType.B)) {

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
				if ((count3 % (GlobalVars.perColumn)) == (GlobalVars.perColumn - 1)) {
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
			checkboxs.add(chckbxSda);

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
		question.setAnswersMC(checkboxs);
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
	 * Gibt die Position des "FrageErstellen" Objektes in dem ArrayList "questions" zurück
	 * welche die entsprechende Fragen- ID und Fragenart hat. F�r die Vorschau!
	 * <p>
	 * 
	 * @param x
	 *            int: Fragen- ID
	 * @param type
	 *            String: Fragenart
	 * @param questions
	 *            ArrayList FrageErstellen: alle Fragen
	 * @return Postition im ArrayList "questions" als int.
	 */
	private static int getY(int x, QuestionType type, List<Question> questions) {
		// TODO

		for (int i = 0; i < questions.size(); i++) {
			// System.out.println(questions.get(i).getFrageID()+ " == " + x + " && " +
			// questions.get(i).getArt() + " == " + type);
			if (x == questions.get(i).getQuestionId() && type.equals(questions.get(i).getQuestionType())) {
				return i;
			}
		}
		return -1;
	}

	@FXML
	private void adminLogin() {
		ScreenController.activate(SceneName.LOGIN, "toAdmin", true);
	}

	@FXML
	private void next() {
		List<Question> questions = QuestionListService.getQuestions(GlobalVars.activeQuestionnaire.getId());
		makeQuestionnaire(questions, false);
		GlobalVars.page = 0;
		ScreenController.activate("survey_0");
		// questions.forEach((Frage frage) -> System.out.println(frage));
	}
}
