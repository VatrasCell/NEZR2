package admin;

import application.GlobalVars;
import application.ScreenController;
import export.ExportController;
import export.impl.ExportControllerImpl;
import javafx.application.Platform;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import login.LoginService;
import model.Questionnaire;
import model.SceneName;
import org.controlsfx.control.Notifications;
import questionList.QuestionListController;
import start.StartController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static application.GlobalFuncs.getURL;
import static application.ScreenController.styleSheet;

public class AdminController {

    private static ObservableList<Questionnaire> data = FXCollections.observableArrayList();

    public Button btn_loc;
    @FXML
    private TableView<Questionnaire> questionnaireTableView;
    @FXML
    private TableColumn<Questionnaire, String> nameColumn;
    @FXML
    private TableColumn<Questionnaire, String> dateColumn;
    @FXML
    private TableColumn<Questionnaire, Boolean> activeColumn = new TableColumn<>("Aktiv");
    @FXML
    private TableColumn<Questionnaire, Boolean> finalColumn = new TableColumn<>("Final");
    @FXML
    private TableColumn<Questionnaire, String> editButtonColumn = new TableColumn<>("Bearbeiten");
    @FXML
    private TableColumn<Questionnaire, String> copyButtonColumn = new TableColumn<>("Kopieren");
    @FXML
    private TableColumn<Questionnaire, String> renameButtonColumn = new TableColumn<>("Umbenennen");
    @FXML
    private TableColumn<Questionnaire, String> sqlExportButtonColumn = new TableColumn<>("SQL Export");
    @FXML
    private TableColumn<Questionnaire, String> xlsExportButtonColumn = new TableColumn<>("XLS Export");
    @FXML
    private TableColumn<Questionnaire, String> deleteButtonColumn = new TableColumn<>("L\u00f6schen");

    /**
     * The constructor (is called before the initialize()-method).
     */
    public AdminController() {
        getData();
        data.addListener((ListChangeListener<Questionnaire>) change -> {
            change.next();
            if(questionnaireTableView != null) {
                questionnaireTableView.refresh();
            }
        });
    }

    private static void getData() {
        data.clear();
        ArrayList<Questionnaire> questionnaires = AdminService.getQuestionnaires(GlobalVars.location);
        data.addAll(Objects.requireNonNull(questionnaires));
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        questionnaireTableView.setItems(data);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        activeColumn.setCellValueFactory(cellData -> {
            Questionnaire questionnaire = cellData.getValue();
            ObservableBooleanValue property = questionnaire.isActive();
            
            property.addListener((observable, oldValue, newValue) -> {
                questionnaire.setActive(newValue);
                if (newValue) {
                    AdminService.activateQuestionnaire(questionnaire.getId());
                    GlobalVars.activeQuestionnaire = questionnaire;
                } else {
                    AdminService.disableQuestionnaire(questionnaire.getId());
                    GlobalVars.activeQuestionnaire = null;
                }
                StartController.setStartText();
                getData();
                questionnaireTableView.refresh();
            });

            return property;
        });
        activeColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeColumn));
        finalColumn.setCellValueFactory(cellData -> {

            Questionnaire cellValue = cellData.getValue();
            ObservableBooleanValue property = cellValue.isFinal();

            property.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    AdminService.setFinal(cellValue);
                } else {
                    AdminService.setUnFinal(cellValue);
                }
                cellValue.setFinal(newValue);
            });

            return property;
        });
        finalColumn.setCellFactory(CheckBoxTableCell.forTableColumn(finalColumn));

        editButtonColumn.setCellValueFactory(new PropertyValueFactory<>("edit"));
        questionnaireTableView.getColumns().add(editButtonColumn);

        copyButtonColumn.setCellValueFactory(new PropertyValueFactory<>("copy"));
        questionnaireTableView.getColumns().add(copyButtonColumn);

        renameButtonColumn.setCellValueFactory(new PropertyValueFactory<>("rename"));
        questionnaireTableView.getColumns().add(renameButtonColumn);

        sqlExportButtonColumn.setCellValueFactory(new PropertyValueFactory<>("sqlExport"));
        questionnaireTableView.getColumns().add(sqlExportButtonColumn);

        xlsExportButtonColumn.setCellValueFactory(new PropertyValueFactory<>("xlsExport"));
        questionnaireTableView.getColumns().add(xlsExportButtonColumn);

        deleteButtonColumn.setCellValueFactory(new PropertyValueFactory<>("delete"));
        questionnaireTableView.getColumns().add(deleteButtonColumn);

    }

    public static Button initEditButton(Questionnaire questionnaire) {
        ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            try {
                QuestionListController.questionnaire = questionnaire;
                ScreenController.addScreen(SceneName.QUESTION_LIST,
                        FXMLLoader.load(getURL(SceneName.QUESTION_LIST_PATH)));
                ScreenController.activate(SceneName.QUESTION_LIST);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return button;
    }

    public static Button initCopyButton(Questionnaire questionnaire) {
        ImageView imgView = new ImageView(GlobalVars.IMG_COP);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(GlobalVars.location, GlobalVars.locations);
            dialog.setTitle("Fragebogen kopieren");
            dialog.setHeaderText("Fragebogen kopieren");
            dialog.setContentText("Standort wählen:");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(getURL(styleSheet).toExternalForm());

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(ort -> {
                        if (AdminService.copyQuestionnaire(questionnaire, ort)) {
                            getData();
                            Notifications.create().title("Fragebogen kopieren").text("Der Fragebogen \"" + questionnaire.getName() + "\" wurde erfolgreich\nnach \"" + ort + "\" kopiert.").show();
                        } else {
                            Notifications.create().title("Fragebogen kopieren").text("Ein Fehler ist aufgetreten.").showError();
                        }
                    }
            );

        });

        return button;
    }

    public static Button initRenameButton(Questionnaire questionnaire) {
        ImageView imgView = new ImageView(GlobalVars.IMG_REN);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Fragebogen umbenennen");
            dialog.setContentText("neuer Name:");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(
                    getURL("style/application.css").toExternalForm());

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(questionnaire::setName);

            if (AdminService.renameQuestionnaire(questionnaire)) {
                getData();
            }
        });

        return button;
    }

    public static Button initSqlExportButton(Questionnaire questionnaire) {
        ImageView imgView = new ImageView(GlobalVars.IMG_SQL);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            //TODO
        });

        return button;
    }

    public static Button initXlsExportButton(Questionnaire questionnaire) {
        ImageView imgView = new ImageView(GlobalVars.IMG_XLS);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            ExportController exportController = new ExportControllerImpl();
            Optional<Pair<String, String>> result = getDatePickerDialog();
            result.ifPresent(dates -> {
                if (exportController.excelNeu(questionnaire.getId() + "_" + questionnaire.getOrt() + "_" + questionnaire.getName() + ".xlsx", questionnaire,
                        dates.getKey(), dates.getValue())) {
                    Notifications.create().title("Excel Export").text("Export erfolgreich abgeschlossen.").show();
                } else {
                    Notifications.create().title("Excel Export").text("Ein Fehler ist aufgetreten.").showError();
                }
            });
        });

        return button;
    }

    public static Button initDeleteButton(Questionnaire questionnaire) {
        ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Fragebogen löschen");
            alert.setHeaderText("Wollen Sie den Fragebogen wirklich löschen?");
            alert.setContentText("Fortfahren?");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (AdminService.deleteQuestionnaire(questionnaire.getId())) {
                    getData();
                    Notifications.create().title("Fragebogen löschen").text("Fragebogen \"" + questionnaire.getName() + "\" wurde erfolgreich abgeschlossen.").show();
                } else {
                    Notifications.create().title("Fragebogen löschen").text("Ein Fehler ist aufgetreten.").showError();
                }
            }
        });

        return button;
    }

    private static Optional<Pair<String, String>> getDatePickerDialog() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Excel Export");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());

        ButtonType okButtonType = new ButtonType("Export", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker von = new DatePicker(LocalDate.now());
        von.setPromptText("von");
        DatePicker bis = new DatePicker(LocalDate.now());
        von.setPromptText("bis");

        grid.add(new Label("Von: "), 0, 0);
        grid.add(von, 1, 0);
        grid.add(new Label("Bis: "), 0, 1);
        grid.add(bis, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(von::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(von.getValue().toString(), bis.getValue().toString());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @FXML
    private void newQuestionnaire() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Fragebogen erstellen");
        dialog.setContentText("Name:");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (AdminService.createQuestionnaire(name) != -1) {
                getData();
                questionnaireTableView.refresh();
            }
        });
    }

    @FXML
    private void logout() {
        LoginService.login("usr", "Q#DQ8Ka&9Vq6`;)s");
        ScreenController.activate(SceneName.START);
    }

    @FXML
    private void changeLocation() {
        ScreenController.activate(SceneName.LOCATION);
    }

    @FXML
    private void exit() {
        System.exit(0);
    }

}
