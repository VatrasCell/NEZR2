package start;

import application.GlobalVars;
import application.NotificationController;
import application.ScreenController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import login.LoginController;
import message.MessageId;
import model.AnswerOption;
import model.Headline;
import model.Question;
import model.QuestionType;
import model.SceneName;
import model.SurveyPage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import questionList.QuestionListService;
import survey.SurveyController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static application.GlobalFuncs.getURL;
import static model.SceneName.SURVEY_1;

public class StartController {

    @FXML
    Label questionnaireLabel;

    @FXML
    Label warningLabel;

    @FXML
    Pane pane;

    @FXML
    Button startButton;

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
            case "R\u00FCgen":
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

        String image = getURL(value).toExternalForm();
        pane.setStyle("-fx-background-image: url('" + image + "');" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-attachment: fixed;" +
                "-fx-background-size: 10% auto;" +
                "-fx-background-position: 98% 5%;");

        setStartText();
        questionnaireLabel.textProperty().bind(questionnaireText);
        warningLabel.textProperty().bind(questionnaireWarn);
        warningLabel.setStyle("-fx-text-fill: #c90000;");
    }

    public static void setStartText() {
        if (GlobalVars.activeQuestionnaire != null) {
            questionnaireText.set(String.format("Fragebogen: %s", GlobalVars.activeQuestionnaire.getName()));
            if (!GlobalVars.activeQuestionnaire.getLocation().equals(GlobalVars.location)) {
                questionnaireWarn.set("Fragebogen ist nicht für diesen Standort optimiert");
            } else {
                questionnaireWarn.set("");
            }
        } else {
            questionnaireText.set("");
            questionnaireWarn.set("kein Fragebogen ausgewählt");
        }
    }

    public static void makeQuestionnaire(List<Question> questions, boolean isPreview) throws IOException {
        SurveyController.setPreview(isPreview);
        List<SurveyPage> pages = getSurveyPages(questions);
        SurveyController.setPageCount(pages.size());
        createPanels(pages);
    }

    private static List<SurveyPage> getSurveyPages(List<Question> questions) {
        List<SurveyPage> pages = new ArrayList<>();
        int oldPosition = 1;
        Headline currentHeadline = questions.get(0).getHeadline();
        SurveyPage page = new SurveyPage();
        for (Question question : questions) {
            //check need for new page and create it if necessary
            if (isNewHeadline(currentHeadline, question.getHeadline()) || oldPosition < question.getPosition() || page.getQuestions().size() == GlobalVars.PER_COLUMN) {
                page.setPageNumber(pages.size() + 1);
                page.setHeadline(currentHeadline);
                pages.add(page);
                currentHeadline = question.getHeadline();

                page = new SurveyPage();
            }

            oldPosition = question.getPosition();
            page.addQuestion(question);
        }

        //add last page
        page.setPageNumber(pages.size() + 1);
        page.setHeadline(currentHeadline);
        pages.add(page);

        return pages;
    }

    public static void createPanels(List<SurveyPage> pages) throws IOException {
        for (SurveyPage page : pages) {

            Pane scene = FXMLLoader.load(getURL(SceneName.SURVEY_PATH));

            setHeaderFields(scene, page.getPageNumber(), pages.size(), page.getHeadline());

            setQuestions(scene, page.getQuestions());

            if (needsEvaluationQuestionFooter(page.getQuestions())) {
                createEvaluationQuestionFooter(scene);
            }


            ScreenController.addScreen("survey_" + page.getPageNumber(), scene);
        }
    }

    private static boolean needsEvaluationQuestionFooter(List<Question> questions) {
        return questions.stream().anyMatch(question -> question.getFlags().isEvaluationQuestion());
    }

    private static void setHeaderFields(Pane scene, int pageNumber, int pageCount, Headline headline) {
        ProgressBar progressBar = (ProgressBar) scene.lookup("#progressBar");
        progressBar.setProgress((float) (pageNumber) / (float) pageCount);

        Label lbl_count = (Label) scene.lookup("#lbl_count");
        lbl_count.setText(String.format("Frage %s/%s", (pageNumber), pageCount));

        if (headline != null) {
            Label lbl_headline = (Label) scene.lookup("#lbl_headline");
            lbl_headline.setText(removeMark(headline.getName()));
        }

    }

    private static void setQuestions(Pane scene, List<Question> questions) {
        ValidationSupport validationSupport = new ValidationSupport();
        VBox outerVBox = (VBox) scene.lookup("#vbox");

        HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap = new HashMap<>();
        BooleanProperty checkRequiredValue = new SimpleBooleanProperty(true);

        for (Question question : questions) {
            VBox innerVBox = new VBox();
            innerVBox.setAlignment(Pos.CENTER);
            innerVBox.getChildren().add(createQuestionLabel(scene, question));

            if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                createMultipleChoiceQuestion(innerVBox, question, booleanPropertyHashMap, checkRequiredValue);
            } else {
                createShortAnswerQuestion(innerVBox, question, validationSupport);
            }

            outerVBox.getChildren().add(innerVBox);
        }

        scene.lookup("#btn_next").disableProperty().bind(validationSupport.invalidProperty().isEqualTo(checkRequiredValue));
    }

    private static void createEvaluationQuestionFooter(Pane scene) {
        HBox hBox = (HBox) scene.lookup("#footer_hBox");
        hBox.getChildren().add(new Label("0: keine Aussage"));
        hBox.getChildren().add(new Label("1: sehr schlecht"));
        hBox.getChildren().add(new Label("10: sehr gut"));
    }

    private static void createMultipleChoiceQuestion(VBox innerVBox, Question question, HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap, BooleanProperty checkRequiredValue) {
        if (question.getFlags().isList()) {
            innerVBox.getChildren().add(createListViewHBox(question, booleanPropertyHashMap, checkRequiredValue));
        } else {
            innerVBox.getChildren().add(createCheckboxHBox(question, booleanPropertyHashMap, checkRequiredValue));
        }
    }

    private static void createShortAnswerQuestion(VBox innerVBox, Question question, ValidationSupport validationSupport) {
        if (question.getFlags().isTextArea()) {
            innerVBox.getChildren().add(createTextAreaHBox(question, validationSupport));
        } else {
            innerVBox.getChildren().add(createTextFieldHBox(question, validationSupport));
        }
    }

    private static HBox createTextFieldHBox(Question question, ValidationSupport validationSupport) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        TextField textField = new TextField();

        if (question.getFlags().getValidation() != null) {
            validationSupport.registerValidator(textField,
                    Validator.createRegexValidator("FELHER", regexOrEmpty(question.getFlags().getValidation().getRegex()), Severity.ERROR));
        }

        if (question.getFlags().isRequired()) {
            validationSupport.registerValidator(textField, Validator.createEmptyValidator("FEHLER"));
        }

        hBox.getChildren().add(textField);
        return hBox;
    }

    private static HBox createTextAreaHBox(Question question, ValidationSupport validationSupport) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        TextArea textArea = new TextArea();

        if (question.getFlags().getValidation() != null) {
            validationSupport.registerValidator(textArea,
                    Validator.createRegexValidator("FELHER", regexOrEmpty(question.getFlags().getValidation().getRegex()), Severity.ERROR));
        }

        if (question.getFlags().isRequired()) {
            validationSupport.registerValidator(textArea, Validator.createEmptyValidator("FEHLER"));
        }

        hBox.getChildren().add(textArea);
        return hBox;
    }

    private static String regexOrEmpty(String regex) {
        return String.format("(^$|%s)", regex);
    }

    private static HBox createListViewHBox(Question question, HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap, BooleanProperty checkRequiredValue) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        if (question.getFlags().isRequired()) {
            checkRequiredValue.setValue(false);
            booleanPropertyHashMap.put(question.getQuestionId(), new ArrayList<>());
        }

        ListView<AnswerOption> answerOptionListView = new ListView<>();
        if (question.getFlags().isMultipleChoice()) {
            answerOptionListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } else {
            answerOptionListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }

        answerOptionListView.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();
            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != answerOptionListView && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            // if is part of a cell or the cell,
            // handle event instead of using standard handling
            if (node instanceof ListCell) {
                // prevent further handling
                evt.consume();

                ListCell<AnswerOption> cell = (ListCell) node;
                ListView<AnswerOption> lv = cell.getListView();

                // focus the listview
                lv.requestFocus();

                if (!cell.isEmpty()) {
                    // handle selection for non-empty cells
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }

            if (question.getFlags().isRequired()) {
                List<BooleanProperty> booleanProperties = booleanPropertyHashMap.get(question.getQuestionId());
                booleanProperties.add(new SimpleBooleanProperty(!answerOptionListView.selectionModelProperty().getValue().getSelectedItems().isEmpty()));
                booleanPropertyHashMap.put(question.getQuestionId(), booleanProperties);

                checkObservableValues(checkRequiredValue, booleanPropertyHashMap);
            }
        });

        answerOptionListView.setItems(FXCollections.observableArrayList(question.getAnswerOptions()));
        hBox.getChildren().add(answerOptionListView);
        return hBox;
    }

    private static HBox createCheckboxHBox(Question question, HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap, BooleanProperty checkRequiredValue) {
        ToggleGroup group = new ToggleGroup();
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        if (question.getFlags().isRequired()) {
            checkRequiredValue.setValue(false);
            booleanPropertyHashMap.put(question.getQuestionId(), new ArrayList<>());
        }

        for (AnswerOption answerOption : question.getAnswerOptions()) {
            if (question.getFlags().isMultipleChoice()) {
                CheckBox checkBox = new CheckBox();
                checkBox.setUserData(answerOption);
                checkBox.setText(answerOption.getValue());

                if (question.getFlags().isRequired()) {
                    List<BooleanProperty> booleanProperties = booleanPropertyHashMap.get(question.getQuestionId());
                    booleanProperties.add(checkBox.selectedProperty());
                    booleanPropertyHashMap.put(question.getQuestionId(), booleanProperties);

                    checkBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                        checkObservableValues(checkRequiredValue, booleanPropertyHashMap);
                    });
                }

                hBox.getChildren().add(checkBox);
            } else {
                RadioButton radioButton = new RadioButton();
                radioButton.setUserData(answerOption);
                radioButton.setText(answerOption.getValue());
                radioButton.setToggleGroup(group);

                if (question.getFlags().isRequired()) {
                    List<BooleanProperty> booleanProperties = booleanPropertyHashMap.get(question.getQuestionId());
                    booleanProperties.add(radioButton.selectedProperty());
                    booleanPropertyHashMap.put(question.getQuestionId(), booleanProperties);

                    radioButton.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                        checkObservableValues(checkRequiredValue, booleanPropertyHashMap);
                    });
                }

                hBox.getChildren().add(radioButton);
            }
        }

        return hBox;
    }

    private static void checkObservableValues(BooleanProperty checkRequiredValue, HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap) {
        List<Boolean> booleans = new ArrayList<>();

        for (List<BooleanProperty> booleanProperties : booleanPropertyHashMap.values()) {
            booleans.add(booleanProperties.stream().anyMatch(ObservableBooleanValue::get));
        }
        checkRequiredValue.setValue(booleans.stream().allMatch(aBoolean -> aBoolean));
    }


    private static boolean isNewHeadline(Headline currentHeadline, Headline headline) {
        if (currentHeadline != null && headline != null) {
            return !currentHeadline.equals(headline);
        } else {
            return currentHeadline == null ^ headline == null;
        }
    }

    private static Label createQuestionLabel(Pane screen, Question question) {
        String questionTest = removeMark(question.getQuestion());

        questionTest = addRequiredTag(questionTest, question.getFlags().isRequired());

        Label questionLabel = new Label(questionTest);
        // System.out.println("frageObj.get(y).frageid = " +
        // frageObj.get(y).getFrageID());
        questionLabel.setId("lbl_question_" + question.getQuestionId());

        if (question.getFlags().hasMultipleChoiceReact()) {
            //questionLabel.setVisible(false);
        }

        // allePanel.get(z).add(questionLabel, "align center, span, wrap");
        question.setScene(screen);
        question.setQuestionLabel(questionLabel);

        return questionLabel;
    }

    private static String removeMark(String text) {
        Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
        Matcher ms = MY_PATTERNs.matcher(text);
        if (ms.find()) {
            System.out.println("Old dataset pattern found.");
        }

        return text;
    }

    private static String addRequiredTag(String text, boolean required) {
        return required ? text + " *" : text;
    }

    @FXML
    private void adminLogin() throws IOException {
        if (GlobalVars.DEV_MODE) {
            LoginController.devLogin();
        } else {
            ScreenController.activate(SceneName.LOGIN);
        }
    }

    @FXML
    private void next() throws IOException {
        List<Question> questions = QuestionListService.getQuestions(GlobalVars.activeQuestionnaire.getId());
        if (questions.isEmpty()) {
            NotificationController.createErrorMessage(MessageId.TITLE_QUESTIONNAIRE, MessageId.MESSAGE_QUESTIONNAIRE_IS_EMPTY);
            return;
        }
        makeQuestionnaire(questions, false);
        ScreenController.activate(SURVEY_1);
    }
}
