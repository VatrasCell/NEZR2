package de.vatrascell.nezr.questionList;

import de.vatrascell.nezr.application.GlobalVars;
import de.vatrascell.nezr.application.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import de.vatrascell.nezr.model.Question;
import de.vatrascell.nezr.model.Questionnaire;
import de.vatrascell.nezr.model.SceneName;
import de.vatrascell.nezr.model.tableObject.QuestionTableObject;
import de.vatrascell.nezr.model.tableObject.converter.QuestionTableObjectConverter;
import de.vatrascell.nezr.question.QuestionController;
import de.vatrascell.nezr.question.QuestionService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static de.vatrascell.nezr.application.GlobalFuncs.getURL;
import static de.vatrascell.nezr.application.TableColumnNameController.getColumnName;
import static de.vatrascell.nezr.message.TableColumnNameId.DELETE;
import static de.vatrascell.nezr.message.TableColumnNameId.EDIT;

public class QuestionListController {
    public static Questionnaire questionnaire;
    private static final ObservableList<QuestionTableObject> data = FXCollections.observableArrayList();

    @FXML
    private TableView<QuestionTableObject> questionTable;
    @FXML
    private TableColumn<QuestionTableObject, String> nameColumn;
    @FXML
    private TableColumn<QuestionTableObject, String> categoryColumn;
    @FXML
    private TableColumn<QuestionTableObject, Integer> positionColumn;
    @FXML
    private TableColumn<QuestionTableObject, String> questionTypeColumn;
    @FXML
    private final TableColumn<QuestionTableObject, String> editButtonColumn = new TableColumn<>(getColumnName(EDIT));
    //@FXML
    //private TableColumn<QuestionTableObject, String> copCol = new TableColumn<>("Kopieren");
    @FXML
    private final TableColumn<QuestionTableObject, String> deleteButtonColumn = new TableColumn<>(getColumnName(DELETE));

    /**
     * The constructor (is called before the initialize()-method).
     */
    public QuestionListController() {
        getData();
    }

    private static void getData() {
        data.clear();
        List<QuestionTableObject> tableObjects =
                QuestionTableObjectConverter.convert(Objects.requireNonNull(QuestionListService.getQuestions(questionnaire.getId())));
        data.addAll(Objects.requireNonNull(tableObjects));
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        questionTable.setItems(data);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>(Question.QUESTION));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>(Question.CATEGORY));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>(Question.POSITION));
        questionTypeColumn.setCellValueFactory(new PropertyValueFactory<>(Question.QUESTION_TYPE));

        editButtonColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        editButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Question.EDIT));
        questionTable.getColumns().add(editButtonColumn);

        deleteButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Question.DELETE));
        questionTable.getColumns().add(deleteButtonColumn);
    }

    public static Button initEditButton(Question question) {
        ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            QuestionController.questionnaire = questionnaire;
            QuestionController.question = question;
            try {
                ScreenController.addScreen(SceneName.QUESTION, getURL(SceneName.QUESTION_PATH));
                ScreenController.activate(SceneName.QUESTION);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        return button;
    }

    public static Button initDeleteButton(Question question) {
        ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            QuestionListService.deleteQuestion(questionnaire.getId(), question);
            getData();
        });

        return button;
    }

    @FXML
    private void save() throws IOException {
        ScreenController.activate(SceneName.ADMIN);
    }

    @FXML
    private void newQuestion() {
        QuestionController.questionnaire = questionnaire;

        QuestionController.question = new Question(QuestionService.getMaxQuestionPosition(questionnaire.getId()));
        try {
            ScreenController.addScreen(SceneName.QUESTION, getURL(SceneName.QUESTION_PATH));
            ScreenController.activate(SceneName.QUESTION);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
