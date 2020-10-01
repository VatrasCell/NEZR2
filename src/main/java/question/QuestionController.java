package question;

import application.GlobalVars;
import application.ScreenController;
import flag.FlagList;
import flag.Number;
import flag.React;
import flag.SymbolType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import model.FrageEditParam;
import model.Question;
import model.QuestionType;
import model.Questionnaire;
import model.SceneName;
import org.controlsfx.control.Notifications;
import react.ReactController;
import start.StartController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static application.GlobalFuncs.getURL;

public class QuestionController {
	public static Questionnaire questionnaire;
	public static Question question;
	private ArrayList<String> answers;
	private ObservableList<Antwort> data = FXCollections.observableArrayList();
	
	@FXML
	private Label lblQuestion;

	@FXML
	private TextField textFieldFE;
	@FXML
	private TextField textFieldZahl;

	@FXML
	private ChoiceBox<Integer> posChoice;
	@FXML
	private ChoiceBox<String> katChoice;
	@FXML
	private ChoiceBox<String> artChoice;
	@FXML
	private ChoiceBox<String> zahlChoice;

	//@FXML
	//private ImageView imageView;

	@FXML
	private CheckBox chckbxMultipleChoice;
	@FXML
	private CheckBox chckbxListe;
	@FXML
	private CheckBox chckbxTextArea;
	@FXML
	private CheckBox chckbxPflichtfrage;
	@FXML
	private CheckBox chckbxJaNein;
	@FXML
	private CheckBox chckbxUeberschrift;
	@FXML
	private CheckBox chckbxX;
	@FXML
	private CheckBox chckbxZahl;
	
	@FXML
	private Button btnSave;

	@FXML
	private Button btnNewAnswer;

	@FXML
	private TableView<Antwort> tbl_antworten;
	@FXML
	private TableColumn<Antwort, String> nrCol;
	@FXML
	private TableColumn<Antwort, String> antCol;
	@FXML
	private TableColumn<Antwort, String> actionCol = new TableColumn<>("Bearbeiten");
	@FXML
	private TableColumn<Antwort, String> delCol = new TableColumn<>("Löschen");

	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public QuestionController() {
		// fuer die Generierung der Antwortentabelle
		answers = QuestionService.getAnswers(question);
		for (int i = 0; i < Objects.requireNonNull(answers).size(); ++i) {
			Antwort antwort = new Antwort();
			antwort.setNr(i + 1);
			antwort.setAntwort(answers.get(i));
			data.add(antwort);
		}
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		tbl_antworten.setItems(data);

		nrCol.setCellValueFactory(new PropertyValueFactory<>("nr"));
		antCol.setCellValueFactory(new PropertyValueFactory<>("antwort"));

		actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

		Callback<TableColumn<Antwort, String>, TableCell<Antwort, String>> cellFactory = //
				new Callback<TableColumn<Antwort, String>, TableCell<Antwort, String>>() {
					@Override
					public TableCell<Antwort, String> call(final TableColumn<Antwort, String> param) {
						ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
						imgView.setFitHeight(30);
						imgView.setFitWidth(30);
						return new TableCell<Antwort, String>() {

							final Button btn = new Button("", imgView);

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									btn.setOnAction(event -> {
										//TODO edit Antwort
										Antwort antwort = getTableView().getItems().get(getIndex());
									});
									setGraphic(btn);
									setText(null);
								}
							}
						};
					}
				};

		actionCol.setCellFactory(cellFactory);
		tbl_antworten.getColumns().add(actionCol);

		delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
		Callback<TableColumn<Antwort, String>, TableCell<Antwort, String>> cellFactoryDel = //
				new Callback<TableColumn<Antwort, String>, TableCell<Antwort, String>>() {
					@Override
					public TableCell<Antwort, String> call(final TableColumn<Antwort, String> param) {
						ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
						imgView.setFitHeight(30);
						imgView.setFitWidth(30);
						return new TableCell<Antwort, String>() {

							final Button btn = new Button("", imgView);

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									btn.setOnAction(event -> {
										//TODO delete Antwort
										 Antwort antwort = getTableView().getItems().get(getIndex());
									});
									setGraphic(btn);
									setText(null);
								}
							}
						};
					}
				};

		delCol.setCellFactory(cellFactoryDel);
		tbl_antworten.getColumns().add(delCol);
		
		fillScene();
	}

	private void fillScene() {
		String string;
		Pattern MY_PATTERN = Pattern.compile("#\\[[0-9]+]");
		Matcher m = MY_PATTERN.matcher(question.getQuestion());
		if (m.find()) {
			string = question.getQuestion().substring(0, m.start());
		} else {
			string = question.getQuestion();
		}
		textFieldFE.setText(string);
		List<Integer> range = IntStream.range(1, QuestionService.getCountPosition(questionnaire.getId()) + 2).boxed()
				.collect(Collectors.toList());
		ObservableList<Integer> list = FXCollections.observableArrayList(range);
		posChoice.setItems(list);
		posChoice.getSelectionModel().select(question.getPosition() - 1);

		ObservableList<String> listKat = FXCollections.observableArrayList(QuestionService.getCategories());
		katChoice.setItems(listKat);
		katChoice.getSelectionModel().select(question.getCategory());

		ObservableList<String> listArt = FXCollections.observableArrayList("Bewertungsfrage", "Multiple Choice",
				"Freie Frage");
		artChoice.setItems(listArt);
		artChoice.getSelectionModel().select("Freie Frage");
		artChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			artChoice.getSelectionModel().select(newValue.intValue());
			updateCheckboxes();
		});

		ObservableList<String> listZahl = FXCollections.observableArrayList("Genau wie die Zahl", "Kleiner gleich Zahl",
				"Größer gleich Zahl");
		zahlChoice.setItems(listZahl);

		if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
			if (question.getFlags().is(SymbolType.B)) {
				artChoice.getSelectionModel().select("Bewertungsfrage");
			} else {
				artChoice.getSelectionModel().select("Multiple Choice");
			}
		} else {
			artChoice.getSelectionModel().select("Freie Frage");
		}

		if (question.getFlags().is(SymbolType.MC)) {
			chckbxMultipleChoice.setSelected(true);
		} else {
			chckbxMultipleChoice.setSelected(false);
		}

		if (question.getFlags().is(SymbolType.LIST)) {
			chckbxListe.setSelected(true);
		} else {
			chckbxListe.setSelected(false);
		}

		if (question.getFlags().is(SymbolType.TEXT)) {
			chckbxTextArea.setSelected(true);
		} else {
			chckbxTextArea.setSelected(false);
		}

		if (question.getFlags().is(SymbolType.REQUIRED)) {
			chckbxPflichtfrage.setSelected(true);
		} else {
			chckbxPflichtfrage.setSelected(false);
		}

		if (question.getFlags().is(SymbolType.JN)) {
			chckbxJaNein.setSelected(true);
			//imageView.setVisible(true);
			if (question.getFlags().is(SymbolType.JNExcel)) {
				chckbxX.setSelected(true);
				// image.showQRImage();
			} else {
				chckbxX.setSelected(false);
				// image.showImage();
			}
		} else {
			//imageView.setVisible(false);
			chckbxJaNein.setSelected(false);
		}
		/*TODO was macht das?!
		if (frage.getFlags().contains("A")) {
			if (frage.getFlags().contains(" ")) {
				int indexSpace = frage.getFlags().indexOf(" ");
				flags = frage.getFlags().substring(0, indexSpace);
				System.out.println(flags);

			}
		}*/
		if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
			if(question.getAnswerOptions().size() > 0) {
				if (question.getAnswerOptions().get(0).equals("#####")) {
					chckbxUeberschrift.setSelected(true);
				} else {
					chckbxUeberschrift.setSelected(false);
				}
			} else {
				chckbxUeberschrift.setSelected(false);
			}		
		}

		// anneSehrNeu
		/*
		 * answers = new ArrayList<String>(); for (short i = 0; i <
		 * tableFE.getRowCount(); i++) { answers.add(tableFE.getValueAt(i,
		 * 1).toString()); }
		 */

		List<Number> numbers = question.getFlags().getAll(Number.class);

		if (numbers.size() > 0) {
			Number number = numbers.get(0);
			textFieldZahl.setText(number.getDigits() + "");
			chckbxZahl.setSelected(true);
			switch (number.getOperator()) {
			case EQ:
				zahlChoice.getSelectionModel().select("Genau wie die Zahl");
				break;
			case LTE:
				zahlChoice.getSelectionModel().select("Kleiner gleich Zahl");
				break;
			case GTE:
				zahlChoice.getSelectionModel().select("Größer gleich Zahl");
				break;
			}
		} else {
			chckbxZahl.setSelected(false);
		}

		updateCheckboxes();
	}
	
	@FXML
	private void exit() {
		ScreenController.activate(SceneName.QUESTION_LIST);
	}
	
	@FXML
	private void save() {
		if (checkFrageDaten()) {
			Question neueQuestion = new Question();
			
			String string;
			
			String oldFrage = question.getQuestion();
			
			FlagList flags = question.getFlags();
			
			Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+]");
			Matcher ms = MY_PATTERNs.matcher(oldFrage);
			if (ms.find()) {
				string = oldFrage.substring(0, ms.start());
			} else {
				string = oldFrage;
			}
			
			if(!string.equals(textFieldFE.getText())) {
				neueQuestion.setQuestion(QuestionService.duplicateQuestion(textFieldFE.getText()));
			} else {
				neueQuestion.setQuestion(oldFrage);
			}
			oldFrage = "";
			neueQuestion.setPosition((int)posChoice.getValue());
			if(question != null) {
				neueQuestion.setQuestionId(question.getQuestionId());
			}

			FrageEditParam param = new FrageEditParam(artChoice, zahlChoice, textFieldZahl, chckbxPflichtfrage, chckbxMultipleChoice,
					chckbxListe, chckbxTextArea, chckbxJaNein, chckbxUeberschrift, chckbxX, chckbxZahl);

			neueQuestion.setQuestionType(param.getType());
			
			String selectedKat = katChoice.getSelectionModel().getSelectedItem();
			if (selectedKat.equals("")) {
				selectedKat = katChoice.getItems().get(0);
			}						
			neueQuestion.setCategory(selectedKat);
	        
	        List<React> mcList = flags.getAll(React.class);
	        for (React react : mcList) {
	        	if(param.isRequired()) {
					QuestionService.provideQuestionRequired(questionnaire.getId(), react.getQuestionType(), react.getQuestionId());
				}
		        if (QuestionService.isQuestionRequired(questionnaire.getId(), react.getQuestionType(), react.getQuestionId())) {
		        	param.setRequired(true);
		        }
			}
	        
			if (artChoice.getSelectionModel().getSelectedItem().equals("Freie Frage")) {
				QuestionService.getPossibleFlags(flags, param);//floNeu
				neueQuestion.setFlags(flags);
				QuestionService.saveShortAnswerQuestion(questionnaire.getId(), neueQuestion);
			}
			if (artChoice.getSelectionModel().getSelectedItem().equals("Multiple Choice")) {
				ArrayList<Integer> antIds = new ArrayList<>();
				ArrayList<Integer> antIdsRaus = new ArrayList<>();
				ArrayList<String> ants = new ArrayList<>();
				if (param.isYesNoQuestion()) {
					ants.add("Ja");
					ants.add("Nein");
				} else {
					for (int i = 0; i < tbl_antworten.getItems().size(); i++) {
						ants.add(tbl_antworten.getItems().get(i).getAntwort());
					}
				}
				
				//anneSehrNeu
				if(lblQuestion.getText().equals("Frage Bearbeiten")) {
					for(int i = 0; i < answers.size(); i++) {
						for (String ant : ants) {
							if (!answers.isEmpty() && answers.get(i).equals(ant)) {
								answers.remove(i);
							}
						}
					}
				}
			
				if(lblQuestion.getText().equals("Frage Bearbeiten")) {
					for (String answer : answers) {
						int antId = QuestionService.provideAnswerId(answer);
						antIdsRaus.add(antId);
					}
				}
				
				if(!antIdsRaus.isEmpty()) {
					QuestionService.updateFlags(neueQuestion);
					QuestionService.deleteAnswers(antIdsRaus, neueQuestion.getQuestionId());
				}
				//
				for (String ant : ants) {
					int antId = QuestionService.provideAnswerId(ant);
					antIds.add(antId);
				}
				QuestionService.getPossibleFlags(flags, param);//floNeu
				neueQuestion.setFlags(flags);
				QuestionService.saveMultipleChoice(questionnaire.getId(), neueQuestion, antIds);
			}
			if (artChoice.getSelectionModel().getSelectedItem().equals("Bewertungsfrage")) {
				QuestionService.getPossibleFlags(flags, param);//floNeu
				neueQuestion.setFlags(flags);
				QuestionService.saveEvaluationQuestion(questionnaire.getId(), neueQuestion);
			}
		
			ScreenController.activate(SceneName.QUESTION_LIST);
		} else {
			Notifications
					.create()
					.title("Antwort anlegen").text("\"Die Frage ist fehlerhaft und kann deswegen nicht gespeichert werden!")
					.showError();
		}
	}
	
	@FXML
	private void setPreview() {
		StartController.makeQuestionnaire(Collections.singletonList(question), true);
		ScreenController.activate(SceneName.SURVEY_0);
	}
	
	@FXML
	private void createAnswer() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Antwort anlegen");
    	dialog.setContentText("Name:");
    	DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());
		
    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> {
    		Antwort antwort = new Antwort();
    		antwort.setNr(data.size() + 1);
    		antwort.setAntwort(name);
    		data.add(antwort);
        	Notifications.create().title("Antwort anlegen").text("Antwort \"" + name + "\" wurde erfolgreich angelegt.").show();
    	});
		
	}
	
	@FXML
	private void createCategory() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Kategorie anlegen");
    	dialog.setContentText("Name:");
    	DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());
		
    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> {
    		if(QuestionService.provideCategory(name)) {
    			ObservableList<String> listKat = FXCollections.observableArrayList(QuestionService.getCategories());
    			katChoice.setItems(listKat);
    			katChoice.getSelectionModel().select(name);
        		Notifications.create().title("Kategorie anlegen").text("Kategorie \"" + name + "\" wurde erfolgreich angelegt.").show();
    		} else {
    			Notifications.create().title("Kategorie anlegen").text("Ein Fehler ist aufgetreten.").showError();
    		}
    	});
	}
	
	@FXML
	private void react() {
		ReactController.question = question;
		ReactController.questionnaire = questionnaire;
		
		try {
			ScreenController.addScreen(SceneName.REACT, FXMLLoader.load(getURL(SceneName.REACT_PATH)));
			ScreenController.activate(SceneName.REACT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Ueberprueft ob alle Bedingungen zum Speichern gegeben sind.
	 */
	public boolean checkFrageDaten() {
		//TODO Button nicht hier blockieren; dynamische Validierung?!
		System.out.println(textFieldFE.getText());
		System.out.println(artChoice.getSelectionModel().getSelectedItem());
		if(!textFieldFE.getText().equals("")) {
			if(!artChoice.getSelectionModel().getSelectedItem().equals("-- Art der Frage --")) {
				if((tbl_antworten.getItems().size()<1 || chckbxJaNein.isSelected())&& artChoice.getSelectionModel().getSelectedItem().equals("Multiple Choice")) { //anneSehrNeu ? weiß nicht mehr
					btnSave.setDisable(true);
				} else {
					btnSave.setDisable(false);
				}
			} else {
				btnSave.setDisable(true);
			}
		} else {
			btnSave.setDisable(true);
		}


		return !btnSave.isDisabled();
	}

	@FXML
	private void updateCheckboxes() {
		FrageEditParam param = new FrageEditParam(artChoice, zahlChoice, textFieldZahl, chckbxPflichtfrage, chckbxMultipleChoice,
				chckbxListe, chckbxTextArea, chckbxJaNein, chckbxUeberschrift, chckbxX, chckbxZahl);

		artChoice.setDisable(!param.isTypeActivatable());
		if(artChoice.isDisabled()) {
			artChoice.getSelectionModel().select(1);
		}

		zahlChoice.setDisable(!param.isNumberTypeActivatable());
		if(zahlChoice.isDisabled()) {
			zahlChoice.getSelectionModel().clearSelection();
		}

		textFieldZahl.setDisable(!param.isCountCharsActivatable());
		if(textFieldZahl.isDisabled()) {
			textFieldZahl.clear();
		}

		chckbxPflichtfrage.setDisable(!param.isRequiredActivatable());
		if(chckbxPflichtfrage.isDisabled()) {
			chckbxPflichtfrage.setSelected(false);
		}

		chckbxMultipleChoice.setDisable(!param.isMultipleChoiceActivatable());
		if(chckbxMultipleChoice.isDisabled()) {
			chckbxMultipleChoice.setSelected(false);
		}

		chckbxListe.setDisable(!param.isListActivatable());
		if(chckbxListe.isDisabled()) {
			chckbxListe.setSelected(false);
		}

		chckbxTextArea.setDisable(!param.isTextareaActivatable());
		if(chckbxTextArea.isDisabled()) {
			chckbxTextArea.setSelected(false);
		}

		chckbxJaNein.setDisable(!param.isYesNoQuestionActivatable());
		if(chckbxJaNein.isDisabled()) {
			chckbxJaNein.setSelected(false);
		}

		chckbxX.setDisable(!param.isSingleLineActivatable());
		if(chckbxX.isDisabled()) {
			chckbxX.setSelected(false);
		}

		chckbxZahl.setDisable(!param.isNumericActivatable());
		if(chckbxZahl.isDisabled()) {
			chckbxZahl.setSelected(false);
		}

		tbl_antworten.setDisable(!param.isAnswersListActivatable());
		btnNewAnswer.setDisable(!param.isAnswersListActivatable());
	}

	public class Antwort {
		private int nr;
		private String antwort;

		public int getNr() {
			return nr;
		}

		public void setNr(int nr) {
			this.nr = nr;
		}

		public String getAntwort() {
			return antwort;
		}

		public void setAntwort(String antwort) {
			this.antwort = antwort;
		}

	}
}
