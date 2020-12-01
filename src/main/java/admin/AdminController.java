package admin;

import application.GlobalVars;
import application.ScreenController;
import export.ExportController;
import export.impl.ExportControllerImpl;
import javafx.application.Platform;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;
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

	private ArrayList<Questionnaire> questionnaire;
	private ObservableList<Questionnaire> data = FXCollections.observableArrayList();
	
	
	@FXML
	private Button btn_loc;
	@FXML
	private TableView<Questionnaire> tbl_fragebogen;
	@FXML
	private TableColumn<Questionnaire, String> nameCol;
	@FXML
	private TableColumn<Questionnaire, String> dateCol;
	@FXML
	private TableColumn<Questionnaire, Boolean> activCol = new TableColumn<>("Aktiv");
	@FXML
	private TableColumn<Questionnaire, Boolean> finalCol = new TableColumn<>("Final");
	@FXML
	private TableColumn<Questionnaire, String> actionCol = new TableColumn<>("Bearbeiten");
	@FXML
	private TableColumn<Questionnaire, String> copCol = new TableColumn<>("Kopieren");
	@FXML
	private TableColumn<Questionnaire, String> renCol = new TableColumn<>("Umbenennen");
	@FXML
	private TableColumn<Questionnaire, String> sqlCol = new TableColumn<>("SQL Export");
	@FXML
	private TableColumn<Questionnaire, String> xlsCol = new TableColumn<>("XLS Export");
	@FXML
	private TableColumn<Questionnaire, String> delCol = new TableColumn<>("Löschen");
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public AdminController() {
		getData();
		
	}
	
	private void getData() {
		data.clear();
		questionnaire = AdminService.getQuestionnaires(GlobalVars.location);
		//System.out.println(fragebogen.toString());
		data.addAll(Objects.requireNonNull(questionnaire));
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		tbl_fragebogen.setItems(data);
		
		nameCol.setCellValueFactory(new PropertyValueFactory<Questionnaire, String>("name"));
		dateCol.setCellValueFactory(new PropertyValueFactory<Questionnaire, String>("date"));
		//activCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, Boolean>("activ"));
		activCol.setCellValueFactory(cellData -> {
            Questionnaire questionnaire = cellData.getValue();
            ObservableBooleanValue property = questionnaire.isActive();

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> {
            	questionnaire.setActive(newValue);
            	if(newValue) {
            		AdminService.activateQuestionnaire(questionnaire.getId());
            		GlobalVars.activeQuestionnaire = questionnaire;
            	} else {
            		AdminService.disableQuestionnaire(questionnaire.getId());
            		GlobalVars.activeQuestionnaire = null;
            	}
            	StartController.setStartText();
        		getData();
            	tbl_fragebogen.refresh();
            	});

            return property;
        });
		activCol.setCellFactory(CheckBoxTableCell.forTableColumn(activCol));	
		//finalCol.setCellValueFactory(new PropertyValueFactory<Fragebogen, Boolean>("final"));
		finalCol.setCellValueFactory(cellData -> {

            Questionnaire cellValue = cellData.getValue();
            ObservableBooleanValue property = cellValue.isFinal();

            // Add listener to handler change
            property.addListener((observable, oldValue, newValue) -> {
            	if(newValue) {
            		AdminService.setFinal(cellValue);
            	} else {
            		AdminService.setUnFinal(cellValue);
            	}
            	cellValue.setFinal(newValue);
            });

            return property;
        });
		finalCol.setCellFactory(CheckBoxTableCell.forTableColumn(finalCol));
		
		actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>> cellFactory
                = //
                new Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>>() {
            @Override
            public TableCell<Questionnaire, String> call(final TableColumn<Questionnaire, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_EDT);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
        		Button btn = new Button("", imgView);
                btn.setPadding(Insets.EMPTY);
				return new TableCell<Questionnaire, String>() {

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								try {
									QuestionListController.questionnaire = getTableView().getItems().get(getIndex());
									ScreenController.addScreen(SceneName.QUESTION_LIST,
											FXMLLoader.load(getURL(SceneName.QUESTION_LIST_PATH)));
									ScreenController.activate(SceneName.QUESTION_LIST);
								} catch (IOException e) {
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
        tbl_fragebogen.getColumns().add(actionCol);
        
        copCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>> cellFactoryCop
                = //
                new Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>>() {
            @Override
            public TableCell<Questionnaire, String> call(final TableColumn<Questionnaire, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_COP);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Questionnaire, String>() {

					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Questionnaire questionnaire1 = getTableView().getItems().get(getIndex());

								ChoiceDialog<String> dialog = new ChoiceDialog<>(GlobalVars.location, GlobalVars.locations);
								dialog.setTitle("Fragebogen kopieren");
								dialog.setHeaderText("Fragebogen kopieren");
								dialog.setContentText("Standort wählen:");
								DialogPane dialogPane = dialog.getDialogPane();
								dialogPane.getStylesheets().add(getURL(styleSheet).toExternalForm());

								Optional<String> result = dialog.showAndWait();
								result.ifPresent(ort -> {
										if(AdminService.copyQuestionnaire(questionnaire1, ort)) {
											getData();
											tbl_fragebogen.refresh();
											Notifications.create().title("Fragebogen kopieren").text("Der Fragebogen \"" + questionnaire1.getName() + "\" wurde erfolgreich\nnach \"" + ort  + "\" kopiert.").show();
										} else {
											Notifications.create().title("Fragebogen kopieren").text("Ein Fehler ist aufgetreten.").showError();
										}
									}
								);

							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        copCol.setCellFactory(cellFactoryCop);
        tbl_fragebogen.getColumns().add(copCol);
        
        renCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>> cellFactoryRen
                = //
                new Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>>() {
            @Override
            public TableCell<Questionnaire, String> call(final TableColumn<Questionnaire, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_REN);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Questionnaire, String>() {
					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Questionnaire questionnaire1 = getTableView().getItems().get(getIndex());
								TextInputDialog dialog = new TextInputDialog("");
								dialog.setTitle("Fragebogen umbenennen");
								dialog.setContentText("neuer Name:");
								DialogPane dialogPane = dialog.getDialogPane();
								dialogPane.getStylesheets().add(
								   getURL("style/application.css").toExternalForm());

								Optional<String> result = dialog.showAndWait();
								result.ifPresent(questionnaire1::setName);

								if(AdminService.renameQuestionnaire(questionnaire1)) {
									getData();
									tbl_fragebogen.refresh();
								}
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        renCol.setCellFactory(cellFactoryRen);
        tbl_fragebogen.getColumns().add(renCol);
        
        sqlCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>> cellFactorySql
                = //
                new Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>>() {
            @Override
            public TableCell<Questionnaire, String> call(final TableColumn<Questionnaire, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_SQL);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Questionnaire, String>() {
					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Questionnaire questionnaire1 = getTableView().getItems().get(getIndex());

							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        sqlCol.setCellFactory(cellFactorySql);
        tbl_fragebogen.getColumns().add(sqlCol);
        
        xlsCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>> cellFactoryXls
                = //
                new Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>>() {
            @Override
            public TableCell<Questionnaire, String> call(final TableColumn<Questionnaire, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_XLS);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Questionnaire, String>() {
					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Questionnaire questionnaire1 = getTableView().getItems().get(getIndex());
								ExportController exportController = new ExportControllerImpl();
								Optional<Pair<String, String>> result = getDatePickerDialog();
								result.ifPresent(dates -> {
									//System.out.println("Von=" + usernamePassword.getKey() + ", Bis=" + usernamePassword.getValue());
									if( exportController.excelNeu(questionnaire1.getId() + "_" + questionnaire1.getOrt() + "_" + questionnaire1.getName() + ".xlsx", questionnaire1,
									dates.getKey(), dates.getValue())) {
										Notifications.create().title("Excel Export").text("Export erfolgreich abgeschlossen.").show();
									} else {
										Notifications.create().title("Excel Export").text("Ein Fehler ist aufgetreten.").showError();
									}
								});
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        xlsCol.setCellFactory(cellFactoryXls);
        tbl_fragebogen.getColumns().add(xlsCol);
        
        delCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>> cellFactoryDel
                = //
                new Callback<TableColumn<Questionnaire, String>, TableCell<Questionnaire, String>>() {
            @Override
            public TableCell<Questionnaire, String> call(final TableColumn<Questionnaire, String> param) {
            	ImageView imgView = new ImageView(GlobalVars.IMG_DEL);
            	imgView.setFitHeight(30);
        		imgView.setFitWidth(30);
				return new TableCell<Questionnaire, String>() {
					final Button btn = new Button("", imgView);

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								Questionnaire questionnaire1 = getTableView().getItems().get(getIndex());
								Alert alert = new Alert(AlertType.CONFIRMATION);
								alert.setTitle("Fragebogen löschen");
								alert.setHeaderText("Wollen Sie den Fragebogen wirklich löschen?");
								alert.setContentText("Fortfahren?");

								DialogPane dialogPane = alert.getDialogPane();
								dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());

								Optional<ButtonType> result = alert.showAndWait();
								if (result.isPresent() && result.get() == ButtonType.OK){
									if(AdminService.deleteQuestionnaire(questionnaire1.getId())) {
										getData();
										tbl_fragebogen.refresh();
										Notifications.create().title("Fragebogen löschen").text("Fragebogen \"" + questionnaire1.getName() + "\" wurde erfolgreich abgeschlossen.").show();
									} else {
										Notifications.create().title("Fragebogen löschen").text("Ein Fehler ist aufgetreten.").showError();
									}
								}
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
            }
        };

        delCol.setCellFactory(cellFactoryDel);
        tbl_fragebogen.getColumns().add(delCol);

	}
	
	private Optional<Pair<String, String>> getDatePickerDialog() {
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
	private void newFragebogen() {
		TextInputDialog dialog = new TextInputDialog("");
    	dialog.setTitle("Fragebogen erstellen");
    	dialog.setContentText("Name:");
    	DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getStylesheets().add(getURL("style/application.css").toExternalForm());
		
    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(name -> {
    		if(AdminService.createQuestionnaire(name) != -1) {
        		getData();
        		tbl_fragebogen.refresh();
        	}
    	});
	}
	
	@FXML
	private void logout() {
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
