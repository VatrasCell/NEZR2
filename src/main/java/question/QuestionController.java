package question;

import application.DialogMessageController;
import application.GlobalVars;
import application.NotificationController;
import application.ScreenController;
import flag.FlagList;
import flag.Number;
import flag.React;
import flag.SymbolType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import message.DialogId;
import message.MessageId;
import model.Answer;
import model.Category;
import model.Headline;
import model.Question;
import model.QuestionEditParam;
import model.QuestionType;
import model.Questionnaire;
import model.SceneName;
import questionList.QuestionListService;
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
import static application.ScreenController.STYLESHEET;
import static application.TableColumnNameController.getColumnName;
import static message.TableColumnNameId.DELETE;
import static message.TableColumnNameId.EDIT;
import static model.QuestionType.MULTIPLE_CHOICE_STRING;
import static model.QuestionType.SHORT_ANSWER_STRING;

public class QuestionController {
    public static Questionnaire questionnaire;
    public static Question question;
    public Button saveButton;
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
    private ChoiceBox<Category> categoryChoiceBox;
    @FXML
    private ChoiceBox<String> questionTypeChoiceBox;
    @FXML
    private ChoiceBox<String> numberChoiceBox;
    @FXML
    private ChoiceBox<Headline> headlineChoiceBox;

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
    private CheckBox excelFormatCheckBox;
    @FXML
    private CheckBox numberCheckBox;
    @FXML
    private CheckBox evaluationQuestionCheckBox;

    @FXML
    private Button newAnswerButton;

    @FXML
    private TableView<Answer> answerTable;
    @FXML
    private TableColumn<Answer, String> answerIdTableColumn;
    @FXML
    private TableColumn<Answer, String> answerValueTableColumn;
    @FXML
    private TableColumn<Answer, String> editButtonColumn = new TableColumn<>(getColumnName(EDIT));
    @FXML
    private TableColumn<Answer, String> deleteButtonColumn = new TableColumn<>(getColumnName(DELETE));

    /**
     * The constructor (is called before the initialize()-method).
     */
    public QuestionController() {
        // fuer die Generierung der Antwortentabelle
        answers = QuestionService.getAnswers(question);
        for (int i = 0; i < Objects.requireNonNull(answers).size(); ++i) {
            Answer answer = new Answer();
            answer.setId(i + 1);
            answer.setValue(answers.get(i));
            data.add(answer);
        }
    }

    /**
     * Initializes the controller class. This method is automatically called after
     * the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        answerTable.setItems(data);

        answerIdTableColumn.setCellValueFactory(new PropertyValueFactory<>(Answer.ID));
        answerValueTableColumn.setCellValueFactory(new PropertyValueFactory<>(Answer.VALUE));

        editButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.EDIT));
        answerTable.getColumns().add(editButtonColumn);

        deleteButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.DELETE));
        answerTable.getColumns().add(deleteButtonColumn);

        fillScene();
    }

    public static Button initEditButton(Answer answer) {
        ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            //TODO edit Antwort
        });

        return button;
    }

    public static Button initDeleteButton(Answer answer) {
        ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            //TODO delete Antwort
        });

        return button;
    }

    private void fillScene() {
        Pattern MY_PATTERN = Pattern.compile("#\\[[0-9]+]");
        Matcher m = MY_PATTERN.matcher(question.getQuestion());
        if (m.find()) {
            System.out.println("Old dataset pattern found.");
        }
        questionTextField.setText(question.getQuestion());
        List<Integer> range = IntStream.range(1, QuestionService.getCountPosition(questionnaire.getId()) + 2).boxed()
                .collect(Collectors.toList());
        ObservableList<Integer> positionList = FXCollections.observableArrayList(range);
        positionChoiceBox.setItems(positionList);
        positionChoiceBox.getSelectionModel().select(question.getPosition() - 1);

        ObservableList<Category> categoryList = FXCollections.observableArrayList(QuestionService.getCategories());
        categoryChoiceBox.setItems(categoryList);
        categoryChoiceBox.getSelectionModel().select(question.getCategory());

        headlineChoiceBox.setItems(createHeadlineList());
        if (question.getHeadline() != null) {
            headlineChoiceBox.getSelectionModel().select(question.getHeadline());
        } else {
            headlineChoiceBox.getSelectionModel().selectFirst();
        }


        ObservableList<String> questionTypeList = FXCollections.observableArrayList(QuestionType.toList());
        questionTypeChoiceBox.setItems(questionTypeList);
        questionTypeChoiceBox.getSelectionModel().select(question.getQuestionType().getQuestionType());
        questionTypeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            questionTypeChoiceBox.getSelectionModel().select(newValue.intValue());
            updateCheckboxes();
        });

        ObservableList<String> comparisonList = FXCollections.observableArrayList("Genau wie die Zahl", "Kleiner gleich Zahl",
                "Größer gleich Zahl");
        numberChoiceBox.setItems(comparisonList);

        if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
            questionTypeChoiceBox.getSelectionModel().select(MULTIPLE_CHOICE_STRING);
        } else {
            questionTypeChoiceBox.getSelectionModel().select(SHORT_ANSWER_STRING);
        }

        if (question.getFlags().has(SymbolType.B)) {
            evaluationQuestionCheckBox.setSelected(true);
        } else {
            evaluationQuestionCheckBox.setSelected(false);
        }

        if (question.getFlags().has(SymbolType.MC)) {
            multipleChoiceCheckBox.setSelected(true);
        } else {
            multipleChoiceCheckBox.setSelected(false);
        }

        if (question.getFlags().has(SymbolType.LIST)) {
            listCheckBox.setSelected(true);
        } else {
            listCheckBox.setSelected(false);
        }

        if (question.getFlags().has(SymbolType.TEXT)) {
            textAreaCheckBox.setSelected(true);
        } else {
            textAreaCheckBox.setSelected(false);
        }

        if (question.getFlags().has(SymbolType.REQUIRED)) {
            requiredQuestionCheckBox.setSelected(true);
        } else {
            requiredQuestionCheckBox.setSelected(false);
        }

        if (question.getFlags().has(SymbolType.JN)) {
            yesNoCheckBox.setSelected(true);
            if (question.getFlags().has(SymbolType.JNExcel)) {
                excelFormatCheckBox.setSelected(true);
            } else {
                excelFormatCheckBox.setSelected(false);
            }
        } else {
            yesNoCheckBox.setSelected(false);
        }

        if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
            if (question.getAnswerOptions().size() > 0) {
                if (question.getAnswerOptions().get(0).equals("#####")) {
                    System.out.println("Old dataset pattern found.");
                }
            }
        }

        List<Number> numbers = question.getFlags().getAll(Number.class);

        if (numbers.size() > 0) {
            Number number = numbers.get(0);
            numberTextField.setText(String.valueOf(number.getDigits()));
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
    private void exit() throws IOException {
        ScreenController.activate(SceneName.QUESTION_LIST);
    }

    @FXML
    private void save() throws IOException {
        //TODO "real time" validation
        if (!checkQuestionData()) {
            NotificationController.createErrorMessage(MessageId.TITLE_SAVE_QUESTION, MessageId.MESSAGE_SAVE_QUESTION_ERROR);
            return;
        }

        Question questionToSave = new Question();

        String oldQuestion = question.getQuestion();
        String newQuestion = questionTextField.getText();

        FlagList flags = question.getFlags();

        QuestionEditParam param = new QuestionEditParam(questionTypeChoiceBox, numberChoiceBox, numberTextField,
                requiredQuestionCheckBox, evaluationQuestionCheckBox, multipleChoiceCheckBox, listCheckBox, textAreaCheckBox,
                yesNoCheckBox, excelFormatCheckBox, numberCheckBox);

        questionToSave.setQuestionType(param.getQuestionType());

        Category selectedCategory = categoryChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            selectedCategory = categoryChoiceBox.getItems().get(0);
        }
        questionToSave.setCategory(selectedCategory);

        if (!newQuestion.equals(oldQuestion)) {
            if (QuestionService.doQuestionExistsInQuestionnaire(newQuestion, questionnaire.getId(), questionToSave.getQuestionType())) {
                NotificationController.createErrorMessage(MessageId.TITLE_SAVE_QUESTION, MessageId.MESSAGE_SAVE_QUESTION_ALREADY_EXISTS_ERROR);
                return;
            }
        }

        questionToSave.setQuestion(newQuestion);

        questionToSave.setPosition(positionChoiceBox.getValue());

        List<React> reactList = flags.getAll(React.class);
        for (React react : reactList) {
            if (param.isRequired()) {
                QuestionService.provideQuestionRequired(questionnaire.getId(), react.getQuestionType(), react.getQuestionId());
            }
            if (QuestionService.isQuestionRequired(questionnaire.getId(), react.getQuestionType(), react.getQuestionId())) {
                param.setRequired(true);
            }
        }

        Headline selectedHeadline = headlineChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedHeadline != null) {
            questionToSave.setHeadline(selectedHeadline);
        }

        if (param.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            if (param.isEvaluationQuestion()) {
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
                    QuestionService.deleteFlagsFromTargetQuestion(questionnaire.getId(), questionToSave.getQuestionId());
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
            QuestionService.getPossibleFlags(flags, param);
            questionToSave.setFlags(flags);
            QuestionService.saveShortAnswerQuestion(questionnaire.getId(), questionToSave);
        }

        ScreenController.activate(SceneName.QUESTION_LIST);

    }

    @FXML
    private void setPreview() throws IOException {
        StartController.makeQuestionnaire(Collections.singletonList(question), true);
        ScreenController.activate(SceneName.SURVEY_0);
    }

    @FXML
    private void createAnswer() {
        TextInputDialog dialog = new TextInputDialog("");
        DialogMessageController.setMessage(dialog, DialogId.TITLE_CREATE_ANSWER, DialogId.CONTENT_TEXT_CREATE_ANSWER);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Answer answer = new Answer();
            answer.setId(data.size() + 1);
            answer.setValue(name);
            data.add(answer);

            NotificationController.createMessage(MessageId.TITLE_CREATE_ANSWER, MessageId.MESSAGE_CREATE_ANSWER, name);
        });

    }

    @FXML
    private void createCategory() {
        TextInputDialog dialog = new TextInputDialog("");
        DialogMessageController.setMessage(dialog, DialogId.TITLE_CREATE_CATEGORY, DialogId.CONTENT_TEXT_CREATE_CATEGORY);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            QuestionService.createUniqueCategory(name);
            Category category = QuestionService.getCategory(name);
            ObservableList<Category> categoryList = FXCollections.observableArrayList(QuestionService.getCategories());
            categoryChoiceBox.setItems(categoryList);
            categoryChoiceBox.getSelectionModel().select(category);
        });
    }

    @FXML
    private void createHeadline() {
        TextInputDialog dialog = new TextInputDialog("");
        DialogMessageController.setMessage(dialog, DialogId.TITLE_CREATE_HEADLINE, DialogId.CONTENT_TEXT_CREATE_HEADLINE);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            QuestionListService.createUniqueHeadline(name);
            Headline headline = QuestionListService.getHeadlineByName(name);
            headlineChoiceBox.setItems(createHeadlineList());
            headlineChoiceBox.getSelectionModel().select(headline);
        });
    }

    @FXML
    //TODO Folgefrage
    private void react() {
        ReactController.question = question;
        ReactController.questionnaire = questionnaire;

        try {
            ScreenController.addScreen(SceneName.REACT, getURL(SceneName.REACT_PATH));
            ScreenController.activate(SceneName.REACT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkQuestionData() {
        boolean bool;
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
        QuestionEditParam param = new QuestionEditParam(questionTypeChoiceBox, numberChoiceBox, numberTextField,
                requiredQuestionCheckBox, evaluationQuestionCheckBox, multipleChoiceCheckBox,
                listCheckBox, textAreaCheckBox, yesNoCheckBox, excelFormatCheckBox, numberCheckBox);

        questionTypeChoiceBox.setDisable(!param.isQuestionTypeActivatable());
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

    @FXML
    private void validation() {

    }

    private ObservableList<Headline> createHeadlineList() {
        List<Headline> headlineListArrays = new ArrayList<>();
        headlineListArrays.add(null);
        headlineListArrays.addAll(
                QuestionListService.getHeadlines(questionnaire.getId()));
        return FXCollections.observableArrayList(headlineListArrays);
    }
}
