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
import model.Answer;
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
    private ObservableList<Answer> data = FXCollections.observableArrayList();

    @FXML
    private Label questionLabel;

    @FXML
    private TextField questionTextField;
    @FXML
    private TextField numberTextField;

    @FXML
    private ChoiceBox<Integer> positionChoiceBox;
    @FXML
    private ChoiceBox<String> categoryChoiceBox;
    @FXML
    private ChoiceBox<String> questionTypeChoiceBox;
    @FXML
    private ChoiceBox<String> numberChoiceBox;

    //@FXML
    //private ImageView imageView;

    @FXML
    private CheckBox multipleChoiceCheckBox;
    @FXML
    private CheckBox listCheckBox;
    @FXML
    private CheckBox textAreaCheckBox;
    @FXML
    private CheckBox requiredQuestionCheckBox;
    @FXML
    private CheckBox yesNoCheckBox;
    @FXML
    private CheckBox headlineCheckBox;
    @FXML
    private CheckBox excelFormatCheckBox;
    @FXML
    private CheckBox numberCheckBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button newAnswerButton;

    @FXML
    private TableView<Answer> answerTable;
    @FXML
    private TableColumn<Answer, String> answerIdTableColumn;
    @FXML
    private TableColumn<Answer, String> answerValueTableColumn;
    @FXML
    private TableColumn<Answer, String> actionCol = new TableColumn<>("Bearbeiten");
    @FXML
    private TableColumn<Answer, String> delCol = new TableColumn<>("Löschen");

    /**
     * The constructor (is called before the initialize()-method).
     */
    public QuestionController() {
        // fuer die Generierung der Antwortentabelle
        answers = QuestionService.getAnswers(question);
        for (int i = 0; i < Objects.requireNonNull(answers).size(); ++i) {
            Answer antwort = new Answer();
            antwort.setId(i + 1);
            antwort.setValue(answers.get(i));
            data.add(antwort);
        }
    }

    /**
     * Initializes the controller class. This method is automatically called after
     * the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        answerTable.setItems(data);

        answerIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        answerValueTableColumn.setCellValueFactory(new PropertyValueFactory<>("antwort"));

        actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Answer, String>, TableCell<Answer, String>> cellFactory = //
                new Callback<TableColumn<Answer, String>, TableCell<Answer, String>>() {
                    @Override
                    public TableCell<Answer, String> call(final TableColumn<Answer, String> param) {
                        ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
                        imgView.setFitHeight(30);
                        imgView.setFitWidth(30);
                        return new TableCell<Answer, String>() {

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
                                        Answer antwort = getTableView().getItems().get(getIndex());
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };

        actionCol.setCellFactory(cellFactory);
        answerTable.getColumns().add(actionCol);

        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Answer, String>, TableCell<Answer, String>> cellFactoryDel = //
                new Callback<TableColumn<Answer, String>, TableCell<Answer, String>>() {
                    @Override
                    public TableCell<Answer, String> call(final TableColumn<Answer, String> param) {
                        ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
                        imgView.setFitHeight(30);
                        imgView.setFitWidth(30);
                        return new TableCell<Answer, String>() {

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
                                        Answer antwort = getTableView().getItems().get(getIndex());
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };

        delCol.setCellFactory(cellFactoryDel);
        answerTable.getColumns().add(delCol);

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
        questionTextField.setText(string);
        List<Integer> range = IntStream.range(1, QuestionService.getCountPosition(questionnaire.getId()) + 2).boxed()
                .collect(Collectors.toList());
        ObservableList<Integer> list = FXCollections.observableArrayList(range);
        positionChoiceBox.setItems(list);
        positionChoiceBox.getSelectionModel().select(question.getPosition() - 1);

        ObservableList<String> listKat = FXCollections.observableArrayList(QuestionService.getCategories());
        categoryChoiceBox.setItems(listKat);
        categoryChoiceBox.getSelectionModel().select(question.getCategory());

        ObservableList<String> listArt = FXCollections.observableArrayList("Bewertungsfrage", "Multiple Choice",
                "Freie Frage");
        questionTypeChoiceBox.setItems(listArt);
        questionTypeChoiceBox.getSelectionModel().select("Freie Frage");
        questionTypeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            questionTypeChoiceBox.getSelectionModel().select(newValue.intValue());
            updateCheckboxes();
        });

        ObservableList<String> listZahl = FXCollections.observableArrayList("Genau wie die Zahl", "Kleiner gleich Zahl",
                "Größer gleich Zahl");
        numberChoiceBox.setItems(listZahl);

        if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
            if (question.getFlags().is(SymbolType.B)) {
                questionTypeChoiceBox.getSelectionModel().select("Bewertungsfrage");
            } else {
                questionTypeChoiceBox.getSelectionModel().select("Multiple Choice");
            }
        } else {
            questionTypeChoiceBox.getSelectionModel().select("Freie Frage");
        }

        if (question.getFlags().is(SymbolType.MC)) {
            multipleChoiceCheckBox.setSelected(true);
        } else {
            multipleChoiceCheckBox.setSelected(false);
        }

        if (question.getFlags().is(SymbolType.LIST)) {
            listCheckBox.setSelected(true);
        } else {
            listCheckBox.setSelected(false);
        }

        if (question.getFlags().is(SymbolType.TEXT)) {
            textAreaCheckBox.setSelected(true);
        } else {
            textAreaCheckBox.setSelected(false);
        }

        if (question.getFlags().is(SymbolType.REQUIRED)) {
            requiredQuestionCheckBox.setSelected(true);
        } else {
            requiredQuestionCheckBox.setSelected(false);
        }

        if (question.getFlags().is(SymbolType.JN)) {
            yesNoCheckBox.setSelected(true);
            //imageView.setVisible(true);
            if (question.getFlags().is(SymbolType.JNExcel)) {
                excelFormatCheckBox.setSelected(true);
                // image.showQRImage();
            } else {
                excelFormatCheckBox.setSelected(false);
                // image.showImage();
            }
        } else {
            //imageView.setVisible(false);
            yesNoCheckBox.setSelected(false);
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
            if (question.getAnswerOptions().size() > 0) {
                if (question.getAnswerOptions().get(0).equals("#####")) {
                    headlineCheckBox.setSelected(true);
                } else {
                    headlineCheckBox.setSelected(false);
                }
            } else {
                headlineCheckBox.setSelected(false);
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
            numberTextField.setText(number.getDigits() + "");
            numberCheckBox.setSelected(true);
            switch (number.getOperator()) {
                case EQ:
                    numberChoiceBox.getSelectionModel().select("Genau wie die Zahl");
                    break;
                case LTE:
                    numberChoiceBox.getSelectionModel().select("Kleiner gleich Zahl");
                    break;
                case GTE:
                    numberChoiceBox.getSelectionModel().select("Größer gleich Zahl");
                    break;
            }
        } else {
            numberCheckBox.setSelected(false);
        }

        updateCheckboxes();
    }

    @FXML
    private void exit() {
        ScreenController.activate(SceneName.QUESTION_LIST);
    }

    @FXML
    private void save() {
        if (!checkQuestionData()) {
            Notifications
                    .create()
                    .title("Antwort anlegen").text("\"Die Frage ist fehlerhaft und kann deswegen nicht gespeichert werden!")
                    .showError();
            return;
        }

        Question questionToSave = new Question();

        String oldQuestion = question.getQuestion();
        String newQuestion = questionTextField.getText();

        FlagList flags = question.getFlags();

        FrageEditParam param = new FrageEditParam(questionTypeChoiceBox, numberChoiceBox, numberTextField,
				requiredQuestionCheckBox, multipleChoiceCheckBox, listCheckBox, textAreaCheckBox, yesNoCheckBox,
				headlineCheckBox, excelFormatCheckBox, numberCheckBox);

        questionToSave.setQuestionType(param.getType());

        String selectedKat = categoryChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedKat.equals("")) {
            selectedKat = categoryChoiceBox.getItems().get(0);
        }
        questionToSave.setCategory(selectedKat);

        Integer questionId;
        if (!newQuestion.equals(oldQuestion)) {
            if (questionToSave.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                questionId = QuestionService.getMultipleChoiceId(newQuestion);
                if (questionId != null) {
                    if (QuestionService.getMultipleChoiceQuestionnaireRelationId(questionnaire.getId(), questionId) != null) {
                        Notifications
                                .create()
                                .title("Frage anlegen/ bearbeiten").text("\"Eine gleiche Frage existiert bereits in diesem Fragebogen und kann deswegen nicht gespeichert werden!")
                                .showError();
                        return;
                    }
                }
            } else {
                questionId = QuestionService.getShortAnswerId(newQuestion);
                if (questionId != null) {
                    if (QuestionService.getShortAnswerQuestionnaireRelationId(questionnaire.getId(), questionId) != null) {
                        Notifications
                                .create()
                                .title("Frage anlegen/ bearbeiten").text("\"Eine gleiche Frage existiert bereits in diesem Fragebogen und kann deswegen nicht gespeichert werden!")
                                .showError();
                        return;
                    }
                }
            }
        }

        questionToSave.setQuestion(newQuestion);

        questionToSave.setPosition(positionChoiceBox.getValue());

        List<React> mcList = flags.getAll(React.class);
        for (React react : mcList) {
            if (param.isRequired()) {
                QuestionService.provideQuestionRequired(questionnaire.getId(), react.getQuestionType(), react.getQuestionId());
            }
            if (QuestionService.isQuestionRequired(questionnaire.getId(), react.getQuestionType(), react.getQuestionId())) {
                param.setRequired(true);
            }
        }

        if (param.getType() == QuestionType.MULTIPLE_CHOICE) {
            if (param.isValuationAsk()) {
                QuestionService.getPossibleFlags(flags, param);
                questionToSave.setFlags(flags);
                QuestionService.saveEvaluationQuestion(questionnaire.getId(), questionToSave);
            } else {
            	ArrayList<Answer> answers = new ArrayList<>();
                ArrayList<Integer> answerIdsToDelete = new ArrayList<>();

                if (param.isYesNoQuestion()) {
                    answers.add(new Answer("Ja"));
                    answers.add(new Answer("Nein"));
                } else {
					answers.addAll(answerTable.getItems());
                }

                if (questionLabel.getText().equals("Frage Bearbeiten")) {
                    for (int i = 0; i < this.answers.size(); i++) {
                        for (Answer answer : answers) {
                            if (!this.answers.isEmpty() && this.answers.get(i).equals(answer.getValue())) {
                                this.answers.remove(i);
                            }
                        }
                    }

					answerIdsToDelete = this.answers.stream()
							.map(QuestionService::getAnswerId)
							.filter(Objects::nonNull)
							.collect(Collectors.toCollection(ArrayList::new));
                }

                if (!answerIdsToDelete.isEmpty()) {
                    QuestionService.updateFlags(questionnaire.getId(), questionToSave);
                    QuestionService.deleteAnswers(answerIdsToDelete, questionToSave.getQuestionId());
                }

				answers.forEach(answer ->
						answer.setId(Objects.requireNonNull(QuestionService.getAnswerId(answer.getValue())))
				);

                QuestionService.getPossibleFlags(flags, param);
                questionToSave.setFlags(flags);
                QuestionService.saveMultipleChoice(questionnaire.getId(), questionToSave, answers);
            }
        } else {
            QuestionService.getPossibleFlags(flags, param);//floNeu
            questionToSave.setFlags(flags);
            QuestionService.saveShortAnswerQuestion(questionnaire.getId(), questionToSave);
        }

        ScreenController.activate(SceneName.QUESTION_LIST);

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
            Answer antwort = new Answer();
            antwort.setId(data.size() + 1);
            antwort.setValue(name);
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
            if (QuestionService.provideCategory(name)) {
                ObservableList<String> listKat = FXCollections.observableArrayList(QuestionService.getCategories());
                categoryChoiceBox.setItems(listKat);
                categoryChoiceBox.getSelectionModel().select(name);
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
    public boolean checkQuestionData() {
        boolean bool;
        //System.out.println(textFieldFE.getText());
        //System.out.println(artChoice.getSelectionModel().getSelectedItem());
        if (!questionTextField.getText().equals("")) {
            if (!questionTypeChoiceBox.getSelectionModel().getSelectedItem().equals("-- Art der Frage --")) {
                bool = (answerTable.getItems().size() >= 1 && !yesNoCheckBox.isSelected()) || !questionTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Multiple Choice");
            } else {
                bool = false;
            }
        } else {
            bool = false;
        }

        return bool;
    }

    @FXML
    private void updateCheckboxes() {
        FrageEditParam param = new FrageEditParam(questionTypeChoiceBox, numberChoiceBox, numberTextField, requiredQuestionCheckBox, multipleChoiceCheckBox,
				listCheckBox, textAreaCheckBox, yesNoCheckBox, headlineCheckBox, excelFormatCheckBox, numberCheckBox);

        questionTypeChoiceBox.setDisable(!param.isTypeActivatable());
        if (questionTypeChoiceBox.isDisabled()) {
            questionTypeChoiceBox.getSelectionModel().select(1);
        }

        numberChoiceBox.setDisable(!param.isNumberTypeActivatable());
        if (numberChoiceBox.isDisabled()) {
            numberChoiceBox.getSelectionModel().clearSelection();
        }

        numberTextField.setDisable(!param.isCountCharsActivatable());
        if (numberTextField.isDisabled()) {
            numberTextField.clear();
        }

        requiredQuestionCheckBox.setDisable(!param.isRequiredActivatable());
        if (requiredQuestionCheckBox.isDisabled()) {
            requiredQuestionCheckBox.setSelected(false);
        }

        multipleChoiceCheckBox.setDisable(!param.isMultipleChoiceActivatable());
        if (multipleChoiceCheckBox.isDisabled()) {
            multipleChoiceCheckBox.setSelected(false);
        }

        listCheckBox.setDisable(!param.isListActivatable());
        if (listCheckBox.isDisabled()) {
            listCheckBox.setSelected(false);
        }

        textAreaCheckBox.setDisable(!param.isTextareaActivatable());
        if (textAreaCheckBox.isDisabled()) {
            textAreaCheckBox.setSelected(false);
        }

        yesNoCheckBox.setDisable(!param.isYesNoQuestionActivatable());
        if (yesNoCheckBox.isDisabled()) {
            yesNoCheckBox.setSelected(false);
        }

        excelFormatCheckBox.setDisable(!param.isSingleLineActivatable());
        if (excelFormatCheckBox.isDisabled()) {
            excelFormatCheckBox.setSelected(false);
        }

        numberCheckBox.setDisable(!param.isNumericActivatable());
        if (numberCheckBox.isDisabled()) {
            numberCheckBox.setSelected(false);
        }

        answerTable.setDisable(!param.isAnswersListActivatable());
        newAnswerButton.setDisable(!param.isAnswersListActivatable());
    }

}
