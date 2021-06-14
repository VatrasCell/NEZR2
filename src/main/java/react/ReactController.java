package react;

import application.ScreenController;
import flag.FlagList;
import flag.React;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import model.AnswerOption;
import model.Question;
import model.QuestionType;
import model.Questionnaire;
import model.ReactTableElement;
import model.SceneName;
import question.QuestionController;
import questionList.QuestionListService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ReactController {

    public static Question question;
    public static List<Question> questions;
    public static Questionnaire questionnaire;

    private final int MAX_COUNT_REACTIONS = 1;

    private ObservableList<ReactTableElement> data = FXCollections.observableArrayList();
    private ObservableList<Question> questionData = FXCollections.observableArrayList();
    private ObservableList<String> answerData = FXCollections.observableArrayList();

    private boolean hasQuestion = false;
    private boolean hasAnswer = false;

    @FXML
    private Button btn_new;

    @FXML
    private TableView<ReactTableElement> tbl_react;
    @FXML
    private TableColumn<ReactTableElement, String> questionsColumn;
    @FXML
    private TableColumn<ReactTableElement, String> answersColumn;
    @FXML
    private TableColumn<ReactTableElement, String> commentsColumn;
    @FXML
    private TableColumn<ReactTableElement, String> delCol = new TableColumn<>("Löschen");

    /**
     * The constructor (is called before the initialize()-method).
     */
    public ReactController() {
        FlagList flags = question.getFlags();
        questions = QuestionListService.getQuestions(questionnaire.getId());

        for (React react : flags.getReacts()) {
            Question question = questions.get(getY(react.getQuestionId(), react.getQuestionType(), questions));
            data.add(new ReactTableElement(question, react.getAnswerPos(), react));
        }

        questionData.addAll(questions);
        for (int i = 0; i < questions.size(); ++i) {
            if (questions.get(i).getQuestionType().equals(question.getQuestionType()) && questions.get(i).getQuestionId() == question.getQuestionId()) {
                questionData.remove(i);
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

        questionsColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        answersColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentsColumn.setCellFactory(
                param -> {
                    TableCell<ReactTableElement, String> cell = new TableCell<>();
                    Text text = new Text();
                    cell.setGraphic(text);
                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    text.wrappingWidthProperty().bind(cell.widthProperty());
                    text.textProperty().bind(cell.itemProperty());
                    return cell;
                });

        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<ReactTableElement, String>, TableCell<ReactTableElement, String>> cellFactoryDel = //
                new Callback<TableColumn<ReactTableElement, String>, TableCell<ReactTableElement, String>>() {
                    @Override
                    public TableCell<ReactTableElement, String> call(
                            final TableColumn<ReactTableElement, String> param) {
                        return new TableCell<ReactTableElement, String>() {

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
                                        List<React> reacts = question.getFlags().getReacts();
                                        reacts.remove(reactTableElement.getFlag());
                                        question.getFlags().setReacts(reacts);

                                        data.remove(reactTableElement);
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };

        delCol.setCellFactory(cellFactoryDel);
        tbl_react.getColumns().add(delCol);

        ObservableList<React> flagData = FXCollections.observableArrayList();
        flagData.setAll(question.getFlags().getReacts());
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
                .add(ScreenController.class.getClassLoader().getResource(ScreenController.STYLESHEET).toExternalForm());

        HBox hBox = new HBox(8);

        ComboBox<Question> questionComboBox = new ComboBox<>();
        questionComboBox.valueProperty().addListener((ov, oldQuestion, newQuestion) -> {
            if (newQuestion != null) {
                hasQuestion = true;
                okButton.setDisable(!(hasQuestion && hasAnswer));
                answerData.clear();
                for (AnswerOption answerOption : newQuestion.getAnswerOptions()) {
                    String answer = answerOption.getValue();
                    if (answer.equals("")) {
                        answer = "<Textfeld>";
                    }
                    answerData.add(answer);
                }
            } else {
                hasQuestion = false;
            }
        });

        questionComboBox.setItems(questionData);
        ComboBox<String> answer = new ComboBox<>();
        answer.valueProperty().addListener((ov, oldAnswer, newAnswer) -> {
            if (newAnswer != null) {
                hasAnswer = true;
                okButton.setDisable(!(hasQuestion && hasAnswer));
            } else {
                hasAnswer = false;
            }
        });
        answer.setItems(answerData);
        hBox.getChildren().add(new Label("Frage:"));
        hBox.getChildren().add(questionComboBox);
        hBox.getChildren().add(new Label("Antwort:"));
        hBox.getChildren().add(answer);

        dialog.getDialogPane().setContent(hBox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                Question question = questionComboBox.getSelectionModel().getSelectedItem();
                int answerPos = answer.getSelectionModel().getSelectedIndex();
                React react = new React(question.getQuestionType(), question.getQuestionId(), answerPos);
                ReactTableElement tableElement = new ReactTableElement(question, answerPos, react);
                return new Pair<>(react, tableElement);
            }
            return null;
        });

        Optional<Pair<React, ReactTableElement>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            ReactController.question.getFlags().getReacts().add(pair.getKey());
            data.add(pair.getValue());
        });
    }

    @FXML
    private void save() throws IOException {
        QuestionController.question = question;
        ScreenController.activate(SceneName.QUESTION);
    }

    @FXML
    private void exit() throws IOException {
        ScreenController.activate(SceneName.QUESTION);
    }

    /**
     * Gibt die Position des "FrageErstellen" Objektes in dem ArrayList "questions" zurück
     * welche die entsprechende Fragen- ID und Fragenart hat. F�r die Vorschau!
     * <p>
     *
     * @param x            int: Fragen- ID
     * @param questionType QuestionType: Fragenart
     * @param questions    ArrayList FrageErstellen: alle Fragen
     * @return Postition im ArrayList "questions" als int.
     */
    private static int getY(int x, QuestionType questionType, List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            if (x == questions.get(i).getQuestionId() && questionType.equals(questions.get(i).getQuestionType())) {
                return i;
            }
        }
        return -1;
    }
}
