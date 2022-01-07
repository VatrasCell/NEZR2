package de.vatrascell.nezr.start;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.controller.NotificationController;
import de.vatrascell.nezr.application.controller.ScreenController;
import de.vatrascell.nezr.application.svg.SvgImageLoader;
import de.vatrascell.nezr.login.LoginController;
import de.vatrascell.nezr.login.LoginService;
import de.vatrascell.nezr.message.MessageId;
import de.vatrascell.nezr.model.AnswerOption;
import de.vatrascell.nezr.model.Headline;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.QuestionType;
import de.vatrascell.nezr.model.SceneName;
import de.vatrascell.nezr.model.SurveyPage;
import de.vatrascell.nezr.questionList.QuestionListService;
import de.vatrascell.nezr.survey.SurveyController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;
import static de.vatrascell.nezr.model.SceneName.START_PATH;

@Component
@FxmlView(START_PATH)
public class StartController {

    @FXML
    Label questionnaireLabel;
    @FXML
    Label warningLabel;
    @FXML
    Pane pane;
    @FXML
    Button startButton;
    @FXML
    GridPane gridPane;
    @FXML
    ImageView imageView;

    private static final StringProperty questionnaireText = new SimpleStringProperty();
    private static final StringProperty questionnaireWarn = new SimpleStringProperty();

    private final QuestionListService questionListService;
    private final LoginService loginService;
    private final ScreenController screenController;
    private final LoginController loginController;

    /**
     * The constructor (is called before the initialize()-method).
     */
    @Autowired
    public StartController(StartService startService, QuestionListService questionListService,
                           LoginService loginService, ScreenController screenController, LoginController loginController) {
        this.questionListService = questionListService;
        this.loginService = loginService;
        this.screenController = screenController;
        this.loginController = loginController;
        GlobalVars.activeQuestionnaire = startService.getActiveQuestionnaire();
    }

    /**
     * Initializes the controller class. This method is automatically called after
     * the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        System.out.println("initialize StartController");
        String value;
        switch (GlobalVars.location) {
            case "R\u00FCgen":
                value = "images/svg/logo-naturerbe-zentrum-ruegen.svg";
                break;
            case "Bayerischer Wald":
                value = "images/svg/logo-baumwipfelpfad-bayerische-wald.svg";
                break;
            case "Saarschleife":
                value = "images/svg/logo-baumwipfelpfad-saarschleife.svg";
                break;
            case "Schwarzwald":
                value = "images/svg/logo-baumwipfelpfad-schwarzwald.svg";
                break;
            case "Usedom":
                value = "images/svg/logo-baumwipfelpfad-usedom.svg";
                break;
            case "Elsass":
                value = "images/svg/logo-baumwipfelpfad-elsass.svg";
                break;
            case "Salzkammergut":
                value = "images/svg/logo-baumwipfelpfad-salzkammergut.svg";
                break;
            default: //Bachledka, Krkonoše, Lipno, Pohorje
                value = "images/svg/baumwipfelpfade-logo.svg";
                break;
        }

        try {
            imageView.fitHeightProperty().bind(gridPane.heightProperty().multiply(0.55));
            imageView.fitWidthProperty().bind(gridPane.widthProperty().multiply(0.7));
            BufferedImage image = SvgImageLoader.loadSvg(getURL(value), 500);
            imageView.setImage(SwingFXUtils.toFXImage(image, null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pane.setStyle("-fx-background-repeat: no-repeat;" +
                "-fx-background-attachment: fixed;" +
                "-fx-background-size: 20% auto;" +
                "-fx-background-position: 103% 5%;");

        setStartText();
        questionnaireLabel.textProperty().bind(questionnaireText);
        warningLabel.textProperty().bind(questionnaireWarn);
        warningLabel.setStyle("-fx-text-fill: #c90000;");
    }

    public void setStartText() {
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

    public void makeQuestionnaire(List<Question> questions, boolean isPreview) throws IOException {
        SurveyController.setPreview(isPreview);
        List<SurveyPage> pages = getSurveyPages(questions);
        SurveyController.setPageCount(pages.size());
        createPanels(pages);
    }

    private List<SurveyPage> getSurveyPages(List<Question> questions) {
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

    public void createPanels(List<SurveyPage> pages) throws IOException {
        for (SurveyPage page : pages) {

            Pane scene = FXMLLoader.load(getURL(SceneName.SURVEY_PATH));

            setHeaderFields(scene, page.getPageNumber(), pages.size(), page.getHeadline());

            setQuestions(scene, page.getQuestions());

            if (needsEvaluationQuestionFooter(page.getQuestions())) {
                createEvaluationQuestionFooter(scene);
            }


            //screenController.addScreen("survey_" + page.getPageNumber(), scene);
        }
    }

    private boolean needsEvaluationQuestionFooter(List<Question> questions) {
        return questions.stream().anyMatch(question -> question.getFlags().isEvaluationQuestion());
    }

    private void setHeaderFields(Pane scene, int pageNumber, int pageCount, Headline headline) {
        ProgressBar progressBar = (ProgressBar) scene.lookup("#progressBar");
        progressBar.setProgress((float) (pageNumber) / (float) pageCount);

        Label lbl_count = (Label) scene.lookup("#lbl_count");
        lbl_count.setText(String.format("Frage %s/%s", (pageNumber), pageCount));

        if (headline != null) {
            Label lbl_headline = (Label) scene.lookup("#lbl_headline");
            lbl_headline.setText(removeMark(headline.getName()));
        }

    }

    private void setQuestions(Pane scene, List<Question> questions) {
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

    private void createEvaluationQuestionFooter(Pane scene) {
        HBox hBox = (HBox) scene.lookup("#footer_hBox");
        hBox.getChildren().add(new Label("0: keine Aussage"));
        hBox.getChildren().add(new Label("1: sehr schlecht"));
        hBox.getChildren().add(new Label("10: sehr gut"));
    }

    private void createMultipleChoiceQuestion(VBox innerVBox, Question question, HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap, BooleanProperty checkRequiredValue) {
        if (question.getFlags().isList()) {
            innerVBox.getChildren().add(createListViewHBox(question, booleanPropertyHashMap, checkRequiredValue));
        } else {
            innerVBox.getChildren().add(createCheckboxHBox(question, booleanPropertyHashMap, checkRequiredValue));
        }
    }

    private void createShortAnswerQuestion(VBox innerVBox, Question question, ValidationSupport validationSupport) {
        if (question.getFlags().isTextArea()) {
            innerVBox.getChildren().add(createTextAreaHBox(question, validationSupport));
        } else {
            innerVBox.getChildren().add(createTextFieldHBox(question, validationSupport));
        }
    }

    private HBox createTextFieldHBox(Question question, ValidationSupport validationSupport) {
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

    private HBox createTextAreaHBox(Question question, ValidationSupport validationSupport) {
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

    private String regexOrEmpty(String regex) {
        return String.format("(^$|%s)", regex);
    }

    private HBox createListViewHBox(Question question, HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap, BooleanProperty checkRequiredValue) {
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

    private HBox createCheckboxHBox(Question question, HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap, BooleanProperty checkRequiredValue) {
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

    private void checkObservableValues(BooleanProperty checkRequiredValue, HashMap<Integer, List<BooleanProperty>> booleanPropertyHashMap) {
        List<Boolean> booleans = new ArrayList<>();

        for (List<BooleanProperty> booleanProperties : booleanPropertyHashMap.values()) {
            booleans.add(booleanProperties.stream().anyMatch(ObservableBooleanValue::get));
        }
        checkRequiredValue.setValue(booleans.stream().allMatch(aBoolean -> aBoolean));
    }


    private boolean isNewHeadline(Headline currentHeadline, Headline headline) {
        if (currentHeadline != null && headline != null) {
            return !currentHeadline.equals(headline);
        } else {
            return currentHeadline == null ^ headline == null;
        }
    }

    private Label createQuestionLabel(Pane screen, Question question) {
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

    private String removeMark(String text) {
        Pattern MY_PATTERNs = Pattern.compile("#\\[[0-9]+\\]");
        Matcher ms = MY_PATTERNs.matcher(text);
        if (ms.find()) {
            System.out.println("Old dataset pattern found.");
        }

        return text;
    }

    private String addRequiredTag(String text, boolean required) {
        return required ? text + " *" : text;
    }

    @FXML
    private void adminLogin() {
        if (GlobalVars.DEV_MODE) {
            loginController.devLogin();
        } else {
            screenController.activate(LoginController.class);
        }
    }

    @FXML
    private void next() throws IOException {
        List<Question> questions = questionListService.getQuestions(GlobalVars.activeQuestionnaire.getId());
        if (questions.isEmpty()) {
            NotificationController.createErrorMessage(MessageId.TITLE_QUESTIONNAIRE, MessageId.MESSAGE_QUESTIONNAIRE_IS_EMPTY);
            return;
        }
        makeQuestionnaire(questions, false);
        screenController.activate(SurveyController.class);
    }
}
