package questionList;

import application.GlobalVars;
import application.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import model.Question;
import model.Questionnaire;
import model.SceneName;
import question.QuestionController;

import java.io.IOException;
import java.util.List;

import static application.GlobalFuncs.getURL;

public class QuestionListController {
    public static Questionnaire questionnaire;
    private List<Question> questions;
    private ObservableList<Question> data = FXCollections.observableArrayList();

    @FXML
    private TableView<Question> tbl_fragen;
    @FXML
    private TableColumn<Question, String> nameCol;
    @FXML
    private TableColumn<Question, String> katCol;
    @FXML
    private TableColumn<Question, Integer> posCol;
    @FXML
    private TableColumn<Question, String> artCol;
    @FXML
    private TableColumn<Question, String> actionCol = new TableColumn<>("Bearbeiten");
    //@FXML
    //private TableColumn<Fragebogen, String> copCol = new TableColumn<>("Kopieren");
    @FXML
    private TableColumn<Question, String> delCol = new TableColumn<>("Löschen");

    /**
     * The constructor (is called before the initialize()-method).
     */
    public QuestionListController() {
        getData();
    }

    private void getData() {
        data.clear();
        questions = QuestionListService.getQuestions(questionnaire.getId());
        data.addAll(questions);
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        tbl_fragen.setItems(data);

        nameCol.setCellValueFactory(new PropertyValueFactory<>("frage"));
        katCol.setCellValueFactory(new PropertyValueFactory<>("kategorie"));
        posCol.setCellValueFactory(new PropertyValueFactory<>("Position"));
        artCol.setCellValueFactory(new PropertyValueFactory<>("art"));

        actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Question, String>, TableCell<Question, String>> cellFactory
                = //
                new Callback<TableColumn<Question, String>, TableCell<Question, String>>() {
                    @Override
                    public TableCell<Question, String> call(final TableColumn<Question, String> param) {
                        ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
                        imgView.setFitHeight(30);
                        imgView.setFitWidth(30);
                        return new TableCell<Question, String>() {

                            Button btn = new Button("", imgView);
                            //final Button btn = new Button("EDIT");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        QuestionController.questionnaire = questionnaire;
                                        QuestionController.question = getTableView().getItems().get(getIndex());
                                        try {
                                            ScreenController.addScreen(SceneName.QUESTION,
                                                    FXMLLoader.load(getURL(SceneName.QUESTION_PATH)));
                                            ScreenController.activate(SceneName.QUESTION);
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };

        actionCol.setCellFactory(cellFactory);
        tbl_fragen.getColumns().add(actionCol);


        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Question, String>, TableCell<Question, String>> cellFactoryDel
                = //
                new Callback<TableColumn<Question, String>, TableCell<Question, String>>() {
                    @Override
                    public TableCell<Question, String> call(final TableColumn<Question, String> param) {
                        ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
                        imgView.setFitHeight(30);
                        imgView.setFitWidth(30);

                        return new TableCell<Question, String>() {
                            Button btn = new Button("", imgView);
                            //final Button btn = new Button("DEL");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        Question question = getTableView().getItems().get(getIndex());
                                        QuestionListService.deleteQuestion(questionnaire.getId(), question);
                                        getData();
                                        tbl_fragen.refresh();
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                    }
                };

        delCol.setCellFactory(cellFactoryDel);
        tbl_fragen.getColumns().add(delCol);

    }

    @FXML
    private void save() {
        ScreenController.activate(SceneName.ADMIN);
    }

    @FXML
    private void newQuestion() {
        QuestionController.questionnaire = questionnaire;
        System.out.println(questions);
        System.out.println(questions.size());

        QuestionController.question = new Question(questions.size());
        try {
            ScreenController.addScreen(SceneName.QUESTION,
                    FXMLLoader.load(getURL(SceneName.QUESTION_PATH)));
            ScreenController.activate(SceneName.QUESTION);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
