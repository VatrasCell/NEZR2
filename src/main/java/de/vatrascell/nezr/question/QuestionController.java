package de.vatrascell.nezr.question;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.DialogMessageController;
import de.vatrascell.nezr.application.controller.NotificationController;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.flag.FlagList;
import de.vatrascell.nezr.message.DialogId;
import de.vatrascell.nezr.message.MessageId;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Category;
import de.vatrascell.nezr.model.Headline;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionEditParam;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.model.Questionnaire;
import de.vatrascell.nezr.model.tableObject.AnswerOptionTableObject;
import de.vatrascell.nezr.model.tableObject.converter.AnswerTableObjectConverter;
import de.vatrascell.nezr.questionList.QuestionListController;
import de.vatrascell.nezr.react.ReactController;
import de.vatrascell.nezr.start.StartController;
import de.vatrascell.nezr.validation.ValidationController;
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
import javafx.scene.layout.GridPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;
import static de.vatrascell.nezr.application.controller.ScreenController.STYLESHEET;
import static de.vatrascell.nezr.application.controller.TableColumnNameController.getColumnName;
import static de.vatrascell.nezr.message.TableColumnNameId.DELETE;
import static de.vatrascell.nezr.message.TableColumnNameId.EDIT;
import static de.vatrascell.nezr.model.QuestionType.MULTIPLE_CHOICE_STRING;
import static de.vatrascell.nezr.model.QuestionType.SHORT_ANSWER_STRING;
import static de.vatrascell.nezr.model.SceneName.QUESTION_PATH;

@Component
@FxmlView(QUESTION_PATH)
public class QuestionController {
    public static Questionnaire questionnaire;
    public static Question question;
    public Button saveButton;
    private final ObservableList<AnswerOptionTableObject> data = FXCollections.observableArrayList();

    private final ScreenController screenController;
    private final AnswerOptionService answerOptionService;
    private final CategoryService categoryService;
    private final HeadlineService headlineService;
    private final StartController startController;
    private final QuestionService questionService;

    @FXML
    private Label questionLabel;

    @FXML
    private TextField questionTextField;

    @FXML
    private ChoiceBox<Integer> positionChoiceBox;
    @FXML
    private ChoiceBox<Category> categoryChoiceBox;
    @FXML
    private ChoiceBox<String> questionTypeChoiceBox;
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
    private CheckBox evaluationQuestionCheckBox;

    @FXML
    private GridPane formGridPane;

    @FXML
    private Button newAnswerButton;
    @FXML
    private final TableColumn<AnswerOptionTableObject, String> editButtonColumn = new TableColumn<>(getColumnName(EDIT));
    @FXML
    private final TableColumn<AnswerOptionTableObject, String> deleteButtonColumn = new TableColumn<>(getColumnName(DELETE));
    @FXML
    private TableView<AnswerOptionTableObject> answerTable;
    @FXML
    private TableColumn<AnswerOptionTableObject, String> answerValueTableColumn;

    /**
     * The constructor (is called before the initialize()-method).
     */
    @Autowired
    @Lazy
    public QuestionController(AnswerOptionService answerOptionService, CategoryService categoryService,
                              HeadlineService headlineService, QuestionService questionService,
                              StartController startController, ScreenController screenController) {
        this.answerOptionService = answerOptionService;
        this.categoryService = categoryService;
        this.headlineService = headlineService;
        this.questionService = questionService;
        this.startController = startController;
        this.screenController = screenController;
    }

    /**
     * Initializes the controller class. This method is automatically called after
     * the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // fuer die Generierung der Antwortentabelle
        data.clear();
        List<AnswerOptionTableObject> tableObjects = question.getQuestionId() == null ?
                new ArrayList<>() :
                AnswerTableObjectConverter.convert(Objects.requireNonNull(answerOptionService.getAnswerOptions(question.getQuestionId())), this);
        data.addAll(Objects.requireNonNull(tableObjects));

        //TODO debug only
        //formGridPane.setGridLinesVisible(true);

        answerTable.setItems(data);

        //answerIdTableColumn.setCellValueFactory(new PropertyValueFactory<>(Answer.ID));
        answerValueTableColumn.setCellValueFactory(new PropertyValueFactory<>(AnswerOption.VALUE));

        editButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.EDIT));
        answerTable.getColumns().add(editButtonColumn);

        deleteButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.DELETE));
        answerTable.getColumns().add(deleteButtonColumn);

        fillScene();
    }

    public Button initEditButton(AnswerOption answerOption) {
        ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
        imgView.setFitHeight(20);
        imgView.setFitWidth(20);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            //TODO edit Antwort
        });

        return button;
    }

    public Button initDeleteButton(AnswerOption answerOption) {
        ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
        imgView.setFitHeight(20);
        imgView.setFitWidth(20);
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
        List<Integer> range = IntStream.range(1, questionService.getMaxQuestionPosition(questionnaire.getId()) + 2).boxed()
                .collect(Collectors.toList());
        ObservableList<Integer> positionList = FXCollections.observableArrayList(range);
        positionChoiceBox.setItems(positionList);
        positionChoiceBox.getSelectionModel().select(question.getPosition() - 1);

        ObservableList<Category> categoryList = FXCollections.observableArrayList(categoryService.getCategories());
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

        if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
            questionTypeChoiceBox.getSelectionModel().select(MULTIPLE_CHOICE_STRING);
        } else {
            questionTypeChoiceBox.getSelectionModel().select(SHORT_ANSWER_STRING);
        }

        evaluationQuestionCheckBox.setSelected(question.getFlags().isEvaluationQuestion());

        multipleChoiceCheckBox.setSelected(question.getFlags().isMultipleChoice());

        listCheckBox.setSelected(question.getFlags().isList());

        textAreaCheckBox.setSelected(question.getFlags().isTextArea());

        requiredQuestionCheckBox.setSelected(question.getFlags().isRequired());

        if (question.getFlags().isYesNoQuestion()) {
            yesNoCheckBox.setSelected(true);
            excelFormatCheckBox.setSelected(question.getFlags().isSingleLine());
        } else {
            yesNoCheckBox.setSelected(false);
        }

        if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
            if (question.getAnswerOptions().size() > 0) {
                if (question.getAnswerOptions().get(0).getValue().equals("#####")) {
                    System.out.println("Old dataset pattern found.");
                }
            }
        }

        updateCheckboxes();
    }

    @FXML
    private void exit() {
        screenController.activate(QuestionListController.class);
    }

    @FXML
    private void save() throws IOException {
        if (question.getQuestionId() == null) {
            System.out.println("new Question");
            saveQuestion();
            //createQuestion();
        } else {
            System.out.println("update Question");
            saveQuestion();
        }
    }

    private void createQuestion() throws IOException {

    }

    private void saveQuestion() throws IOException {
        //TODO "real time" de.vatrascell.nezr.validation
        if (!checkQuestionData()) {
            NotificationController.createErrorMessage(MessageId.TITLE_SAVE_QUESTION, MessageId.MESSAGE_SAVE_QUESTION_ERROR);
            return;
        }

        Question questionToSave = new Question();

        String oldQuestion = question.getQuestion();
        String newQuestion = questionTextField.getText();

        QuestionEditParam param = new QuestionEditParam(questionTypeChoiceBox, requiredQuestionCheckBox, evaluationQuestionCheckBox,
                multipleChoiceCheckBox, listCheckBox, textAreaCheckBox, yesNoCheckBox, excelFormatCheckBox);

        FlagList flags = question.getFlags();
        flags.setFlagListByParam(param);

        questionToSave.setQuestionType(param.getQuestionType());

        Category selectedCategory = categoryChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            selectedCategory = categoryChoiceBox.getItems().get(0);
        }
        questionToSave.setCategory(selectedCategory);

        if (!newQuestion.equals(oldQuestion)) {
            if (questionService.doQuestionExistsInQuestionnaire(newQuestion, questionnaire.getId(), questionToSave.getQuestionType())) {
                NotificationController.createErrorMessage(MessageId.TITLE_SAVE_QUESTION, MessageId.MESSAGE_SAVE_QUESTION_ALREADY_EXISTS_ERROR);
                return;
            }
        }

        questionToSave.setQuestion(newQuestion);

        questionToSave.setPosition(positionChoiceBox.getValue());

        Headline selectedHeadline = headlineChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedHeadline != null) {
            questionToSave.setHeadline(selectedHeadline);
        }

        if (param.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            ArrayList<AnswerOption> answerOptions = new ArrayList<>();

            if (param.isEvaluationQuestion()) {
                for (int i = 0; i < 10; ++i) {
                    answerOptions.add(Objects.requireNonNull(answerOptionService.getAnswerOption(String.valueOf(i))));
                }

                //QuestionService.getPossibleFlags(flags, param);
                questionToSave.setFlags(flags);
                questionToSave.setAnswerOptions(answerOptions);
                questionService.saveMultipleChoice(questionnaire.getId(), questionToSave);
            } else {
                answerOptions = new ArrayList<>();

                if (param.isYesNoQuestion()) {
                    answerOptions.add(Objects.requireNonNull(answerOptionService.getAnswerOption("ja")));
                    answerOptions.add(Objects.requireNonNull(answerOptionService.getAnswerOption("nein")));
                } else {
                    answerOptions.addAll(answerTable.getItems());
                }

                //TODO what is this?!
                /*if (questionLabel.getText().equals("Frage Bearbeiten")) {
                    for (int i = 0; i < this.answers.size(); i++) {
                        for (AnswerOption answerOption : answerOptions) {
                            if (!this.answers.isEmpty() && this.answers.get(i).equals(answerOption.getValue())) {
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
                }*/

                //QuestionService.getPossibleFlags(flags, param);
                questionToSave.setFlags(flags);
                questionToSave.setAnswerOptions(answerOptions);
                questionService.saveMultipleChoice(questionnaire.getId(), questionToSave);
            }
        } else {
            //QuestionService.getPossibleFlags(flags, param);
            questionToSave.setFlags(flags);
            questionService.saveShortAnswerQuestion(questionnaire.getId(), questionToSave);
        }

        screenController.activate(QuestionListController.class);

    }

    @FXML
    private void setPreview() throws IOException {
        startController.makeQuestionnaire(Collections.singletonList(question), true);
        screenController.activate(QuestionListController.class);
    }

    @FXML
    private void createAnswer() {
        TextInputDialog dialog = new TextInputDialog("");
        DialogMessageController.setMessage(dialog, DialogId.TITLE_CREATE_ANSWER, DialogId.CONTENT_TEXT_CREATE_ANSWER);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            AnswerOption answerOption = new AnswerOption();
            answerOption.setValue(name);
            data.add(AnswerTableObjectConverter.convert(answerOption, this));

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
            categoryService.createUniqueCategory(name);
            Category category = categoryService.getCategory(name);
            ObservableList<Category> categoryList = FXCollections.observableArrayList(categoryService.getCategories());
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
            headlineService.createUniqueHeadline(name);
            Headline headline = headlineService.getHeadlineByName(name);
            headlineChoiceBox.setItems(createHeadlineList());
            headlineChoiceBox.getSelectionModel().select(headline);
        });
    }

    @FXML
    //TODO Folgefrage
    private void react() {
        ReactController.question = question;
        ReactController.questionnaire = questionnaire;
        screenController.activate(ReactController.class);
    }

    public boolean checkQuestionData() {
        boolean bool;
        if (!questionTextField.getText().equals("")) {
            if (!questionTypeChoiceBox.getSelectionModel().getSelectedItem().equals("-- Art der Frage --")) {
                bool = answerTable.getItems().size() >= 1 || !questionTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Multiple Choice") || yesNoCheckBox.isSelected();
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
        QuestionEditParam param = new QuestionEditParam(questionTypeChoiceBox,
                requiredQuestionCheckBox, evaluationQuestionCheckBox, multipleChoiceCheckBox,
                listCheckBox, textAreaCheckBox, yesNoCheckBox, excelFormatCheckBox);

        questionTypeChoiceBox.setDisable(!param.isQuestionTypeActivatable());
        if (questionTypeChoiceBox.isDisabled()) {
            questionTypeChoiceBox.getSelectionModel().select(1);
        }

        requiredQuestionCheckBox.setDisable(!param.isRequiredActivatable());
        if (requiredQuestionCheckBox.isDisabled()) {
            requiredQuestionCheckBox.setSelected(false);
        }

        evaluationQuestionCheckBox.setDisable(!param.isEvaluationQuestionActivatable());
        if (evaluationQuestionCheckBox.isDisabled()) {
            evaluationQuestionCheckBox.setSelected(false);
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

        answerTable.setDisable(!param.isAnswersListActivatable());
        newAnswerButton.setDisable(!param.isAnswersListActivatable());
    }

    @FXML
    private void validation() {
        screenController.activate(ValidationController.class);
    }

    private ObservableList<Headline> createHeadlineList() {
        List<Headline> headlineListArrays = new ArrayList<>();
        headlineListArrays.add(null);
        headlineListArrays.addAll(
                headlineService.getHeadlines(questionnaire.getId()));
        return FXCollections.observableArrayList(headlineListArrays);
    }
}
