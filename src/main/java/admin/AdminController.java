package admin;

import application.DialogMessageController;
import application.GlobalVars;
import application.NotificationController;
import application.ScreenController;
import export.ExportController;
import export.impl.ExportControllerImpl;
import javafx.application.Platform;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import message.DialogId;
import message.MessageId;
import model.Questionnaire;
import model.SceneName;
import model.tableObject.QuestionnaireTableObject;
import model.tableObject.converter.QuestionnaireTableObjectConverter;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import questionList.QuestionListController;
import start.StartController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static application.DialogMessageController.getDialogMessage;
import static application.GlobalFuncs.getURL;
import static application.ScreenController.STYLESHEET;
import static application.TableColumnNameController.getColumnName;
import static message.TableColumnNameId.ADMIN_ACTIVE;
import static message.TableColumnNameId.ADMIN_COPY;
import static message.TableColumnNameId.ADMIN_FINAL;
import static message.TableColumnNameId.ADMIN_RENAME;
import static message.TableColumnNameId.ADMIN_SQL_EXPORT;
import static message.TableColumnNameId.ADMIN_XLS_EXPORT;
import static message.TableColumnNameId.DELETE;
import static message.TableColumnNameId.EDIT;

public class AdminController {

    private static final ObservableList<QuestionnaireTableObject> data = FXCollections.observableArrayList();

    public Button btn_loc;
    @FXML
    private TableView<QuestionnaireTableObject> questionnaireTableView;
    @FXML
    private TableColumn<QuestionnaireTableObject, String> nameColumn;
    @FXML
    private TableColumn<QuestionnaireTableObject, String> dateColumn;
    @FXML
    @SuppressWarnings("FieldMayBeFinal")
    private TableColumn<QuestionnaireTableObject, Boolean> activeColumn = new TableColumn<>(getColumnName(ADMIN_ACTIVE));
    @FXML
    @SuppressWarnings("FieldMayBeFinal")
    private TableColumn<QuestionnaireTableObject, Boolean> finalColumn = new TableColumn<>(getColumnName(ADMIN_FINAL));
    @FXML
    private final TableColumn<QuestionnaireTableObject, String> editButtonColumn = new TableColumn<>(getColumnName(EDIT));
    @FXML
    private final TableColumn<QuestionnaireTableObject, String> copyButtonColumn = new TableColumn<>(getColumnName(ADMIN_COPY));
    @FXML
    private final TableColumn<QuestionnaireTableObject, String> renameButtonColumn = new TableColumn<>(getColumnName(ADMIN_RENAME));
    @FXML
    private final TableColumn<QuestionnaireTableObject, String> sqlExportButtonColumn = new TableColumn<>(getColumnName(ADMIN_SQL_EXPORT));
    @FXML
    private final TableColumn<QuestionnaireTableObject, String> xlsExportButtonColumn = new TableColumn<>(getColumnName(ADMIN_XLS_EXPORT));
    @FXML
    private final TableColumn<QuestionnaireTableObject, String> deleteButtonColumn = new TableColumn<>(getColumnName(DELETE));

    /**
     * The constructor (is called before the initialize()-method).
     */
    public AdminController() {
        getData();
        data.addListener((ListChangeListener<QuestionnaireTableObject>) change -> {
            change.next();
            if (questionnaireTableView != null) {
                questionnaireTableView.refresh();
            }
        });
    }

    private static void getData() {
        data.clear();
        List<QuestionnaireTableObject> tableObjects =
                QuestionnaireTableObjectConverter.convert(Objects.requireNonNull(AdminService.getQuestionnaires(GlobalVars.location)));
        data.addAll(Objects.requireNonNull(tableObjects));
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        questionnaireTableView.setItems(data);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.NAME));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.DATE));
        activeColumn.setCellValueFactory(cellData -> {
            Questionnaire questionnaire = cellData.getValue();
            ObservableBooleanValue property = questionnaire.isActive();

            property.addListener((observable, oldValue, newValue) -> {
                questionnaire.setActive(newValue);
                if (newValue) {
                    AdminService.activateQuestionnaire(questionnaire.getId());
                    GlobalVars.activeQuestionnaire = questionnaire;
                    deactivateAllOtherQuestionnaires(questionnaire);
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

        editButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.EDIT));
        questionnaireTableView.getColumns().add(editButtonColumn);

        copyButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.COPY));
        questionnaireTableView.getColumns().add(copyButtonColumn);

        renameButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.RENAME));
        questionnaireTableView.getColumns().add(renameButtonColumn);

        sqlExportButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.SQL_EXPORT));
        questionnaireTableView.getColumns().add(sqlExportButtonColumn);

        xlsExportButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.XLS_EXPORT));
        questionnaireTableView.getColumns().add(xlsExportButtonColumn);

        deleteButtonColumn.setCellValueFactory(new PropertyValueFactory<>(Questionnaire.DELETE));
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
                        getURL(SceneName.QUESTION_LIST_PATH));
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
            DialogMessageController.setMessage(dialog,
                    DialogId.TITLE_COPY_QUESTIONNAIRE,
                    DialogId.CONTENT_TEXT_COPY_QUESTIONNAIRE);
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(ort -> {
                        if (AdminService.copyQuestionnaire(questionnaire, ort)) {
                            getData();
                            NotificationController.createMessage(
                                    MessageId.TITLE_COPY_QUESTIONNAIRE,
                                    MessageId.MESSAGE_COPY_QUESTIONNAIRE,
                                    questionnaire.getName(),
                                    ort);
                        } else {
                            NotificationController.createErrorMessage(
                                    MessageId.TITLE_COPY_QUESTIONNAIRE,
                                    MessageId.MESSAGE_UNDEFINED_ERROR);
                        }
                    }
            );

        });

        return button;
    }

    public static Button initRenameButton(Questionnaire questionnaire) {
        ValidationSupport validationSupport = new ValidationSupport();
        ImageView imgView = new ImageView(GlobalVars.IMG_REN);
        imgView.setFitHeight(30);
        imgView.setFitWidth(30);
        Button button = new Button("", imgView);
        button.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("");
            DialogMessageController.setMessage(dialog,
                    DialogId.TITLE_RENAME_QUESTIONNAIRE,
                    DialogId.CONTENT_TEXT_RENAME_QUESTIONNAIRE);
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getStylesheets().add(
                    getURL(STYLESHEET).toExternalForm());

            validationSupport.setErrorDecorationEnabled(false);
            validationSupport.registerValidator(dialog.getEditor(), Validator.createEmptyValidator("FEHLER"));
            dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validationSupport.invalidProperty());

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
            //TODO replace with backend sync
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
                if (exportController.excelNeu(
                        String.format("%s_%s_%s.xlsx", questionnaire.getId(), questionnaire.getLocation(), questionnaire.getName()),
                        questionnaire,
                        dates.getKey(),
                        dates.getValue())) {
                    NotificationController.createMessage(
                            MessageId.TITLE_EXCEL_EXPORT,
                            MessageId.MESSAGE_EXPORTED_QUESTIONNAIRE_SUCCESSFULLY);
                } else {
                    NotificationController.createErrorMessage(
                            MessageId.TITLE_EXCEL_EXPORT,
                            MessageId.MESSAGE_UNDEFINED_ERROR);
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
            DialogMessageController.setMessage(alert,
                    DialogId.TITLE_REMOVE_QUESTIONNAIRE,
                    DialogId.HEADER_TEXT_REMOVE_QUESTIONNAIRE,
                    DialogId.CONTENT_TEXT_REMOVE_QUESTIONNAIRE);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (AdminService.deleteQuestionnaire(questionnaire.getId())) {
                    getData();
                    NotificationController.createErrorMessage(
                            MessageId.TITLE_REMOVE_QUESTIONNAIRE,
                            MessageId.MESSAGE_DELETED_QUESTIONNAIRE_SUCCESSFULLY,
                            questionnaire.getName());
                } else {
                    NotificationController.createErrorMessage(
                            MessageId.TITLE_REMOVE_QUESTIONNAIRE,
                            MessageId.MESSAGE_UNDEFINED_ERROR);
                }
            }
        });

        return button;
    }

    private static Optional<Pair<String, String>> getDatePickerDialog() {
        ValidationSupport validationSupport = new ValidationSupport();

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        DialogMessageController.setMessage(dialog, DialogId.TITLE_EXCEL_EXPORT);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        ButtonType okButtonType = new ButtonType(
                getDialogMessage(DialogId.BUTTON_EXCEL_EXPORT),
                ButtonData.OK_DONE
        );
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePickerFrom = new DatePicker(LocalDate.now());
        datePickerFrom.setPromptText(getDialogMessage(DialogId.DATEPICKER_EXPORT_FROM));
        DatePicker datePickerTo = new DatePicker(LocalDate.now());
        datePickerTo.setPromptText(getDialogMessage(DialogId.DATEPICKER_EXPORT_TO));

        validationSupport.setErrorDecorationEnabled(false);
        Predicate<LocalDate> predicate = (localDate) ->
                datePickerFrom.getValue() != null &&
                        datePickerFrom.getValue() != null &&
                        (datePickerFrom.getValue().isBefore(datePickerTo.getValue()) || datePickerFrom.getValue().equals(datePickerTo.getValue())
                        );
        Platform.runLater(() -> {
            validationSupport.registerValidator(datePickerFrom, Validator.createEmptyValidator("FEHLER"));
            validationSupport.registerValidator(datePickerTo, Validator.createEmptyValidator("FEHLER2"));
            validationSupport.registerValidator(datePickerFrom, Validator.createPredicateValidator(predicate, ""));
            validationSupport.registerValidator(datePickerTo, Validator.createPredicateValidator(predicate, ""));
        });
        dialog.getDialogPane().lookupButton(okButtonType).disableProperty().bind(validationSupport.invalidProperty());

        grid.add(new Label(getDialogMessage(DialogId.DATEPICKER_EXPORT_FROM_LABEL)), 0, 0);
        grid.add(datePickerFrom, 1, 0);
        grid.add(new Label(getDialogMessage(DialogId.DATEPICKER_EXPORT_TO_LABEL)), 0, 1);
        grid.add(datePickerTo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(datePickerFrom::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(datePickerFrom.getValue().toString(), datePickerTo.getValue().toString());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void deactivateAllOtherQuestionnaires(Questionnaire activeQuestionnaire) {
        for (Questionnaire questionnaire : data) {
            if (questionnaire.isActive().get() && questionnaire.getId() != activeQuestionnaire.getId()) {
                AdminService.disableQuestionnaire(questionnaire.getId());
            }
        }
    }

    @FXML
    private void newQuestionnaire() {
        ValidationSupport validationSupport = new ValidationSupport();
        TextInputDialog dialog = new TextInputDialog("");
        DialogMessageController.setMessage(
                dialog,
                DialogId.TITLE_CREATE_QUESTIONNAIRE,
                DialogId.CONTENT_TEXT_CREATE_QUESTIONNAIRE);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getURL(STYLESHEET).toExternalForm());

        validationSupport.setErrorDecorationEnabled(false);
        validationSupport.registerValidator(dialog.getEditor(), Validator.createEmptyValidator("FEHLER"));
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(validationSupport.invalidProperty());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (AdminService.createQuestionnaire(name) != -1) {
                getData();
                questionnaireTableView.refresh();
            }
        });
    }

    @FXML
    private void logout() throws IOException {
        LoginService.login("usr", "Q#DQ8Ka&9Vq6`;)s");
        ScreenController.activate(SceneName.START);
    }

    @FXML
    private void changeLocation() throws IOException {
        ScreenController.activate(SceneName.LOCATION);
    }

    @FXML
    private void exit() {
        System.exit(0);
    }

}
