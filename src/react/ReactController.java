package react;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import application.ScreenController;
import flag.Flag;
import flag.FlagList;
import flag.QuestionType;
import flag.React;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Pair;
import model.Frage;
import model.Fragebogen;
import question.QuestionController;
import survey.SurveyService;

public class ReactController {

	public static Frage frage;
	public static Vector<Frage> fragen;
	public static Fragebogen fragebogen;
	
	private final int MAX_COUNT_REACTIONS = 1;
	private final String REACT_DESCRIPTION = "...die Antwortmöglichkeit \"{1}\" der Frage \"{0}\" ausgewählt wurde.";

	private ObservableList<ReactTableElement> data = FXCollections.observableArrayList();
	private ObservableList<Frage> fragenData = FXCollections.observableArrayList();
	private ObservableList<String> antwortData = FXCollections.observableArrayList();
	
	private boolean hasQuestion = false;
	private boolean hasAnswer = false;
	
	@FXML
	private Button btn_new;

	@FXML
	private TableView<ReactTableElement> tbl_react;
	@FXML
	private TableColumn<ReactTableElement, String> questions;
	@FXML
	private TableColumn<ReactTableElement, String> answers;
	@FXML
	private TableColumn<ReactTableElement, String> comments;
	@FXML
	private TableColumn<ReactTableElement, String> delCol = new TableColumn<>("Löschen");

	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public ReactController() {
		FlagList flags = frage.getFlags();
		fragen = SurveyService.getFragen(fragebogen);

		for (React react : flags.getAll(React.class)) {
			Frage frage = fragen.get(getY(react.getQuestionId(), react.getQuestionType().toString(), fragen));
			data.add(new ReactTableElement(frage, react.getAnswerPos()));
		}

		fragenData.addAll(fragen);
		for (int i = 0; i < fragen.size(); ++i) {
			if(fragen.get(i).getArt().equals(frage.getArt()) && fragen.get(i).getFrageID() == frage.getFrageID()) {	
				fragenData.remove(i);
			}
		}
	}

	/**
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

		tbl_react.setItems(data);

		questions.setCellValueFactory(new PropertyValueFactory<ReactTableElement, String>("question"));
		answers.setCellValueFactory(new PropertyValueFactory<ReactTableElement, String>("answer"));
		comments.setCellValueFactory(new PropertyValueFactory<ReactTableElement, String>("comment"));
		comments.setCellFactory(
				new Callback<TableColumn<ReactTableElement, String>, TableCell<ReactTableElement, String>>() {

					@Override
					public TableCell<ReactTableElement, String> call(TableColumn<ReactTableElement, String> param) {
						TableCell<ReactTableElement, String> cell = new TableCell<>();
						Text text = new Text();
						cell.setGraphic(text);
						cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
						text.wrappingWidthProperty().bind(cell.widthProperty());
						text.textProperty().bind(cell.itemProperty());
						return cell;
					}

				});

		delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
		Callback<TableColumn<ReactTableElement, String>, TableCell<ReactTableElement, String>> cellFactoryDel = //
				new Callback<TableColumn<ReactTableElement, String>, TableCell<ReactTableElement, String>>() {
					@Override
					public TableCell<ReactTableElement, String> call(
							final TableColumn<ReactTableElement, String> param) {
						final TableCell<ReactTableElement, String> cell = new TableCell<ReactTableElement, String>() {

							final Button btn = new Button("DEL");

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									btn.setOnAction(event -> {
										ReactTableElement reactTableElement = getTableView().getItems().get(getIndex());
										
										for(int i = 0; i < frage.getFlags().getAll(React.class).size(); ++i) {
											System.out.println(frage.getFlags().getAll(React.class).get(i).toString() + " - " + reactTableElement.getFlag().toString());
											if(frage.getFlags().getAll(React.class).get(i).toString().equals(reactTableElement.getFlag().toString())) {
												List<React> reacts = frage.getFlags().getAll(React.class);
												reacts.remove(i);
												frage.getFlags().replaceAll(React.class, reacts);
											}
										}
									
										data.remove(reactTableElement);
									});
									setGraphic(btn);
									setText(null);
								}
							}
						};
						return cell;
					}
				};

		delCol.setCellFactory(cellFactoryDel);
		tbl_react.getColumns().add(delCol);
		
		ObservableList<Flag> flagData = FXCollections.observableArrayList();
		flagData.setAll(frage.getFlags().getAll(React.class));
		BooleanBinding invalid = Bindings.size(flagData).greaterThanOrEqualTo(MAX_COUNT_REACTIONS);
		
		btn_new.disableProperty().bind(invalid);
	}

	@FXML
	private void createNew() {
		Dialog<Pair<React, ReactTableElement>> dialog = new Dialog<>();
		dialog.setTitle("Neu Bedinung auswählen");
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
		final Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
		okButton.setDisable(true);
		dialog.setResizable(true);
		dialog.getDialogPane().getStylesheets()
				.add(ScreenController.class.getResource(ScreenController.styleSheet).toExternalForm());

		HBox hBox = new HBox(8);

		ComboBox<Frage> question = new ComboBox<Frage>();
		question.valueProperty().addListener(new ChangeListener<Frage>() {
			@Override
			public void changed(ObservableValue ov, Frage oldFrage, Frage newFrage) {
				if(newFrage != null) {
					hasQuestion = true;
					okButton.setDisable(!(hasQuestion && hasAnswer));
					antwortData.clear();
					for (String antwort : newFrage.getAntwort_moeglichkeit()) {
						if(antwort.equals("")) {
							antwort = "<Textfeld>";
						}
						antwortData.add(antwort);
					}
				} else {
					hasQuestion = false;
				}
			}
		});

		question.setItems(fragenData);
		ComboBox<String> answer = new ComboBox<String>();
		answer.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String oldAntwort, String newAntwort) {
				if(newAntwort != null) {
					hasAnswer = true;
					okButton.setDisable(!(hasQuestion && hasAnswer));
				} else {
					hasAnswer = false;
				}
			}
		});
		answer.setItems(antwortData);
		hBox.getChildren().add(new Label("Frage:"));
		hBox.getChildren().add(question);
		hBox.getChildren().add(new Label("Antwort:"));
		hBox.getChildren().add(answer);

		dialog.getDialogPane().setContent(hBox);
		
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				Frage frage = question.getSelectionModel().getSelectedItem();
				int answerPos = answer.getSelectionModel().getSelectedIndex();
				React react = new React(QuestionType.valueOf(frage.getArt()), frage.getFrageID(), answerPos);
				ReactTableElement tableElement = new ReactTableElement(frage, answerPos, react);
				Pair<React, ReactTableElement> result = new Pair<React, ReactTableElement>(react, tableElement);
				return result;
			}
			return null;
		});

		Optional<Pair<React, ReactTableElement>> result = dialog.showAndWait();
		result.ifPresent(pair -> {
			frage.getFlags().add(pair.getKey());
			data.add(pair.getValue());
		});
	}
	
	@FXML
	private void save() {
		QuestionController.frage = frage;
		ScreenController.activate(model.Scene.QUESTION);
	}

	@FXML
	private void exit() {
		ScreenController.activate(model.Scene.QUESTION);
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
			if (x == fragen.get(i).getFrageID() && s.equals(fragen.get(i).getArt())) {
				return i;
			}
		}
		return -1;
	}

	public class ReactTableElement {
		private String question;
		private String answer;
		private String comment;
		private React flag;
		
		public ReactTableElement(Frage frage, int answerPos) {
			super();
			this.question = frage.getFrage();
			this.answer = frage.getAntwort_moeglichkeit().get(answerPos);
			this.comment = MessageFormat.format(REACT_DESCRIPTION, question, answer);
			this.flag = getFlagFromFlagList(frage, answerPos);
		}
		
		public ReactTableElement(Frage frage, int answerPos, React flag) {
			super();
			this.question = frage.getFrage();
			this.answer = frage.getAntwort_moeglichkeit().get(answerPos);
			this.comment = MessageFormat.format(REACT_DESCRIPTION, question, answer);
			this.flag = flag;
		}

		/**
		 * @return the question
		 */
		public String getQuestion() {
			return question;
		}

		/**
		 * @param question
		 *            the question to set
		 */
		public void setQuestion(String question) {
			this.question = question;
		}

		/**
		 * @return the answer
		 */
		public String getAnswer() {
			return answer;
		}

		/**
		 * @param answer
		 *            the answer to set
		 */
		public void setAnswer(String answer) {
			this.answer = answer;
		}

		/**
		 * @return the comment
		 */
		public String getComment() {
			return comment;
		}

		/**
		 * @param comment
		 *            the comment to set
		 */
		public void setComment(String comment) {
			this.comment = comment;
		}
		
		/**
		 * @return the flag
		 */
		public React getFlag() {
			return flag;
		}

		/**
		 * @param flag the flag to set
		 */
		public void setFlag(React flag) {
			this.flag = flag;
		}

		private React getFlagFromFlagList(Frage frage, int answerPos) {
			for (React react : frage.getFlags().getAll(React.class)) {
				if(react.getQuestionType().equals(QuestionType.valueOf(frage.getArt())) && 
						react.getQuestionId() == frage.getFrageID() && 
						react.getAnswerPos() == answerPos) {
							return react;
						}
			}
			
			return null;
		}

	}
}
